package com.dianping.swallow.web.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.VersionInfo;
import org.codehaus.jettison.json.JSONObject;
import org.mortbay.jetty.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.model.alarm.ResultType;
import com.dianping.swallow.web.service.HttpService;

/**
 * @author qiyin
 */
@Service("httpService")
public class HttpServiceImpl implements HttpService {

    private static final Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);

    private static final String UTF_8 = "UTF-8";

    private static final int TIMEOUT = 2000;

    private static final int CONNECTION_TIMEOUT = 2000;

    private static final int SOCKET_TIMEOUT = 1000;

    private HttpClient httpClient;

    public HttpServiceImpl() {
        createHttpClient();
    }

    private void createHttpClient() {
        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        final VersionInfo vi = VersionInfo.loadVersionInfo("org.apache.http.client", getClass().getClassLoader());
        final String release = (vi != null) ? vi.getRelease() : VersionInfo.UNAVAILABLE;
        HttpProtocolParams.setUserAgent(params, "Apache-HttpClient/" + release + " (java 1.5)");

        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(CommonUtils.getCpuCount() * 2));
        // 等待获取链接时间
        ConnManagerParams.setTimeout(params, TIMEOUT);
        // 链接超时时间
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        // 读取超时时间
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(connectionManager, params);
    }

    @Override
    public HttpResult httpPost(String url, List<NameValuePair> params) {
        HttpPost httpPost = new HttpPost(url);
        HttpResult result = new HttpResult();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, UTF_8));
        } catch (UnsupportedEncodingException e) {
            logger.error("http post param encoded failed. ", e);
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.ORDINAL_200_OK) {
                result.setResponseBody(EntityUtils.toString(response.getEntity()));
                result.setResultType(ResultType.SUCCESS);
                result.setSuccess(true);
            } else {
                handleException(httpPost, result, ResultType.FAILED);
                logger.error("http post request failed. url = {}", url);
            }
        } catch (UnknownHostException e) {
            handleException(httpPost, result, ResultType.FAILED_HOST_UNKNOWN);
            logger.error("http post request failed. url = {}", url, e);
        } catch (ConnectTimeoutException e) {
            handleException(httpPost, result, ResultType.FAILED_CONNECTION_TIMEOUT);
            logger.error("http post request failed. url = {}", url, e);
        } catch (ConnectException e) {
            handleException(httpPost, result, ResultType.FAILED_CONNECT);
            logger.error("http post request failed. url = {}", url, e);
        } catch (SocketTimeoutException e) {
            handleException(httpPost, result, ResultType.FAILED_SOCKET_TIMEOUT);
            logger.error("http post request failed. url = {}", url, e);
        } catch (IOException e) {
            handleException(httpPost, result, ResultType.FAILED);
            logger.error("http post request failed. url = {}", url, e);
        } catch (ParseException e) {
            handleException(httpPost, result, ResultType.FAILED);
            logger.error("http get request failed. url = {}", url, e);
        }
        return result;
    }

    @Override
    public HttpResult httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        HttpResult result = new HttpResult();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.ORDINAL_200_OK) {
                result.setResponseBody(EntityUtils.toString(response.getEntity()));
                result.setResultType(ResultType.SUCCESS);
                result.setSuccess(true);
            } else {
                handleException(httpGet, result, ResultType.FAILED);
            }
        } catch (UnknownHostException e) {
            handleException(httpGet, result, ResultType.FAILED_HOST_UNKNOWN);
            logger.error("http get request failed. url = {}", url, e);
        } catch (ConnectTimeoutException e) {
            handleException(httpGet, result, ResultType.FAILED_CONNECTION_TIMEOUT);
            logger.error("http get request failed. url = {}", url, e);
        } catch (ConnectException e) {
            handleException(httpGet, result, ResultType.FAILED_CONNECT);
            logger.error("http get request failed. url = {}", url, e);
        } catch (SocketTimeoutException e) {
            handleException(httpGet, result, ResultType.FAILED_SOCKET_TIMEOUT);
            logger.error("http get request failed. url = {}", url, e);
        } catch (IOException e) {
            handleException(httpGet, result, ResultType.FAILED);
            logger.error("http get request failed. url = {}", url, e);
        } catch (ParseException e) {
            handleException(httpGet, result, ResultType.FAILED);
            logger.error("http get request failed. url = {}", url, e);
        }
        return result;
    }

    @Override
    public HttpResult httpPost(String url, JSONObject jsonObject) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.addHeader("Accept", "application/json");
        HttpResult result = new HttpResult();
        httpPost.setEntity(new StringEntity(jsonObject.toString(), UTF_8));
        try {
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.ORDINAL_200_OK) {
                result.setResponseBody(EntityUtils.toString(response.getEntity()));
                result.setResultType(ResultType.SUCCESS);
                result.setSuccess(true);
            } else {
                handleException(httpPost, result, ResultType.FAILED);
                logger.error("http post request failed. url = {}", url);
            }
        } catch (UnknownHostException e) {
            handleException(httpPost, result, ResultType.FAILED_HOST_UNKNOWN);
            logger.error("http post request failed. url = {}", url, e);
        } catch (ConnectTimeoutException e) {
            handleException(httpPost, result, ResultType.FAILED_CONNECTION_TIMEOUT);
            logger.error("http post request failed. url = {}", url, e);
        } catch (ConnectException e) {
            handleException(httpPost, result, ResultType.FAILED_CONNECT);
            logger.error("http post request failed. url = {}", url, e);
        } catch (SocketTimeoutException e) {
            handleException(httpPost, result, ResultType.FAILED_SOCKET_TIMEOUT);
            logger.error("http post request failed. url = {}", url, e);
        } catch (IOException e) {
            handleException(httpPost, result, ResultType.FAILED);
            logger.error("http post request failed. url = {}", url, e);
        } catch (ParseException e) {
            handleException(httpPost, result, ResultType.FAILED);
            logger.error("http get request failed. url = {}", url, e);
        }
        return result;
    }

    private void handleException(HttpRequestBase request, HttpResult result, ResultType resultType) {
        result.setSuccess(false);
        result.setResultType(resultType);
        request.abort();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
