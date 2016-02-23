package com.accelerator.signbar.support.httpclient;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;

public class ResponseData implements Serializable {

    private static final long serialVersionUID = -1285292747469978180L;

    private int statusCode;

    private String mimeType;

    private Charset charset;

    private List<Header> headerList;

    private byte[] contentBytes;

    private List<Cookie> cookieList;

    public ResponseData(HttpResponse response) {
        this(response, null);
    }

    public ResponseData(HttpResponse response, HttpContext context) {
        Asserts.notNull(response, "response");
        // 获取请求状态
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        setStatusCode(statusCode);
        // 获取头信息
        Header[] headerArr = response.getAllHeaders();
        List<Header> headerList = Arrays.asList(headerArr);
        setHeaderList(headerList);
        // 获取实体
        HttpEntity entity = response.getEntity();
        // 获取内容
        try {
            byte[] contentBytes = EntityUtils.toByteArray(entity);
            setContentBytes(contentBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 获取ContentType并分解为相关信息
        ContentType contentType = ContentType.get(entity);
        String mimeType = contentType.getMimeType();
        setMimeType(mimeType);
        Charset charset = contentType.getCharset();
        setCharset(charset);
        // 获取cookie信息
        if (context != null) {
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            CookieStore cookieStore = clientContext.getCookieStore();
            if (null != cookieStore) {
                List<Cookie> cookies = cookieStore.getCookies();
                setCookieList(cookies);
            }
        }
    }

    public final int getStatusCode() {
        return statusCode;
    }

    public final void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public final String getMimeType() {
        return mimeType;
    }

    public final void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public final Charset getCharset() {
        return charset;
    }

    public final void setCharset(Charset charset) {
        this.charset = charset;
    }

    public final List<Header> getHeaderList() {
        return headerList;
    }

    public final void setHeaderList(List<Header> headerList) {
        this.headerList = headerList;
    }

    public final byte[] getContentBytes() {
        return contentBytes;
    }

    public final void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }

    public final List<Cookie> getCookieList() {
        return cookieList;
    }

    public final void setCookieList(List<Cookie> cookieList) {
        this.cookieList = cookieList;
    }

    public final String getContentStr() {
        if (charset == null)
            return getContentStr(Consts.UTF_8);
        else
            return getContentStr(charset);
    }

    public final String getContentStr(String charsetName) {
        Charset charset = null;
        if (Charset.isSupported(charsetName))
            charset = Charset.forName(charsetName);
        else
            charset = null;
        String contentStr = getContentStr(charset);
        return contentStr;
    }

    public final String getContentStr(Charset charset) {
        if (charset == null)
            return new String(contentBytes, Consts.UTF_8);
        else
            return new String(contentBytes, charset);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((charset == null) ? 0 : charset.hashCode());
        result = prime * result + Arrays.hashCode(contentBytes);
        result = prime * result + ((cookieList == null) ? 0 : cookieList.hashCode());
        result = prime * result + ((headerList == null) ? 0 : headerList.hashCode());
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + statusCode;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ResponseData))
            return false;
        ResponseData other = (ResponseData) obj;
        if (charset == null) {
            if (other.charset != null)
                return false;
        } else if (!charset.equals(other.charset))
            return false;
        if (!Arrays.equals(contentBytes, other.contentBytes))
            return false;
        if (cookieList == null) {
            if (other.cookieList != null)
                return false;
        } else if (!cookieList.equals(other.cookieList))
            return false;
        if (headerList == null) {
            if (other.headerList != null)
                return false;
        } else if (!headerList.equals(other.headerList))
            return false;
        if (mimeType == null) {
            if (other.mimeType != null)
                return false;
        } else if (!mimeType.equals(other.mimeType))
            return false;
        if (statusCode != other.statusCode)
            return false;
        return true;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("ResponseData [statusCode=%s, mimeType=%s, charset=%s, headerList=%s, cookieList=%s, getContentStr()=%s]", statusCode, mimeType, charset, headerList != null ? headerList.subList(0, Math.min(headerList.size(), maxLen)) : null, cookieList != null ? cookieList.subList(0, Math.min(cookieList.size(), maxLen)) : null, getContentStr());
    }

}
