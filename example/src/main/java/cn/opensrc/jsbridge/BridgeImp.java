package cn.opensrc.jsbridge;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import cn.opensrc.brilib.IBridge;

/**
 * Author:       sharp
 * Created on:   15/12/2016 1:59 PM
 * Description:
 * Revisions:
 */

public class BridgeImp implements IBridge{

    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void log(String msg){
        Log.d("test",msg);
    }

    public static void sout(){
        System.out.println("hello javascript");
    }

}
