package com.mmc.webview;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * <b>Project:</b> WebViewSample <br>
 * <b>Create Date:</b> 2018/3/2 <br>
 * <b>@author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 实现类 <br>
 */
public class JsCallJavaCallBackImpl implements IJsCallJavaCallBack, IJsCallJavaCallBackV2 {

    private static final String TAG = "JsCallJavaCallBackImpl";

    private Activity mActivity;

    public JsCallJavaCallBackImpl(Activity activity) {
        this.mActivity = activity;
    }

    private void showDialog(CharSequence msg) {
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage(msg)
                .create();
        dialog.show();
    }

    @Override
    public String getUserInfo() {
        showDialog("getUserInfo");
        Log.i(TAG, "getUserInfo");
        return "{\n" +
                "     *             \"methodName\": \"MMCOnlinePay\",\n" +
                "     *             \"callBackID\": \"\",\n" +
                "     *             \"params\": {\n" +
                "     *             \"order_id\": \"CHFKHJL7908FG\",\n" +
                "     *             \"pay_point\": \"bazihehun\"\n" +
                "     *             }\n" +
                "     *             }";
    }

    @Override
    public void MMCLogin() {
        showDialog("MMCLogin");
        Log.i(TAG, "MMCLogin");
    }

    @Override
    public void MMCOnlinePay(String data) {
        showDialog("MMCOnlinePay");
        Log.i(TAG, "MMCOnlinePay");
    }

    @Override
    public void postMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String method = object.getString("methodName");
            String params = object.optString("params");
            String callback = object.optString("callBackID");
            // 调用当前类的其他方法
            Method targetMethod = null;
            boolean hasParams = !TextUtils.isEmpty(params);
            if (hasParams) {
                targetMethod = getClass().getMethod(method, String.class);
            } else {
                targetMethod = getClass().getMethod(method);
            }
            if (targetMethod != null) {
                targetMethod.setAccessible(true);
                if (hasParams) {
                    targetMethod.invoke(this, params);
                } else {
                    targetMethod.invoke(this, (Object[]) null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
