package ru.prolib.aquila.web.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

@Deprecated
public class HttpClientFactory {
	public static final int DEFAULT_TIMEOUT = 180000;

	public static CloseableHttpClient createDefaultClient() {
		return createDefaultClient(DEFAULT_TIMEOUT);
	}
	
	public static CloseableHttpClient createDefaultClient(int timeout) {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setValidateAfterInactivity(timeout);
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout)
				.build();
		return HttpClients.custom()
				.setDefaultRequestConfig(defaultRequestConfig)
				.setConnectionManager(connManager)
				.build();		
	}

}
