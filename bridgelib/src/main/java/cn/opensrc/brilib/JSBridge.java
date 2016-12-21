package cn.opensrc.brilib;

import android.net.Uri;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:       sharp
 * Created on:   14/12/2016 3:44 PM
 * Description:  Android 与 前端通信桥梁，通信操作都通过此类实现
 * Revisions:
 */

public class JSBridge {

    // 缓存暴露类的所有方法
    private static Map<String, Map<String, Method>> exposedMethods = new HashMap<>();

    /**
     * 调用JavaScript函数
     *
     * @param webView current WebView
     * @param func target JavaScript function
     * @param params the target function parameters
     */
    public static void callJSFunc(WebView webView, String func, String... params) {

        if (webView == null || func == null || "".equals(func) || params == null)
            throw new RuntimeException("callJSFunc method exist illegal parameter");

        StringBuilder sb = new StringBuilder("javascript:" + func + "(");
        if (params.length > 0) {
            for (String param : params) {
                sb.append("\'");
                sb.append(param);
                sb.append("\'");
                sb.append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), "");
        }
        sb.append(")");
        webView.loadUrl(sb.toString());
    }

    /**
     * 调用Java方法
     * web端传来的消息格式：jsbridge://className/methodName?{"param1":"value1","param2":"value2"}
     * web端参数定义格式：var msg = "{\"msg\":\"msg from javascript\"}"
     *
     * @param className the register class name
     * @param methodName target method
     * @param params the method args,if this parameter is not passed,its length is 0，not null
     * @return method return value
     */
    public static Object callJavaMethod(String className, String methodName, Object... params) {

        if (className == null || "".equals(className) || methodName == null || "".equals(methodName))
            throw new RuntimeException("callJavaMethod method exist illegal parameter");
        if (!exposedMethods.containsKey(className))
            throw new RuntimeException(className + " class not register");
        if (!exposedMethods.get(className).containsKey(methodName))
            throw new RuntimeException(methodName + "the invoked method dose not exist");
        Method method = exposedMethods.get(className).get(methodName);

        try {
            return method.invoke(null, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 注册需要显露给web端的Java类，获取类中所有方法并缓存
     *
     * @param className the class need exposed
     * @param clz the exposed class Class instance
     */
    public static void register(String className, Class<? extends IBridge> clz) {

        if (className == null || "".equals(className) || clz == null)
            throw new RuntimeException("register method exist illegal parameter");
        if (!exposedMethods.containsKey(className)) {
            Map<String, Method> methods = new HashMap<>();
            for (Method method : clz.getMethods()) {
                methods.put(method.getName(), method);
            }
            exposedMethods.put(className, methods);
        }

    }

    /**
     * 获取 String strJs = jsbridge://className/methodName?{"param1":"value1"} 中的 className
     *
     * @param uri Uri uri = Uri.parse(strJs)
     * @return the className in strJs
     */
    public static String getClassName(Uri uri){
        if (uri == null)
            throw new RuntimeException("getClassName method parameter is null");
        String className = uri.getHost();
        return className == null || "".equals(className) ? "" : className;
    }

    /**
     * 获取 String strJs = jsbridge://className/methodName?{"param1":"value1"} 中的 methodName
     *
     * @param uri Uri uri = Uri.parse(strJs)
     * @return the methodName in strJs
     */
    public static String getMethodName(Uri uri){
        if (uri == null)
            throw new RuntimeException("getMethodName method parameter is null");
        String methodName = uri.getPath().replace("/","");
        return methodName == null || "".equals(methodName) ? "" : methodName;
    }

    /**
     * 获取 String strJs = jsbridge://className/methodName?{"param1":"value1"} 中的 {"param1":"value1"}
     *
     * @param uri Uri uri = Uri.parse(strJs)
     * @return the Json parameter in strJs
     */
    public static JSONObject getMethodJsonParams(Uri uri){
        if (uri == null)
            throw new RuntimeException("getMethodJsonParams method parameter is null");
        String queryStrJSON = uri.getQuery();
        if (queryStrJSON == null || "".equals(queryStrJSON) || "{}".equals(queryStrJSON))
            return null;
        try {
            return new JSONObject(queryStrJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
