package ru.prolib.aquila.web.utils.finam;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;

import ru.prolib.aquila.web.utils.HTTPAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.WebDriverFactory;
import ru.prolib.aquila.web.utils.ahc.AHCAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.jbd.JBDWebDriverFactory;
import ru.prolib.aquila.web.utils.swd.ff.FFWebDriverFactory;

public class FidexpFactorySTD implements FidexpFactory {
	private final WebDriverFactory driverFactory;
	private final HTTPAttachmentManagerFactory attMgrFactory;
	
	public FidexpFactorySTD(WebDriverFactory driverFactory, HTTPAttachmentManagerFactory attMgrFactory) {
		this.driverFactory = driverFactory;
		this.attMgrFactory = attMgrFactory;
	}

	@Override
	public Fidexp createInstance() {
		WebDriver driver = driverFactory.createWebDriver();
		return new Fidexp(driver, attMgrFactory.createAttachmentManager(driver));
	}
	
	/**
	 * Create default factory of FINAM facade.
	 * <p>
	 * It will use JBrowserDriver and attachment manager based on AHC Apache HTTP-Client based.
	 * <p>
	 * @param config - path to configuration ini-file
	 * @param configRequired - if true then configuration file is must be loaded or exception will be thrown.
	 * If false then loading configuration is optional and may be skipped if the file does not exist.
	 * @return new instance of FINAM facade factory
	 * @throws IOException - error during accessing configuration file or file not exists
	 */
	public static FidexpFactory newFactoryJBD(File config, boolean configRequired) throws IOException {
		JBDWebDriverFactory driverFactory = new JBDWebDriverFactory().withMoexTestedSettings();
		AHCAttachmentManagerFactory attMgrFactory = new AHCAttachmentManagerFactory();
		if ( configRequired || config.exists() ) {
			driverFactory.loadIni(config);
			attMgrFactory.loadIni(config);
		}
		return new FidexpFactorySTD(driverFactory, attMgrFactory);
	}
	
	public static FidexpFactory newFactoryJBD() {
		JBDWebDriverFactory driverFactory = new JBDWebDriverFactory().withMoexTestedSettings();
		AHCAttachmentManagerFactory attMgrFactory = new AHCAttachmentManagerFactory();
		return new FidexpFactorySTD(driverFactory, attMgrFactory);
	}
	
	public static FidexpFactory newFactoryFF(File config, boolean configRequired) throws IOException {
		FFWebDriverFactory driverFactory = new FFWebDriverFactory();
		AHCAttachmentManagerFactory attMgrFactory = new AHCAttachmentManagerFactory();
		if ( configRequired || config.exists() ) {
			driverFactory.loadIni(config);
			attMgrFactory.loadIni(config);
		}
		return new FidexpFactorySTD(driverFactory, attMgrFactory);
	}

}
