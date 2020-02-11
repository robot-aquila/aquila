package ru.prolib.aquila.web.utils.swd.ff;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;

import org.apache.commons.io.FileUtils;
import org.easymock.IMocksControl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import ru.prolib.aquila.web.utils.swd.ff.FFWebDriverFactory.DriverInstantiator;
import ru.prolib.aquila.web.utils.swd.ff.FFWebDriverFactory.DriverInstantiatorImpl;

public class FFWebDriverFactoryTest {
	
	@AfterClass
	public static void tearDownAfterClass() {
		System.getProperties().remove("webdriver.gecko.driver");
	}
	
	static List<String> toList(String... args) {
		List<String> result = new ArrayList<>();
		for ( String s : args ) {
			result.add(s);
		}
		return result;
	}
	
	@Rule public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private FirefoxOptions ffoStub;
	private DriverInstantiator diMock;
	private WebDriver driverMock;
	private FFWebDriverFactory service;
	private File temp;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		ffoStub = new FirefoxOptions();
		diMock = control.createMock(DriverInstantiator.class);
		driverMock = control.createMock(WebDriver.class);
		service = new FFWebDriverFactory(ffoStub, 5L, diMock);
		temp = File.createTempFile("ffwdf", ".test.ini");
	}
	
	@Test
	public void testCtor3() {
		assertEquals(ffoStub, service.getFirefoxOptions());
		assertEquals(Long.valueOf(5L), service.getImplicitlyWaitSec());
		assertSame(diMock, service.getDriverInstantiator());
	}
	
	@Test
	public void testCtor2() {
		service = new FFWebDriverFactory(ffoStub, 10L);
		assertEquals(ffoStub, service.getFirefoxOptions());
		assertEquals(Long.valueOf(10L), service.getImplicitlyWaitSec());
		assertEquals(DriverInstantiatorImpl.class, service.getDriverInstantiator().getClass());
	}
	
	@Test
	public void testCtor1() {
		service = new FFWebDriverFactory(ffoStub);
		assertEquals(ffoStub, service.getFirefoxOptions());
		assertNull(service.getImplicitlyWaitSec());
		assertEquals(DriverInstantiatorImpl.class, service.getDriverInstantiator().getClass());
	}
	
	@Test
	public void testCtor0() {
		service = new FFWebDriverFactory();
		assertNotNull(service.getFirefoxOptions());
		assertNull(service.getImplicitlyWaitSec());
		assertNotNull(service.getDriverInstantiator());
	}
	
	@Test
	public void testCreateWebDriver_WithoutImplicitlyWait() {
		service = new FFWebDriverFactory(ffoStub, null, diMock);
		expect(diMock.createDriver(ffoStub)).andReturn(driverMock);
		control.replay();
		
		WebDriver actual = service.createWebDriver();
		
		control.verify();
		assertSame(driverMock, actual);
	}

	@Test
	public void testCreateWebDriver_WithImplicitlyWait() {
		Options opMock = control.createMock(Options.class);
		Timeouts toMock = control.createMock(Timeouts.class);
		expect(diMock.createDriver(ffoStub)).andReturn(driverMock);
		expect(driverMock.manage()).andReturn(opMock);
		expect(opMock.timeouts()).andReturn(toMock);
		expect(toMock.implicitlyWait(5L, TimeUnit.SECONDS)).andReturn(toMock);
		control.replay();
		
		WebDriver actual = service.createWebDriver();
		
		control.verify();
		assertSame(driverMock, actual);
	}
	
	@Test
	public void testLoadIni_EmptyFile() throws Exception {
		FileUtils.writeStringToFile(temp, "");
		
		assertSame(service, service.loadIni(temp));
	}
	
	@SuppressWarnings("rawtypes")
	Map getFFOptions() {
		return (Map) service.getFirefoxOptions().toJson().get("moz:firefoxOptions");
	}
	
	@SuppressWarnings("unchecked")
	List<String> getFFArgs() {
		return (List<String>) getFFOptions().get("args");
	}
	
	@SuppressWarnings({ "rawtypes" })
	Map getFFLog() {
		return (Map) getFFOptions().get("log");
	}
	
	@Test
	public void testLoadIni_Headless() throws Exception {
		FileUtils.writeStringToFile(temp, "\n\n\n");
		service.loadIni(temp);
		assertEquals(toList(), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\n\n\n");
		service.loadIni(temp);
		assertEquals(toList(), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=\n\n\n");
		service.loadIni(temp);
		assertEquals(toList(), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=true\n\n\n");
		service.loadIni(temp);
		assertEquals(toList("--headless"), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=1\n\n\n");
		service.loadIni(temp);
		assertEquals(toList("--headless"), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=y\n\n\n");
		service.loadIni(temp);
		assertEquals(toList("--headless"), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=Y\n\n\n");
		service.loadIni(temp);
		assertEquals(toList("--headless"), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=false\n\n\n");
		service.loadIni(temp);
		assertEquals(toList(), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=0\n\n\n");
		service.loadIni(temp);
		assertEquals(toList(), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=n\n\n\n");
		service.loadIni(temp);
		assertEquals(toList(), getFFArgs());
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nheadless=N\n\n\n");
		service.loadIni(temp);
		assertEquals(toList(), getFFArgs());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testLoadIni_FirefoxBinary_ThrowsIfNotExists() throws Exception {
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nfirefox-binary=/foo/bar\n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_FirefoxBinary_NotSpecified() throws Exception {
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nfirefox-binary=  \n\n\n");
		service.loadIni(temp);
	}
	
	@Test
	public void testLoadIni_GeckodriverBinary() throws Exception {
		FileUtils.writeStringToFile(temp, "[firefox-driver]\ngeckodriver-binary=\n\n\n");
		service.loadIni(temp);
		assertNull(System.getProperty("webdriver.gecko.driver"));
		
		FileUtils.writeStringToFile(temp, "[firefox-driver]\ngeckodriver-binary=/foo/bar\n\n\n");
		service.loadIni(temp);
		assertEquals("/foo/bar", System.getProperty("webdriver.gecko.driver"));
	}
	
	@Test
	public void testLoadIni_LogLevel_SupportedLevels() throws Exception {
		for ( String log_level : toList("TRACE", "DEBUG", "CONFIG", "INFO", "WARN", "ERROR", "FATAL") ) {
			FileUtils.writeStringToFile(temp, "[firefox-driver]\nlog-level=" + log_level + "\n");
			service.loadIni(temp);
			assertEquals(FirefoxDriverLogLevel.fromString(log_level), getFFLog().get("level"));
		}
	}
	
	@Test
	public void testLoadIni_LogLevel_UnsupportedLevelCausesException() throws Exception {
		FileUtils.writeStringToFile(temp, "[firefox-driver]\nlog-level=bambuka\n");
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Unsupported Firefox log level: bambuka");
		
		service.loadIni(temp);
	}

}
