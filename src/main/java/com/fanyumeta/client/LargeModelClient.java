package com.fanyumeta.client;

import com.alibaba.fastjson.JSONObject;
import com.fanyumeta.socket.HttpClient;
import com.fanyumeta.socket.exception.HttpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 大模型客户端
 */
@Component
public class LargeModelClient {

    @Value("${fan-yu.large-model.url}")
    private String url;

    /**
     * 发送消息
     *
     * @param message 请求消息
     * @return 响应数据
     * @throws HttpException 当调用大模型出错时，会抛出此异常
     */
    public LargeModelResponse sendMessage(String message) throws HttpException {
        String data = "{\"messages\":[{\"role\":\"user\",\"content\":\"" + message + "\"}]}";
        JSONObject param = JSONObject.parseObject(data);
        return HttpClient.post(this.url, param, LargeModelResponse.class);
    }
}
