package com.accelerator.signbar.support.httpclient;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class HttpClientUtils {

    public static final String USER_AGNET_CHROME = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36";

    public static final String USER_AGNET_ANDROID = "Mozilla/5.0 (Linux; U; Android 4.1.1; ja-jp; Galaxy Nexus Build/JRO03H) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    public static String formatParams(Map<String, String> paramMap) {
        if (MapUtils.isEmpty(paramMap))
            return "";
        StringBuilder paramBuilder = new StringBuilder();
        Set<Map.Entry<String, String>> paramEntrySet = paramMap.entrySet();
        for (Map.Entry<String, String> paramEntry : paramEntrySet) {
            String key = paramEntry.getKey();
            if (StringUtils.isEmpty(key))
                continue;
            String value = paramEntry.getValue();
            paramBuilder.append(key);
            paramBuilder.append('=');
            paramBuilder.append(value);
            paramBuilder.append('&');
        }
        paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        return paramBuilder.toString();
    }

    public static String formatURL(String urlStr, Map<String, String> paramMap) {
        if (StringUtils.isBlank(urlStr))
            return "";
        String paramStr = formatParams(paramMap);
        StringBuilder urlBuilder = new StringBuilder(urlStr);
        switch (StringUtils.countMatches(urlStr, "?")) {
            case 0:
                urlBuilder.append('?');
                break;
            default:
                if (!urlStr.endsWith("&"))
                    urlBuilder.append('&');
                break;
        }
        urlBuilder.append(paramStr);
        return urlBuilder.toString();
    }

    public static List<Header> createDefaultHeaderList() {
        List<Header> defaultHeaderList = Lists.newArrayList();
        defaultHeaderList.add(new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml,application/javascript;q=0.9,image/webp,*/*;q=0.8"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.CACHE_CONTROL, "max-age=0"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));
        defaultHeaderList.add(new BasicHeader(HttpHeaders.USER_AGENT, USER_AGNET_ANDROID));
        return defaultHeaderList;
    }

    public static void setHeaderList(HttpRequest request, List<Header> headerList) {
        for (Header header : headerList)
            request.setHeader(header);
    }

    public static void addHeaderList(HttpRequest request, List<Header> headerList) {
        for (Header header : headerList)
            request.addHeader(header);
    }

    public static void setDefaultHeaderList(HttpRequest request) {
        List<Header> headers = createDefaultHeaderList();
        setHeaderList(request, headers);
    }

    public static void addDefaultHeaders(HttpRequest request) {
        List<Header> headers = createDefaultHeaderList();
        addHeaderList(request, headers);
    }

    public static void setPostParams(HttpPost postRequest, Map<String, String> params) {
        setPostParams(postRequest, params, Consts.UTF_8);
    }

    public static void setPostParams(HttpPost postRequest, Map<String, String> params, String charset) {
        setPostParams(postRequest, params, Charset.forName(charset));
    }

    public static void setPostParams(HttpPost postRequest, Map<String, String> params, Charset charset) {
        if (MapUtils.isEmpty(params))
            return;
        List<NameValuePair> nameValuePairList = Lists.newArrayList();
        for (Map.Entry<String, String> paramEntry : params.entrySet()) {
            String name = paramEntry.getKey();
            String value = paramEntry.getValue();
            NameValuePair nameValuePair = new BasicNameValuePair(name, value);
            nameValuePairList.add(nameValuePair);
        }
        HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairList, charset);
        postRequest.setEntity(httpEntity);
    }

    public static ResponseData executeRequest(HttpClient client, HttpRequestBase request) {
        ResponseData responseData = executeRequest(client, request, null);
        return responseData;
    }

    public static ResponseData executeRequest(HttpClient client, HttpRequestBase request, HttpContext context) {
        if (null == context)
            context = HttpClientContext.create();
        HttpResponse response = null;
        try {
            response = client.execute(request, context);
            ResponseData responseData = new ResponseData(response, context);
            return responseData;
        } catch (ClientProtocolException e) {
            StringBuilder message = new StringBuilder("执行请求：[");
            message.append(request);
            message.append("]；发生ClientProtocolException！");
            throw new RuntimeException(message.toString(), e);
        } catch (IOException e) {
            StringBuilder message = new StringBuilder("执行请求：[");
            message.append(request);
            message.append("]；发生IOException！");
            throw new RuntimeException(message.toString(), e);
        } finally {
            request.releaseConnection();
            org.apache.http.client.utils.HttpClientUtils.closeQuietly(response);
        }
    }

}
