package ru.prolib.aquila.web.utils.wdfactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import ru.prolib.aquila.web.utils.WebDriverFactory;

/**
 * Firefox WebDriver factory.
 * <p>
 * geckodriver.exe must be in PATH<br>
 * firefox.exe must be in PATH<br>
 * geckodriver version must be >= 0.24.0<br>
 * firefox version must be >= 65.0.0<br>
 */
public class FFWebDriverFactory implements WebDriverFactory {
	
	public interface DriverInstantiator {
		
		WebDriver createDriver(FirefoxOptions options);
		
	}
	
	public static class DriverInstantiatorImpl implements DriverInstantiator {

		@Override
		public WebDriver createDriver(FirefoxOptions options) {
			return new FirefoxDriver(options);
		}
		
	}
	
	private final FFOptionsLoader optionsLoader = new FFOptionsLoader();
	private FirefoxOptions ffo;
	private Long implicitlyWaitSec;
	private final DriverInstantiator di;
	
	public FFWebDriverFactory(FirefoxOptions ffo, Long implicitlyWaitSec, DriverInstantiator di) {
		this.ffo = ffo;
		this.implicitlyWaitSec = implicitlyWaitSec;
		this.di = di;
	}
	
	public FFWebDriverFactory(FirefoxOptions ffo, Long implicitlyWaitSec) {
		this(ffo, implicitlyWaitSec, new DriverInstantiatorImpl());
	}
	
	public FFWebDriverFactory(FirefoxOptions ffo) {
		this(ffo, null);
	}
	
	public FFWebDriverFactory() {
		this(new FirefoxOptions());
	}
	
	public FirefoxOptions getFirefoxOptions() {
		return ffo;
	}
	
	public Long getImplicitlyWaitSec() {
		return implicitlyWaitSec;
	}
	
	public DriverInstantiator getDriverInstantiator() {
		return di;
	}

	@Override
	public WebDriver createWebDriver() {
		WebDriver driver = di.createDriver(ffo);
		if ( implicitlyWaitSec != null ) {
			driver.manage().timeouts().implicitlyWait(implicitlyWaitSec, TimeUnit.SECONDS);
		}
		return driver;
	}
	
	public FFWebDriverFactory loadIni(File file) throws IOException {
		optionsLoader.loadOptions(file, ffo = new FirefoxOptions());
		return this;
	}

}
