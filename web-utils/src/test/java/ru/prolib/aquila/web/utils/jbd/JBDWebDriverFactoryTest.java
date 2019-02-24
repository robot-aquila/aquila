package ru.prolib.aquila.web.utils.jbd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import ru.prolib.aquila.web.utils.jbd.JBDWebDriverFactory;

import com.machinepublishers.jbrowserdriver.ProxyConfig;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;

public class JBDWebDriverFactoryTest {
	private File temp;
	private JBDWebDriverFactory service;

	@Before
	public void setUp() throws Exception {
		service = new JBDWebDriverFactory();
		temp = File.createTempFile("wbfjbd", ".test.ini");
	}
	
	@After
	public void tearDown() throws Exception {
		if ( temp.exists() ) {
			temp.delete();
		}
	}
	
	@Test
	public void testWithMoexTestedSettings1() {
		Capabilities expected = Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.blockAds(true)
				.headless(true)
				.quickRender(true)
				.socketTimeout(15000)
				.connectionReqTimeout(15000)
				.connectTimeout(15000)
				.maxConnections(128)
				.javascript(true)
				.logJavascript(true)
				.saveAttachments(true)
				.userAgent(new UserAgent(
						UserAgent.Family.MOZILLA,
						"Mozilla",
						"Linux",
						"i686",
						"65.0",
						"Mozilla/5.0 (X11; Linux i686; rv:65.0) Gecko/20100101 Firefox/65.0"
					))
				.buildCapabilities();
		
		assertSame(service, service.withMoexTestedSettings(15000));
		
		Capabilities actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWithMoexTestedSettings0() {
		Capabilities expected = Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.blockAds(true)
				.headless(true)
				.quickRender(true)
				.socketTimeout(30000)
				.connectionReqTimeout(30000)
				.connectTimeout(30000)
				.maxConnections(128)
				.javascript(true)
				.logJavascript(true)
				.saveAttachments(true)
				.userAgent(new UserAgent(
						UserAgent.Family.MOZILLA,
						"Mozilla",
						"Linux",
						"i686",
						"65.0",
						"Mozilla/5.0 (X11; Linux i686; rv:65.0) Gecko/20100101 Firefox/65.0"
					))
				.buildCapabilities();
		
		assertSame(service, service.withMoexTestedSettings());
		
		Capabilities actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
	}

	@Test
	public void testLoadIni_Ssl() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nssl=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nssl=compatible\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ssl("compatible")
				.buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nssl=trustanything\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ssl("trustanything")
				.buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nssl=/foo/bar/file.CA\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ssl("/foo/bar/file.CA")
				.buildCapabilities();
		assertEquals(expected, actual);
		
		// if option not specified then existing setting will not be overriden
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ssl("/foo/bar/file.CA")
				.buildCapabilities();
		assertEquals(expected, actual);

		// reset to default
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nssl=\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.buildCapabilities();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoadIni_LoggerLevel() throws Exception {
		Logger logger = LogManager.getLogManager().getLogger("com.machinepublishers.jbrowserdriver");
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		assertEquals(Level.ALL, logger.getLevel());

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.ALL, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.OFF, logger.getLevel());

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=ALL\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.ALL, logger.getLevel());

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=CONFIG\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.CONFIG, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=FINE\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.FINE, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=FINER\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.FINER, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=FINEST\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.FINEST, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=INFO\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.INFO, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=OFF\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.OFF, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=SEVERE\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.SEVERE, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=WARNING\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.WARNING, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.OFF, logger.getLevel());
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nloggerLevel=ALL\n\n\n");
		service.loadIni(temp);
		assertEquals(Level.ALL, logger.getLevel());
	}
	
	@Test
	public void testLoadIni_AjaxWait() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxWait=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxWait=300\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ajaxWait(300)
				.buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxWait=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ajaxWait(300)
				.buildCapabilities();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testLoadIni_AjaxWait_ThrowsIfBadNumber() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxWait=XXL\n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_AjaxResourceTimeout() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxResourceTimeout=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxResourceTimeout=8000\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ajaxResourceTimeout(8000)
				.buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxResourceTimeout=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.ajaxResourceTimeout(8000)
				.buildCapabilities();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testLoadIni_AjaxResourceTimeout_ThrowsIfBadNumber() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\najaxResourceTimeout=256.123\n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_LogWire() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nlogWire=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nlogWire=true\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.logWire(true)
				.buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nlogWire=false\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.logWire(false)
				.buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nlogWire=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.logWire(false)
				.buildCapabilities();
		assertEquals(expected, actual);
	}

	@Test (expected=IOException.class)
	public void testLoadIni_LogWire_ThrowsIfBadValue() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nlogWire=foobar\n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_Cache() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache=false\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cache(false)
				.buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache=true\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cache(true)
				.buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cache(true)
				.buildCapabilities();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testLoadIni_Cache_ThrowsIfBadValue() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache=zulu24\n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_CacheDir() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.dir=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.dir=/foo/bar/buz\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cacheDir(new File("/foo/bar/buz"))
				.buildCapabilities();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoadIni_CacheEntries() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entries=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entries=800\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cacheEntries(800)
				.buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entries=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cacheEntries(800)
				.buildCapabilities();
		assertEquals(expected, actual);
	}

	@Test (expected=IOException.class)
	public void testLoadIni_CacheEntries_ThrowsIfBadNumber() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entries=foobar\n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_CacheEntrySize() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entrySize=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entrySize=1800\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cacheEntrySize(1800)
				.buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entrySize=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.cacheEntrySize(1800)
				.buildCapabilities();
		assertEquals(expected, actual);
	}

	@Test (expected=IOException.class)
	public void testLoadIni_CacheEntrySize_ThrowsIfBadNumber() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\ncache.entrySize=foobar\n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_ProxyType() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "\n\n\n");
		assertSame(service, service.loadIni(temp));
		expected = Settings.builder().buildCapabilities();
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=\n\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=HTTP\nproxy.host=localhost\nproxy.port=3128\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.proxy(new ProxyConfig(ProxyConfig.Type.HTTP, "localhost", 3128))
				.buildCapabilities();
		assertEquals(expected, actual);
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=SOCKS\nproxy.host=127.0.0.1\nproxy.port=5090\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.proxy(new ProxyConfig(ProxyConfig.Type.SOCKS, "127.0.0.1", 5090))
				.buildCapabilities();
		assertEquals(expected, actual);

		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=\nproxy.host=localhost\nproxy.port=3128\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.proxy(new ProxyConfig(ProxyConfig.Type.SOCKS, "127.0.0.1", 5090))
				.buildCapabilities();
		assertEquals(expected, actual);
	}

	@Test (expected=IOException.class)
	public void testLoadIni_ProxyType_ThrowsIfBadValue() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=kappa\n\n\n");
		service.loadIni(temp);
	}

	@Test (expected=IOException.class)
	public void testLoadIni_ProxyPort_ThrowsIfBadValue() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=SOCKS\nproxy.host=127.0.0.1\nproxy.port=zulu24\n");
		service.loadIni(temp);
	}

	@Test
	public void testLoadIni_ProxySettings_WithAuth() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=SOCKS\n"
				+ "proxy.host=127.0.0.1\nproxy.port=9050\nproxy.user=test\nproxy.pass=best\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.proxy(new ProxyConfig(ProxyConfig.Type.SOCKS, "127.0.0.1", 9050, "test", "best"))
				.buildCapabilities();
		assertEquals(expected, actual);
	}

	@Test
	public void testLoadIni_ProxySettings_WithAuthNoPass() throws Exception {
		Capabilities expected, actual;
		
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=SOCKS\n"
				+ "proxy.host=127.0.0.1\nproxy.port=9050\nproxy.user=test\n\n");
		service.loadIni(temp);
		actual = service.getSettingsBuilder().buildCapabilities();
		expected = Settings.builder()
				.proxy(new ProxyConfig(ProxyConfig.Type.SOCKS, "127.0.0.1", 9050, "test", ""))
				.buildCapabilities();
		assertEquals(expected, actual);

	}

	@Test (expected=IOException.class)
	public void testLoadInit_ProxySettings_ThrowsIfProxyEnabledButHostNotSpecified() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=SOCKS\nproxy.port=3128\n");
		service.loadIni(temp);
	}
	
	@Test (expected=IOException.class)
	public void testLoadInit_ProxySettings_ThrowsIfProxyEnabledButPortNotSpecified() throws Exception {
		FileUtils.writeStringToFile(temp, "[jbrowser-driver]\nproxy.type=SOCKS\nproxy.host=127.0.0.1\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testCreateAttachmentManager() {
		// TODO: 
	}

}
