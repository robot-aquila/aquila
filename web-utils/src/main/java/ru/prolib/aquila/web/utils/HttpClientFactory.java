package ru.prolib.aquila.web.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClientFactory {
	public static final int DEFAULT_TIMEOUT = 15000;

	public static CloseableHttpClient createDefaultClient() {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setValidateAfterInactivity(DEFAULT_TIMEOUT);
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(DEFAULT_TIMEOUT)
				.setConnectionRequestTimeout(DEFAULT_TIMEOUT)
				.setConnectTimeout(DEFAULT_TIMEOUT)
				.build();
		return HttpClients.custom()
				.setDefaultRequestConfig(defaultRequestConfig)
				.setConnectionManager(connManager)
				.build();
	}

}
