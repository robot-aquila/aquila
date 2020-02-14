package ru.prolib.aquila.web.utils.wdfactory;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FFOptionsLoader {

	public FirefoxOptions loadOptions(File config_file, FirefoxOptions options) throws IOException {
		FirefoxBinary ffb = new FirefoxBinary();
		Ini ini = new Ini(config_file);
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
			if ( sec.containsKey("log-level") ) {
				String log_level_str = sec.get("log-level");
				FirefoxDriverLogLevel log_level = FirefoxDriverLogLevel.fromString(log_level_str);
				if ( log_level == null ) {
					throw new IllegalArgumentException("Unsupported Firefox log level: " + log_level_str);
				}
				options.setLogLevel(log_level);
			}
		}
		options.setBinary(ffb);
		return options;
	}
	
	public FirefoxOptions loadOptions(File config_file) throws IOException {
		return loadOptions(config_file, new FirefoxOptions());
	}

}
