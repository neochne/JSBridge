package cn.opensrc.jsbridge;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.opensrc.library.JSBridge;

/**
 * Author:       sharp
 * Created on:   14/12/2016 3:36 PM
 * Description:
 * Revisions:
 */

public class MainAty extends AppCompatActivity{

    private WebView wv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
        wv = (WebView) findViewById(R.id.wv);
        init();
    }

    private void init(){
        wv.loadUrl("file:///android_asset/test.html");
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

                System.out.println("message:"+message);
                result.confirm("");
                Uri uri = Uri.parse(message);

                String className = JSBridge.getClassName(uri);
                String methodName = JSBridge.getMethodName(uri);
                JSONObject JSONParams = JSBridge.getMethodJsonParams(uri);

                JSBridge.register(className,BridgeImp.class);

                System.out.println("className="+className+" methodName="+methodName+" JSONParams="+JSONParams);

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
    }

    public void callJsFunc(View v) throws JSONException {
        JSBridge.callJSFunc(wv,"func1","sharp");
    }



}
