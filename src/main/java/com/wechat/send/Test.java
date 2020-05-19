package com.wechat.send;
import com.ultrapower.umap.entity.factor.WeChatMsgSend;

import java.io.IOException;
public class Test {
    public static void main(String[] args) {
        WeChatMsgSend swx = new WeChatMsgSend();
        try {
            String token = swx.getToken("wwdf8f60f186c3bdbf","I0DWzXr2ZkU8jCOhjy6TlxkPvJUndldKAw5qakEMIXI");
            System.out.println("获取到的token======>" + token);

            String postdata = swx.createpostdata("HuangJiXin",1, "text", 1000003, "content","黄记新发的消息通知");
            String resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE,(new WeChatUrlData()).getSendMessage_Url(), postdata, token);
            System.out.println("请求数据======>" + postdata);
            System.out.println("发送微信的响应数据======>" + resp);

            postdata = swx.createpostdata("ZhangZhicheng",1, "text", 1000003, "content","黄记新发的消息通知");
             resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE,(new WeChatUrlData()).getSendMessage_Url(), postdata, token);
            System.out.println("请求数据======>" + postdata);
            System.out.println("发送微信的响应数据======>" + resp);

            postdata = swx.createpostdata("XuYanhong1",1, "text", 1000003, "content","黄记新发的消息通知");
             resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE,(new WeChatUrlData()).getSendMessage_Url(), postdata, token);
            resp = swx.post("utf-8", WeChatMsgSend.CONTENT_TYPE,(new WeChatUrlData()).getSendMessage_Url(), postdata, token);
            System.out.println("请求数据======>" + postdata);
            System.out.println("发送微信的响应数据======>" + resp);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}