package ru.prolib.aquila.web.utils.wdfactory;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import ru.prolib.aquila.web.utils.HTTPAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.WebDriverFactory;
import ru.prolib.aquila.web.utils.httpattachment.ChromeAttachmentManager;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;
import ru.prolib.aquila.web.utils.wdfactory.RWDOptionsLoader.RWDOptions;

/**
 * RemoteWebDriver factory.
 */
public class RWDFactory implements WebDriverFactory, HTTPAttachmentManagerFactory {
	private final RWDOptionsLoader optionsLoader;
	private RWDOptions options;
	
	public RWDFactory(RWDOptionsLoader optionsLoader) {
		this.optionsLoader = optionsLoader;
		this.options = optionsLoader.defaultOptions();
	}
	
	public RWDFactory() {
		this(new RWDOptionsLoader());
	}

	@Override
	public WebDriver createWebDriver() {
		return new RemoteWebDriver(options.getHubUrl(), options.getCapabilities());
	}

	@Override
	public HTTPAttachmentManager createAttachmentManager(WebDriver driver) {
		switch ( options.getDriverID() ) {
		case RWDOptionsLoader.DRIVER_FIREFOX:
			throw new IllegalStateException("Firefox driver currently not supported");
		case RWDOptionsLoader.DRIVER_CHROME:
			return new ChromeAttachmentManager(options.getDownloadDirLocal(), options.getDownloadTimeout());
		default:
			throw new IllegalArgumentException("Unsupported driver: " + options.getDriverID());
		}
	}
	
	public RWDFactory loadIni(File config_file, boolean config_required) throws IOException {
		if ( config_required || config_file.exists() ) {
			options = optionsLoader.loadOptions(config_file);
		}
		return this;
	}

}
