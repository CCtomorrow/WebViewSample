package com.mmc.webview;

/**
 * <b>Project:</b> WebViewSample <br>
 * <b>Create Date:</b> 2018/3/2 <br>
 * <b>@author:</b> qy <br>
 * <b>Address:</b> qingyongai@gmail.com <br>
 * <b>Description:</b>  <br>
 */
public interface IJsCallJavaCallBackV2 {

    /**
     * 现在调用统一用这个方法，然后在这个方法里面做分发，比如要调用MMCOnlinePay
     *
     * @param data {
     *             "methodName": "MMCOnlinePay",
     *             "callBackID": "",
     *             "params": {
     *             "order_id": "CHFKHJL7908FG",
     *             "pay_point": "bazihehun"
     *             }
     *             }
     */
    void postMessage(String data);

}
