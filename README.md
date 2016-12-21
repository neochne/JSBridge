## 功能
简单JSBridge，使用JSBridge实现android端与web端安全通信。
## 使用
### Gradle
```
compile 'cn.opensrc.brilib:jsbridge:1.0.0'
```
### Maven
```
<dependency>
  <groupId>cn.opensrc.brilib</groupId>
  <artifactId>jsbridge</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
### lvy
```
<dependency org='cn.opensrc.brilib' name='jsbridge' rev='1.0.0'>
  <artifact name='jsbridge' ext='pom' ></artifact>
</dependency>
```

## 代码示例
### Js调用Java
```
// 可以在 shouldOverrideUrlLoading() 或 onJsPrompt()方法或其它回调方法中执行
// web 端传来的js消息格式：jsbridge://className/methodName?{"param1":"value1"}
wv.loadUrl("file:///android_asset/test.html");
wv.getSettings().setJavaScriptEnabled(true);
wv.setWebChromeClient(new WebChromeClient(){
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

        result.confirm("");
        Uri uri = Uri.parse(message);

        String className = JSBridge.getClassName(uri);
        String methodName = JSBridge.getMethodName(uri);
        JSONObject JSONParams = JSBridge.getMethodJsonParams(uri);

        JSBridge.register(className,BridgeImp.class);

        if (JSONParams == null){
            JSBridge.callJavaMethod(className,methodName);
        }else {
            if ("showToast".equals(methodName))
                JSBridge.callJavaMethod(className,methodName,MainAty.this,JSONParams.optString("msg"));
            else if ("log".equals(methodName))
                JSBridge.callJavaMethod(className,methodName,JSONParams.optString("msg"));
        }

        return true;
    }
});
```
### Java调用Js
```
// 参数1为WebView
// 参数2为需要调用的js函数名
// 参数3为可变参，为js函数需要的参数，无参数可不传入即可
JSBridge.callJSFunc(wv,"func1","sharp");
```

## 使用注意
1. API >= 11 能使用本库.
2. web端传到Android端的消息格式为：jsbridge://className/methodName?{"param1":"value1"}
3. web端调用android端方法时，暴露的java类请先调用**JSBridge.register**方法进行注册。
4. 具体使用请参见**JSBridge**类。



## 联系方式
* Email:sharpchencn@gmail.com
* QQ交流群:469644904