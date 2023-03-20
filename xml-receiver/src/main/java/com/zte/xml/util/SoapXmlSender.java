package com.zte.xml.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

/**
 * @author Ray
 */
@Slf4j
public class SoapXmlSender {

    public static void sendSoapXml(String url, String headXml) {
        CloseableHttpResponse response;
        CloseableHttpClient httpClient = (CloseableHttpClient) SkipHttpsUtil.wrapClient(url);
        try {
            URIBuilder uri = new URIBuilder(url);
            URI fullUri = uri.build();
            HttpPost httpPost = new HttpPost(fullUri);
            HttpEntity entity = new StringEntity(headXml);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            //设置超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(20000)
                    .setSocketTimeout(20000).setConnectTimeout(20000).build();
            httpPost.setConfig(requestConfig);
            // 发送请求
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseStr = EntityUtils.toString(responseEntity);
            log.info(StringUtils.substringBetween(responseStr, "<soap:Body>", "</soap:Body>"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
