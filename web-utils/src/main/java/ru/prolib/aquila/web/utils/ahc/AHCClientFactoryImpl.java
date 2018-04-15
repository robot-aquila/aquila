package ru.prolib.aquila.web.utils.ahc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

public class AHCClientFactoryImpl implements AHCClientFactory {
	private final RequestConfig.Builder requestConfigBuilder;
	private final HttpClientBuilder httpClientBuilder;
	
	/**
	 * Service constructor (for testing purposes only).
	 * <p>
	 * @param requestConfigBuilder - request configuration builder
	 * @param httpClientBuilder - HTTP client builder
	 */
	AHCClientFactoryImpl(RequestConfig.Builder requestConfigBuilder, HttpClientBuilder httpClientBuilder) {
		this.requestConfigBuilder = requestConfigBuilder;
		this.httpClientBuilder = httpClientBuilder;
	}
	
	public AHCClientFactoryImpl() {
		this(RequestConfig.custom(), HttpClients.custom());
	}

	@Override
	public CloseableHttpClient createHttpClient() {
		return httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build()).build();
	}
	
	public RequestConfig.Builder getRequestConfigBuilder() {
		return requestConfigBuilder;
	}
	
	public HttpClientBuilder getHttpClientBuilder() {
		return httpClientBuilder;
	}
	
	public AHCClientFactoryImpl withHttpProxy(String host, int port) {
		httpClientBuilder.setProxy(new HttpHost(host, port));
		return this;
	}

	/**
	 * Set max time waiting for data – after the connection was established;
	 * maximum time of inactivity between two data packets.
	 * <p>
	 * @param millis - timeout in milliseconds
	 * @return this
	 */
	public AHCClientFactoryImpl withSocketTimeout(int millis) {
		requestConfigBuilder.setSocketTimeout(millis);
		return this;
	}
	
	/**
	 * Set max time to establish the connection with the remote host.
	 * <p>
	 * @param millis - timeout in milliseconds
	 * @return this
	 */
	public AHCClientFactoryImpl withConnectTimeout(int millis) {
		requestConfigBuilder.setConnectTimeout(millis);
		return this;
	}
	
	/**
	 * Set max time to wait for a connection from the connection manager/pool.
	 * <p>
	 * @param millis - timeout in milliseconds
	 * @return this
	 */
	public AHCClientFactoryImpl withConnectionRequestTimeout(int millis) {
		requestConfigBuilder.setConnectionRequestTimeout(millis);
		return this;
	}
	
	public AHCClientFactoryImpl withDefaultHeaders() {
		List<Header> defaultHeaders = new ArrayList<>();
		defaultHeaders.add(new AHCBasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0"));
		defaultHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		defaultHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"));
		defaultHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
		httpClientBuilder.setDefaultHeaders(defaultHeaders);
		return this;
	}
	
	private Integer getOption_Integer(Section section, String key) throws IOException {
		if ( section.containsKey(key) ) {
			String value = section.get(key).trim();
			if ( value.length() > 0 ) {
				try {
					return Integer.parseInt(value);
				} catch ( NumberFormatException e ) {
					throw new IOException("Incorrect " + key + " value: " + value, e);
				}
			}
		}
		return null;
	}
	
	private Boolean getOption_Boolean(Section section, String key) throws IOException {
		if ( section.containsKey(key) ) {
			String value = section.get(key).trim();
			if ( value.length() > 0 ) {
				switch ( value ) {
				case "true": return true;
				case "false": return false;
				default:
					throw new IOException("Incorrect " + key + " value: " + value);
				}

			}
		}
		return null;
	}

	public AHCClientFactoryImpl loadIni(Section section) throws IOException {
		Integer xInt;
		Boolean xBool;
		
		if ( (xInt = getOption_Integer(section, "socketTimeout")) != null ) {
			withSocketTimeout(xInt);
		}
		if ( (xInt = getOption_Integer(section, "connectTimeout")) != null ) {
			withConnectTimeout(xInt);
		}
		if ( (xInt = getOption_Integer(section, "connectionRequestTimeout")) != null ) {
			withConnectionRequestTimeout(xInt);
		}
		xBool = getOption_Boolean(section, "defaultHeaders");
		if ( xBool != null && xBool ) {
			withDefaultHeaders();
		}
		if ( section.containsKey("proxy.type") ) {
			String proxyType = section.get("proxy.type");
			if ( proxyType.length() > 0 ) {
				switch ( proxyType ) {
				case "HTTP":
					break;
				default:
					throw new IOException("Incorrect proxy.type value: " + proxyType);
				}
				String proxyHost = "";
				Integer proxyPort = null;
				if ( ! section.containsKey("proxy.host") ) {
					throw new IOException("Proxy enabled but proxy.host not specified");
				}
				if ( ! section.containsKey("proxy.port") ) {
					throw new IOException("Proxy enabled but proxy.port not specified");
				}
				proxyHost = section.get("proxy.host").trim();
				proxyPort = getOption_Integer(section, "proxy.port");
				withHttpProxy(proxyHost, proxyPort);
			}
		}
		return this;
	}
	
	public AHCClientFactoryImpl loadIni(File file, String sectionID) throws IOException {
		Ini ini = new Ini(file);
		Section section = ini.get(sectionID);
		if ( section == null ) {
			return this;
		}
		return loadIni(section);
	}
	
	/**
	 * Load configuration from ini-file.
	 * <p>
	 * This method searches default section name: [apache-http-client]
	 * <p>
	 * @param file - path to ini-file
	 * @return this
	 * @throws IOException - an error occurred
	 */
	public AHCClientFactoryImpl loadIni(File file) throws IOException {
		return loadIni(file, "apache-http-client");
	}

}
