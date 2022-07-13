package com.tung.marc.util;

import com.alibaba.fastjson.JSON;
import com.tung.marc.dto.ResultDTO;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClientUtil {
    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String post(String url, Map<String, String> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost =   new HttpPost(url);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (null != params && params.size() > 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (key == null || value == null) {
                        continue;
                    }
                    NameValuePair pair = new BasicNameValuePair(key, value);
                    nameValuePairs.add(pair);
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            // 执行请求
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                result = EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
                logger.info("执行请求完毕：" + result);
                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("请求通信[\" + reqURL + \"]时网络时，关闭异常,堆栈轨迹如下", e);
            }
            httpPost.releaseConnection();
        }
        return result;
    }

    public static String doPostObject(String url, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = "";

        try {
            HttpPost post = new HttpPost(url);
            // 创建参数列表
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : params.keySet()) {
                    paramList.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
                }
                // 模拟表单
                post.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
                post.setHeader("Content-type", "application/x-www-form-urlencoded");
            }
            logger.info("接口请求：URL：{}，params:{}",url,params.toString());
            response = httpClient.execute(post);
            result = new BasicResponseHandler().handleResponse(response);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return result;
    }

    public static String get(String url, Map<String, Object> params) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 创建uri
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    uriBuilder.addParameter(key, String.valueOf(params.get(key)));
                }
            }
            URI uri = uriBuilder.build();
            // 创建http GET请求
            HttpGet get = new HttpGet(uri);
            // 执行请求
            response = httpclient.execute(get);
            result = new BasicResponseHandler().handleResponse(response);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return result;
    }


    private static ResultDTO getResultDTO(String url, Map<String, Object> params) throws Exception {
        return JSON.parseObject(get(url, params), ResultDTO.class);
    }

    public static  <T> List<T> getResultArray(String url, Map<String, Object> params, Class<T> clazz) throws Exception {
        ResultDTO result = getResultDTO(url, params);
        return JSON.parseArray(result.getData(), clazz);
    }
}
