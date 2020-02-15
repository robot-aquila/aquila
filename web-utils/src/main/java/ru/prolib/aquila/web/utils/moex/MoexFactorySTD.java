package ru.prolib.aquila.web.utils.moex;

import java.io.File;
import java.io.IOException;

import ru.prolib.aquila.web.utils.WebDriverFactory;
import ru.prolib.aquila.web.utils.jbd.JBDWebDriverFactory;
import ru.prolib.aquila.web.utils.wdfactory.FFWebDriverFactory;
import ru.prolib.aquila.web.utils.wdfactory.RWDFactory;

/**
 * Standard factory of MOEX service facade based on WebDriverFactory.
 * Contains some additional factory methods to produce facade in a most useful way.
 */
public class MoexFactorySTD implements MoexFactory {
	private final WebDriverFactory factory;
	
	public MoexFactorySTD(WebDriverFactory factory) {
		this.factory = factory;
	}

	@Override
	public Moex createInstance() {
		return new Moex(factory.createWebDriver(), true);
	}
	
	/**
	 * Create MOEX facade factory with JBrowserDriver transport.
	 * <p>
	 * @param jbdConfig - path to JBrowserDriver configuration file
	 * @param jbdConfigRequired - if true then configuration file is must be loaded or exception will be thrown.
	 * If false then loading configuration is optional and may be skipped if the file does not exist.
	 * @return new instance of MOEX facade factory 
	 * @throws IOException - error during accessing configuration file or file not exists
	 */
	public static MoexFactory newFactoryJBD(File jbdConfig, boolean jbdConfigRequired) throws IOException {
		JBDWebDriverFactory factory = new JBDWebDriverFactory().withMoexTestedSettings();
		if ( jbdConfigRequired || jbdConfig.exists() ) {
			factory.loadIni(jbdConfig);
		}
		return new MoexFactorySTD(factory);
	}
	
	/**
	 * Create MOEX facade factory with JBrowserDriver transport.
	 * <p>
	 * @return new instance of MOEX facade factory
	 */
	public static MoexFactory newFactoryJBD() {
		return new MoexFactorySTD(new JBDWebDriverFactory().withMoexTestedSettings());
	}
	
	/**
	 * Create MOEX facade factory with Firefox transport.
	 * <p>
	 * @param config - path to configuration ini-file
	 * @param configRequired - if true then configuration file is must be loaded or exception will be thrown.
	 * If false then loading configuration is optional and may be skipped if the file does not exist.
	 * @return new instance of MOEX facade factory 
	 * @throws IOException - error during accessing configuration file or file not exists
	 */
	public static MoexFactory newFactoryFF(File config, boolean configRequired) throws IOException {
		FFWebDriverFactory factory = new FFWebDriverFactory();
		if ( configRequired || config.exists() ) {
			factory.loadIni(config);
		}
		return new MoexFactorySTD(factory);
	}
	
	public static MoexFactory newFactoryRemote(File config, boolean config_required) throws IOException {
		return new MoexFactorySTD(new RWDFactory().loadIni(config, config_required));
	}

}
