package com.zte.xml.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.*;
import java.io.InterruptedIOException;
import java.net.URI;
import java.rmi.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @author Ray
 */
@Slf4j
public class SkipHttpsUtil {
	private static final Integer REQUEST_TIMEOUT = 6000;
	private static final Integer DEFAULT_MAX_PER_ROUTE = 20;
	private static final Integer MAX_TOTAL = 20;
	private static final Integer TIMEOUT = 6000;
	private static final Integer SOCKET_TIMEOUT = 6000;

	/**
	 * 绕过证书
	 */
	public static HttpClient wrapClient(String key) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
											   String arg1) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
											   String arg1) throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLConnectionSocketFactory sslSf = new SSLConnectionSocketFactory(
					ctx, NoopHostnameVerifier.INSTANCE);


			PlainConnectionSocketFactory plainSf = PlainConnectionSocketFactory.getSocketFactory();
			Registry registry = RegistryBuilder.create().register("http", plainSf).register("https", sslSf).build();
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
			Map<String, Integer> httpKeyConfig = httpKeyConfig(key);
			cm.setMaxTotal(httpKeyConfig.get("maxTotal"));
			cm.setDefaultMaxPerRoute(httpKeyConfig.get("defaultMaxPerRoute"));
			String host = getIp(URI.create(key));
			if (host == null) {
				log.error("获取http请求host失败，http地址：" + key);
			}

			cm.setMaxPerRoute(new HttpRoute(HttpHost.create(host)), 50);
			HttpRequestRetryHandler httpRequestRetryHandler = retryHandler();
			RequestConfig defaultRequestConfig = RequestConfig.custom()
					.setSocketTimeout(httpKeyConfig.get("socketTimeout"))
					.setConnectTimeout(httpKeyConfig.get("timeout"))
					.setConnectionRequestTimeout(httpKeyConfig.get("requestTimeout")).build();
			return HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
					.setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
		} catch (Exception e) {
			return HttpClients.createDefault();
		}
	}

	private static String getIp(URI uri) {
		URI effectiveUri;
		try {
			effectiveUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null,
					null, null);
			return effectiveUri.toString();
		} catch (Throwable arg2) {
			return null;
		}
	}

	private static HttpRequestRetryHandler retryHandler() {
		return (exception, executionCount, context) -> {
			if (executionCount >= 1) {
				log.error("已经重试了1次，放弃连接！");
				return false;
			} else if (exception instanceof NoHttpResponseException) {
				log.error("服务器丢掉了连接，请重试！");
				return true;
			} else if (exception instanceof SSLHandshakeException) {
				log.error("SSL握手异常！");
				return false;
			} else if (exception instanceof InterruptedIOException) {
				log.error("连接异常中断");
				return false;
			} else if (exception instanceof UnknownHostException) {
				log.error("目标服务器不可达");
				return false;
			} else if (exception instanceof SSLException) {
				log.error("ssl握手异常");
				return false;
			} else {
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				return !(request instanceof HttpEntityEnclosingRequest);
			}
		};
	}

	private static Map<String, Integer> httpKeyConfig(String key) {
		Map<String, Integer> keyConfig = new HashMap<>();
		keyConfig.put("maxTotal", 200);
		keyConfig.put("defaultMaxPerRoute", 20);
		keyConfig.put("timeout", 6000);
		keyConfig.put("requestTimeout", 6000);
		keyConfig.put("socketTimeout", 6000);
		try {
			keyConfig.put("maxTotal", MAX_TOTAL);
		} catch (Exception arg7) {
			log.warn(key + ".maxTotal配置未找到，使用默认值200");
		}

		try {
			keyConfig.put("defaultMaxPerRoute", DEFAULT_MAX_PER_ROUTE);
		} catch (Exception arg6) {
			log.warn(key + ".defaultMaxPerRoute配置未找到，使用默认值20");
		}

		try {
			keyConfig.put("timeout", TIMEOUT);
			keyConfig.put("requestTimeout", REQUEST_TIMEOUT);
			keyConfig.put("socketTimeout", SOCKET_TIMEOUT);
		} catch (Exception arg5) {
			log.warn(key + ".timeout配置未找到，使用默认值6000");
		}

		try {
			keyConfig.put("requestTimeout", REQUEST_TIMEOUT);
		} catch (Exception arg4) {
			log.warn(key + ".requestTimeout配置未找到，使用默认值6000");
		}

		try {
			keyConfig.put("socketTimeout", SOCKET_TIMEOUT);
		} catch (Exception arg3) {
			log.warn(key + ".socketTimeout配置未找到，使用默认值6000");
		}
		return keyConfig;
	}

	/**
	 * 绕过验证
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static SSLContext createIgnoreVerifySsl() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("TLSV1.2");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}
}