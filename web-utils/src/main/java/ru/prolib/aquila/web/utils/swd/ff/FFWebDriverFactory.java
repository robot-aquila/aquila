package ru.prolib.aquila.web.utils.swd.ff;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
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
		ffo = new FirefoxOptions();
		FirefoxBinary ffb = new FirefoxBinary();
		Ini ini = new Ini(file);
		if ( ini.containsKey("firefox-driver") ) {
			Section sec = ini.get("firefox-driver");
			if ( sec.containsKey("firefox-binary") ) {
				String x = sec.get("firefox-binary").trim();
				if ( x.length() > 0 ) {
					ffb = new FirefoxBinary(new File(x));
				}
			}
			if ( sec.containsKey("headless") ) {
				String ssl = sec.get("headless").trim().toLowerCase();
				switch ( ssl ) {
				case "true":
				case "1":
				case "y":
					ffb.addCommandLineOptions("--headless");
					break;
				case "":
				case "0":
				default:
					break;
				}
			}
			if ( sec.containsKey("geckodriver-binary") ) {
				String x = sec.get("geckodriver-binary").trim();
				if ( x.length() > 0 ) {
					System.setProperty("webdriver.gecko.driver", x);
				}
			}
		}
		ffo.setBinary(ffb);
		return this;
	}

}
