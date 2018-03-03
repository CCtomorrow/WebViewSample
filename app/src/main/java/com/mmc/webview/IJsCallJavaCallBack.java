package com.mmc.webview;

/**
 * <b>Project:</b> WebViewSample <br>
 * <b>Create Date:</b> 2018/3/2 <br>
 * <b>@author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b> 定义需要的方法 <br>
 */
public interface IJsCallJavaCallBack {

    String getUserInfo();

    void MMCLogin();

    void MMCOnlinePay(String data);

}
