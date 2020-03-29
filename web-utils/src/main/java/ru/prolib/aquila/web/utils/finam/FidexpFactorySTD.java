package ru.prolib.aquila.web.utils.finam;

import java.io.File;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import ru.prolib.aquila.web.utils.HTTPAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.WebDriverFactory;
import ru.prolib.aquila.web.utils.ahc.AHCAttachmentManagerFactory;
import ru.prolib.aquila.web.utils.wdfactory.FFWebDriverFactory;
import ru.prolib.aquila.web.utils.wdfactory.RWDFactory;

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
	
	public static FidexpFactory newFactoryFF(File config, boolean config_required, FirefoxOptions options)
			throws IOException
	{
		FFWebDriverFactory driverFactory = new FFWebDriverFactory(options);
		AHCAttachmentManagerFactory attMgrFactory = new AHCAttachmentManagerFactory();
		if ( config_required || config.exists() ) {
			driverFactory.loadIni(config);
			attMgrFactory.loadIni(config);
		}
		return new FidexpFactorySTD(driverFactory, attMgrFactory);
	}
	
	public static FidexpFactory newFactoryFF(File config, boolean config_required) throws IOException {
		return newFactoryFF(config, config_required, new FirefoxOptions());
	}
	
	public static FidexpFactory newFactoryRemote(File config, boolean config_required) throws IOException {
		return new FidexpFactorySTD(
				new RWDFactory().loadIni(config, config_required),
				new AHCAttachmentManagerFactory().loadIni(config, config_required)
			);
	}

}
