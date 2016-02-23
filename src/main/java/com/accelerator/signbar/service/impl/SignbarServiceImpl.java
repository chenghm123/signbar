package com.accelerator.signbar.service.impl;

import com.accelerator.signbar.service.SignbarService;
import com.accelerator.signbar.support.httpclient.HttpClientUtils;
import com.accelerator.signbar.support.httpclient.ResponseData;
import com.accelerator.signbar.util.MailUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;


@Service("signbarService")
public class SignbarServiceImpl implements SignbarService, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private HttpClient httpClient;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${baidu.cookies}")
    private String cookie;

    @Value("${baidu.likeBarUrlPtn}")
    private String likeBarUrlPtn;

    @Value("${baidu.tbsUrl}")
    private String tbsUrl;

    @Value("${baidu.signbarUrl}")
    private String signbarUrl;

    private HttpContext httpContext;

    @Override
    public Map<String, String> getBars() {
        Map<String, String> barsMap = Maps.newHashMap();
        int pn = 1;
        try {
            while (true) {
                String likeBarUrl = String.format(likeBarUrlPtn, pn);
                Connection conn = Jsoup.connect(likeBarUrl);
                conn.cookie("BDUSS", cookie);
                Document doc = conn.get();
                Elements elements = doc.select("span[balvid][balvname]");
                if (elements.isEmpty())
                    break;
                for (Element element : elements) {
                    String balvid = element.attr("balvid");
                    String balvname = element.attr("balvname");
                    String title = URLDecoder.decode(balvname, "GBK");
                    barsMap.put(balvid, title);
                }
                pn++;
            }
        } catch (IOException e) {
            logger.error("Error Method getBarInfos()", e);
            barsMap = Collections.emptyMap();
        }
        return barsMap;
    }

    @Override
    public String getTbs() {
        HttpGet getRequest = new HttpGet(tbsUrl);
        HttpClientUtils.addDefaultHeaders(getRequest);
        ResponseData responseData = HttpClientUtils.executeRequest(
                httpClient, getRequest, httpContext);
        String contentStr = responseData.getContentStr();
        try {
            Map<String, Object> responseJson = objectMapper.readValue(contentStr, Map.class);
            String isLoginStr = Objects.toString(responseJson.get("is_login"));
            int isLoginInt = NumberUtils.toInt(isLoginStr);
            if (isLoginInt != 1) {
                MailUtils.sendCookieExpired();
                return null;
            }
            String tbs = Objects.toString(responseJson.get("tbs"));
            return tbs;
        } catch (IOException e) {
            logger.error("转换JSON发生未知异常！", e);
            return null;
        }
    }

    @Override
    public boolean doSign(Map.Entry<String, String> bar) {

        String tbs = getTbs();

        if (StringUtils.isEmpty(tbs))
            return false;

        Map<String, String> params = Maps.newLinkedHashMap();
        params.put("BDUSS", cookie);
        params.put("_client_id",
                "03-00-DA-59-05-00-72-96-06-00-01-00-04-00-4C-43-01-00-34-F4-02-00-BC-25-09-00-4E-36");
        params.put("_client_type", "4");
        params.put("_client_version", "1.2.1.17");
        params.put("_phone_imei", "540b43b59d21b7a4824e1fd31b08e9a6");
        params.put("fid", bar.getKey());
        params.put("kw", bar.getValue());
        params.put("net_type", "3");
        params.put("tbs", tbs);

        StringBuilder signBuilder = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            String key = param.getKey();
            Object value = param.getValue();
            signBuilder.append(key + "=" + value);
        }
        signBuilder.append("tiebaclient!!!");
        String sign = signBuilder.toString();
        String signMd5 = DigestUtils.md5Hex(sign);

        params.put("sign", signMd5.toUpperCase());

        // Do Request
        HttpPost postRequest = new HttpPost(signbarUrl);
        HttpClientUtils.addDefaultHeaders(postRequest);
        HttpClientUtils.setPostParams(postRequest, params);
        ResponseData responseData = HttpClientUtils.executeRequest(
                httpClient, postRequest, httpContext);

        if (responseData.getStatusCode() != HttpStatus.SC_OK) {
            return false;
        }

        String contentStr = responseData.getContentStr();
        try {
            // 解析返回信息
            Map<String, Object> responseJson =
                    objectMapper.readValue(contentStr, Map.class);
            String codeStr = Objects.toString(responseJson.get("error_code"));
            int code = NumberUtils.toInt(codeStr);

            if (code == 0 || code == 160002) {
                return true;
            }

        } catch (IOException e) {
            logger.error("转换JSON发生未知异常！", e);
        }

        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        BasicClientCookie clientCookie = new BasicClientCookie("BDUSS", cookie);
        clientCookie.setDomain("baidu.com");
        clientCookie.setPath("/");
        clientCookie.setAttribute("domain", "baidu.com");
        clientCookie.setAttribute("path", "/");
        clientCookie.setAttribute("httponly", null);

        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(clientCookie);

        HttpClientContext httpClientContext = new HttpClientContext();
        httpClientContext.setCookieStore(cookieStore);

        httpContext = httpClientContext;
    }
}
