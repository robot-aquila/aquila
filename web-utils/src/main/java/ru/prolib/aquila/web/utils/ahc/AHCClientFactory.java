package ru.prolib.aquila.web.utils.ahc;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Interface of Apache HTTP Client factory.
 */
public interface AHCClientFactory {
	
	CloseableHttpClient createHttpClient();

}
