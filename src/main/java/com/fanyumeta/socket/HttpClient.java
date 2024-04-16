package com.fanyumeta.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fanyumeta.socket.exception.HttpException;
import okhttp3.*;

import java.io.IOException;

/**
 * Http 请求客户端
 */
public class HttpClient {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    /**
     * 发送 GET 请求
     *
     * <p>请求参数需要拼接到请求 url 中，响应数据已字符串形式返回</p>
     * @param url 请求 url
     * @return 响应数据字符串
     * @throws HttpException 请求处理出错时，会抛出此异常
     */
    public static String get(String url) throws HttpException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return httpRequest(request);
    }

    /**
     * 发送 GET 请求
     *
     * <p>将响应数据转换为 responseDataType 类型</p>
     *
     * @param url 请求 url
     * @param responseDataType 响应数据类型
     * @return 接收到数据
     * @param <T> 响应数据类型
     * @throws HttpException 请求处理出错时，会抛出此异常
     */
    public static <T> T get(String url, Class<T> responseDataType) throws HttpException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return JSON.parseObject(httpRequest(request), responseDataType);
    }

    /**
     * 发送 GET 请求
     *
     * <p>将 json 请求参数拼接到请求 url 中，将响应数据转换为 responseDataType 类型</p>
     *
     * @param url 请求 url
     * @param params 请求 json 参数
     * @param responseDataType 响应数据类型
     * @return 接收到数据
     * @param <T> 响应数据类型
     * @throws HttpException 请求处理出错时，会抛出此异常
     */
    public static <T> T get(String url, JSONObject params, Class<T> responseDataType) throws HttpException {
        // 将参数拼接到 url 上
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (String key : params.keySet()) {
            urlBuilder.addQueryParameter(key, params.getString(key));
        }

        // 构建 request
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();
        return JSON.parseObject(httpRequest(request), responseDataType);
    }

    /**
     * 发送 POST 请求
     *
     * <p>将 json 请求参数放入 request body 中，将响应数据转换为 responseDataType 类型</p>
     *
     * @param url 请求 url
     * @param params 请求 json 参数
     * @param responseDataType 响应数据类型
     * @return 接收到数据
     * @param <T> 响应数据类型
     * @throws HttpException 请求处理出错时，会抛出此异常
     */
    public static <T> T post(String url, JSONObject params, Class<T> responseDataType) throws HttpException {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, params.toJSONString());

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        return JSON.parseObject(httpRequest(request), responseDataType);
    }

    /**
     * 以 post 方式提交表单
     *
     * <p>将 json 请求参数放入 request form 中，将响应数据转换为 responseDataType 类型</p>
     *
     * @param url 请求 url
     * @param params 请求 json 参数
     * @param responseDataType 响应数据类型
     * @return 接收到数据
     * @param <T> 响应数据类型
     * @throws HttpException 请求处理出错时，会抛出此异常
     */
    public static <T> T postForm(String url, JSONObject params, Class<T> responseDataType) throws HttpException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (String key : params.keySet()) {
            formBodyBuilder.add(key, params.getString(key));
        }
        RequestBody requestBody = formBodyBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return JSON.parseObject(httpRequest(request), responseDataType);
    }

    /**
     * 发送 http 请求
     *
     * @param request 请求
     * @return 响应数据字符串
     * @throws HttpException 请求处理出错时，会抛出此异常
     */
    private static String httpRequest(Request request) throws HttpException {
        String content = null;
        try {
            Response response = OK_HTTP_CLIENT.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new HttpException("发送 http 请求出错");
            }
            ResponseBody body = response.body();
            content = body != null ? body.string() : null;
        } catch (IOException e) {
            throw new HttpException("发送 http 请求出错", e);
        }
        return content;
    }
}
