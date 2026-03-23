package org.jeecg.modules.instagram.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.api.vo.HttpRequestResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 工具类：直接返回接口原始 JSON 内容（JSONObject）
 * - 成功且响应为 JSON → 返回 JSONObject
 * - 成功但响应非 JSON → 返回 null
 * - 请求失败（网络/HTTP 错误）→ 返回 null
 */
@Slf4j
public class ToolHttpClient {

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER =
            new PoolingHttpClientConnectionManager();

    static {
        CONNECTION_MANAGER.setMaxTotal(200);
        CONNECTION_MANAGER.setDefaultMaxPerRoute(20);
    }

    private static final CloseableHttpClient HTTP_CLIENT = HttpClientBuilder.create()
            .setConnectionManager(CONNECTION_MANAGER)
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(10_000)
                    .setSocketTimeout(30_000)
                    .setConnectionRequestTimeout(5_000)
                    .build())
            .build();

    private ToolHttpClient() {}

    // ==================== GET ====================

    public static Object get(String url) {
        return get(url, null);
    }

    public static Object get(String url, Map<String, String> headers) {
        HttpGet request = new HttpGet(url);
        setHeaders(request, headers);
        return executeAndParseJson(request);
    }

    // ==================== POST ====================

    public static Object postJson(String url, String jsonBody) {
        return postJson(url, jsonBody, null);
    }

    public static Object postJson(String url, String jsonBody, Map<String, String> headers) {
        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        setHeaders(request, headers);
        request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        return executeAndParseJson(request);
    }

    // ==================== PUT ====================

    public static Object putJson(String url, String jsonBody) {
        return putJson(url, jsonBody, null);
    }

    public static Object putJson(String url, String jsonBody, Map<String, String> headers) {
        HttpPut request = new HttpPut(url);
        request.setHeader("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        setHeaders(request, headers);
        request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        return executeAndParseJson(request);
    }

    // ==================== DELETE ====================

    public static Object delete(String url) {
        return delete(url, null);
    }

    public static Object delete(String url, Map<String, String> headers) {
        HttpDelete request = new HttpDelete(url);
        setHeaders(request, headers);
        return executeAndParseJson(request);
    }

    // ==================== 内部执行 ====================

    private static void setHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(request::setHeader);
        }
    }

    private static Object executeAndParseJson(HttpRequestBase request) {
        try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.warn("HTTP 请求失败 [{}]: {}", statusCode, request.getURI());
                return null;
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }

            String body = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            if (body == null || body.trim().isEmpty()) {
                return null;
            }

            // 尝试解析为 JSONObject
            return JSONObject.parseObject(body);

        } catch (IOException e) {
            log.error("HTTP 请求异常: {}", request.getURI(), e);
            return null;
        } catch (Exception e) {
            log.error("JSON 解析失败: {}", request.getURI(), e);
            return null;
        }
    }

    public static void shutdown() {
        try {
            HTTP_CLIENT.close();
            CONNECTION_MANAGER.close();
        } catch (IOException e) {
            log.error("关闭 HttpClient 失败", e);
        }
    }

    /**
     * 字符编码
     */
    public final static String encoding = "UTF-8";

    /**
     * 发送http get请求
     */
    public static JSONObject httpGet(String url) {
        System.out.println("url : " + url);
        JSONObject jsonObject = new JSONObject();
        // 发送GET请求
        try {
            String response = ToolHttpClient.httpGet(url, new HashMap<>(), null);
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 发送http get请求
     */
    public static String httpGet(String url, Map<String, String> headers, String encode) {
        HttpRequestResult httpRequestResult = new HttpRequestResult();
        if (encode == null) {
            encode = "utf-8";
        }
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        //设置header
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity, encoding);
                    return result;
                }
            } else {
                System.out.println("请求未成功响应");
            }

        } catch (ConnectTimeoutException e) {
            System.out.println("请求超时");

        } catch (ClientProtocolException e) {
            System.out.println("请求失败");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {  //关闭连接、释放资源
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}