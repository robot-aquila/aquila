package ru.prolib.aquila.web.utils.ahc;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

// Need PowerMock here because Apache classes have final methods
@Ignore
@RunWith(PowerMockRunner.class) 
@PrepareForTest({RequestConfig.Builder.class, HttpClientBuilder.class })
public class AHCClientFactoryImplTest {
	
	static class LoadIni_FileAndSectionID implements Runnable {
		private final AHCClientFactoryImpl service;
		private final File file;
		private final String sectionID;
		
		LoadIni_FileAndSectionID(AHCClientFactoryImpl service, File file, String sectionID) {
			this.service = service;
			this.file = file;
			this.sectionID = sectionID;
		}

		@Override
		public void run() {
			try {
				assertSame(service, service.loadIni(file, sectionID));
			} catch ( IOException e ) {
				throw new IllegalStateException(e);
			}
		}
		
	}
	
	static class LoadIni_Section implements Runnable {
		private final AHCClientFactoryImpl service;
		private final File file;
		private final String sectionID;
		
		LoadIni_Section(AHCClientFactoryImpl service, File file, String sectionID) {
			this.service = service;
			this.file = file;
			this.sectionID = sectionID;
		}

		@Override
		public void run() {
			try {
				Ini ini = new Ini(file);
				Section sec = ini.get(sectionID);
				if ( sec != null ) {
					assertSame(service, service.loadIni(sec));
				}
			} catch ( IOException e ) {
				throw new IllegalStateException(e);
			}
		}
		
	}
	
	static class LoadIni_File implements Runnable {
		private final AHCClientFactoryImpl service;
		private final File file;
		
		LoadIni_File(AHCClientFactoryImpl service, File file) {
			this.service = service;
			this.file = file;
		}

		@Override
		public void run() {
			try {
				assertSame(service, service.loadIni(file));
			} catch ( IOException e ) {
				throw new IllegalStateException(e);
			}
		}
		
	}
	
	private File temp;
	private RequestConfig.Builder requestConfigBuilderMock;
	private HttpClientBuilder httpClientBuilderMock;
	private RequestConfig requestConfigMock;
	private CloseableHttpClient httpClientMock;
	private AHCClientFactoryImpl service;

	@Before
	public void setUp() throws Exception {
		requestConfigBuilderMock = createMock(RequestConfig.Builder.class);
		httpClientBuilderMock = createMock(HttpClientBuilder.class);
		requestConfigMock = createMock(RequestConfig.class);
		httpClientMock = createMock(CloseableHttpClient.class);
		service = new AHCClientFactoryImpl(requestConfigBuilderMock, httpClientBuilderMock);
		temp = File.createTempFile("ahcf", ".test.ini");
	}
	
	@After
	public void tearDown() throws Exception {
		if ( temp != null ) {
			temp.delete();
		}
	}
	
	@Test
	public void testCtor() {
		assertSame(requestConfigBuilderMock, service.getRequestConfigBuilder());
		assertSame(httpClientBuilderMock, service.getHttpClientBuilder());
	}

	@Test
	public void testCreateHttpClient() {
		expect(requestConfigBuilderMock.build()).andReturn(requestConfigMock);
		expect(httpClientBuilderMock.setDefaultRequestConfig(requestConfigMock)).andReturn(httpClientBuilderMock);
		expect(httpClientBuilderMock.build()).andReturn(httpClientMock);
		replay(requestConfigBuilderMock, httpClientBuilderMock, requestConfigMock, httpClientMock);
		
		CloseableHttpClient actual = service.createHttpClient();
		
		verify(requestConfigBuilderMock, httpClientBuilderMock, requestConfigMock, httpClientMock);
		assertSame(httpClientMock, actual);
	}
	
	@Test
	public void testWithHttpProxy() {
		expect(httpClientBuilderMock.setProxy(new HttpHost("foo.bar", 8080))).andReturn(httpClientBuilderMock);
		replay(httpClientBuilderMock);
		
		assertSame(service, service.withHttpProxy("foo.bar", 8080));
		
		verify(httpClientBuilderMock);
	}
	
	@Test
	public void testWithSocketTimeout() {
		expect(requestConfigBuilderMock.setSocketTimeout(1200)).andReturn(requestConfigBuilderMock);
		replay(requestConfigBuilderMock);
		
		assertSame(service, service.withSocketTimeout(1200));
		
		verify(requestConfigBuilderMock);
	}
	
	@Test
	public void testWithConnectTimeout() {
		expect(requestConfigBuilderMock.setConnectTimeout(2500)).andReturn(requestConfigBuilderMock);
		replay(requestConfigBuilderMock);
		
		assertSame(service, service.withConnectTimeout(2500));
		
		verify(requestConfigBuilderMock);
	}
	
	@Test
	public void testWithConnectionRequestTimeout() {
		expect(requestConfigBuilderMock.setConnectionRequestTimeout(1000)).andReturn(requestConfigBuilderMock);
		replay(requestConfigBuilderMock);
		
		assertSame(service, service.withConnectionRequestTimeout(1000));
		
		verify(requestConfigBuilderMock);
	}
	
	@Test
	public void testWithDefaultHeaders() {
		List<Header> expectedHeaders = new ArrayList<>();
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0"));
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"));
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
		
		expect(httpClientBuilderMock.setDefaultHeaders(expectedHeaders)).andReturn(httpClientBuilderMock);
		replay(httpClientBuilderMock);
		
		assertSame(service, service.withDefaultHeaders());
		
		verify(httpClientBuilderMock);
	}
	
	private void _testLoadIni_SocketTimeout(String sectionID, Runnable action) throws Exception {
		FileUtils.writeStringToFile(temp, "\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nsocketTimeout=\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nsocketTimeout=2850\n\n\n");
		expect(requestConfigBuilderMock.setSocketTimeout(2850)).andReturn(requestConfigBuilderMock);
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nsocketTimeout=zulu\n\n\n");
		replay(requestConfigBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Incorrect socketTimeout value: zulu", e.getCause().getMessage());
		}
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
	}
	
	@Test
	public void testLoadIni_SocketTimeout() throws Exception {
		_testLoadIni_SocketTimeout("section", new LoadIni_Section(service, temp, "section"));
		_testLoadIni_SocketTimeout("my-custom-section", new LoadIni_FileAndSectionID(service, temp, "my-custom-section"));
		_testLoadIni_SocketTimeout("apache-http-client", new LoadIni_File(service, temp));
	}
	
	private void _testLoadIni_ConnectTimeout(String sectionID, Runnable action) throws Exception {
		FileUtils.writeStringToFile(temp, "\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nconnectTimeout=\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nconnectTimeout=1024\n\n\n");
		expect(requestConfigBuilderMock.setConnectTimeout(1024)).andReturn(requestConfigBuilderMock);
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nconnectTimeout=foobar\n\n\n");
		replay(requestConfigBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Incorrect connectTimeout value: foobar", e.getCause().getMessage());
		}
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
	}
	
	@Test
	public void testLoadIni_ConnectTimeout() throws Exception {
		_testLoadIni_ConnectTimeout("section", new LoadIni_Section(service, temp, "section"));
		_testLoadIni_ConnectTimeout("my-custom-section", new LoadIni_FileAndSectionID(service, temp, "my-custom-section"));
		_testLoadIni_ConnectTimeout("apache-http-client", new LoadIni_File(service, temp));
	}
	
	private void _testLoadIni_ConnectionRequestTimeout(String sectionID, Runnable action) throws Exception {
		FileUtils.writeStringToFile(temp, "\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nconnectionRequestTimeout=\n\n\n");
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nconnectionRequestTimeout=500\n\n\n");
		expect(requestConfigBuilderMock.setConnectionRequestTimeout(500)).andReturn(requestConfigBuilderMock);
		replay(requestConfigBuilderMock);
		action.run();
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nconnectionRequestTimeout=charlie\n\n\n");
		replay(requestConfigBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Incorrect connectionRequestTimeout value: charlie", e.getCause().getMessage());
		}
		verify(requestConfigBuilderMock);
		reset(requestConfigBuilderMock);
	}
	
	@Test
	public void testLoadIni_ConnectionRequestTimeout() throws Exception {
		_testLoadIni_ConnectionRequestTimeout("section", new LoadIni_Section(service, temp, "section"));
		_testLoadIni_ConnectionRequestTimeout("my-custom-section", new LoadIni_FileAndSectionID(service, temp, "my-custom-section"));
		_testLoadIni_ConnectionRequestTimeout("apache-http-client", new LoadIni_File(service, temp));
	}
	
	private void _testLoadIni_DefaultHeaders(String sectionID, Runnable action) throws Exception {
		FileUtils.writeStringToFile(temp, "\n\n\n");
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\n\n\n");
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\ndefaultHeaders=\n\n\n");
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\ndefaultHeaders=false\n\n\n");
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\ndefaultHeaders=true\n\n\n");
		List<Header> expectedHeaders = new ArrayList<>();
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0"));
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"));
		expectedHeaders.add(new AHCBasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
		expect(httpClientBuilderMock.setDefaultHeaders(expectedHeaders)).andReturn(httpClientBuilderMock);
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\ndefaultHeaders=12345\n\n\n");
		replay(httpClientBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Incorrect defaultHeaders value: 12345", e.getCause().getMessage());
		}
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

	}
	
	@Test
	public void testLoadIni_DefaultHeaders() throws Exception {
		_testLoadIni_DefaultHeaders("section", new LoadIni_Section(service, temp, "section"));
		_testLoadIni_DefaultHeaders("my-custom-section", new LoadIni_FileAndSectionID(service, temp, "my-custom-section"));
		_testLoadIni_DefaultHeaders("apache-http-client", new LoadIni_File(service, temp));
	}
	
	private void _testLoadIni_ProxySettings(String sectionID, Runnable action) throws Exception {
		FileUtils.writeStringToFile(temp, "\n\n\n");
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\n\n\n");
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nproxy.type=\n\n\n");
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nproxy.type=SOCKS5\n\n\n");
		replay(httpClientBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Incorrect proxy.type value: SOCKS5", e.getCause().getMessage());
		}
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nproxy.type=HTTP\nproxy.port=3128\n\n\n");
		replay(httpClientBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Proxy enabled but proxy.host not specified", e.getCause().getMessage());
		}
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nproxy.type=HTTP\nproxy.host=127.0.0.1\n\n\n");
		replay(httpClientBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Proxy enabled but proxy.port not specified", e.getCause().getMessage());
		}
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);
		
		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nproxy.type=HTTP\nproxy.host=127.0.0.1\nproxy.port=zulu\n\n");
		replay(httpClientBuilderMock);
		try {
			action.run();
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Incorrect proxy.port value: zulu", e.getCause().getMessage());
		}
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);

		FileUtils.writeStringToFile(temp, "[" + sectionID + "]\nproxy.type=HTTP\nproxy.host=127.0.0.1\nproxy.port=3128\n\n");
		expect(httpClientBuilderMock.setProxy(new HttpHost("127.0.0.1", 3128))).andReturn(httpClientBuilderMock);
		replay(httpClientBuilderMock);
		action.run();
		verify(httpClientBuilderMock);
		reset(httpClientBuilderMock);
	}
	
	@Test
	public void testLoadIni_ProxySettings() throws Exception {
		_testLoadIni_ProxySettings("section", new LoadIni_Section(service, temp, "section"));
		_testLoadIni_ProxySettings("my-custom-section", new LoadIni_FileAndSectionID(service, temp, "my-custom-section"));
		_testLoadIni_ProxySettings("apache-http-client", new LoadIni_File(service, temp));		
	}

}
