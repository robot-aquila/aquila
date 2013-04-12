package ru.prolib.aquila.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import javax.swing.UIManager;

import org.apache.log4j.xml.DOMConfigurator;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * 2012-11-19<br>
 * $Id: Main.java 567 2013-03-11 01:55:28Z whirlwind $
 */
public class Main implements Runnable {
	private static final Logger logger;
	public static final String MAIN_INI = "main.ini";
	public static final String MAIN_SEC = "CONFIG FILES";
	public static final String LOGGER = "logger";
	public static final String DRIVERS = "drivers";
	public static final String PLUGINS = "plugins";
	
	private final List<AquilaPlugin> plugins = new Vector<AquilaPlugin>();
	private final StarterQueue starter = new StarterQueue();
	private final ServiceLocator locator;
	
	static {
		logger = LoggerFactory.getLogger(Main.class);
	}
	
	public Main() {
		super();
		locator = new ServiceLocator(new UiTexts(), this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Main().run(args);
		} catch ( Exception e ) {
			System.err.println("Bootstrap exception: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	
	private void run(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		Wini ini = new Wini(new File(MAIN_INI));
		DOMConfigurator.configure(ini.get(MAIN_SEC, LOGGER));
		locator.getTexts().load();
		starter.add(locator.getEventSystem().getEventQueue());
		boolean isUiMode = true;
		
		Wini drivers = new Wini(new File(ini.get(MAIN_SEC, DRIVERS)));
		String selectedDriver = selectDriver(drivers);
		if ( selectedDriver == null ) {
			logger.info("Driver was not selected");
			return;
		}
		
		logger.info("Selected driver: " + selectedDriver);
		Properties driverProps = section2Props(drivers.get(selectedDriver));
		Object object = Class
			.forName(driverProps.getProperty("class")).newInstance();
		if ( ! (object instanceof AquilaPluginTerminal) ) {
			logger.error("Cannot use as terminal plugin: {}",object.getClass());
			logger.info("Terminal plugin class MUST implement {} interface",
					AquilaPluginTerminal.class);
			return;
		}
		AquilaPluginTerminal pluginTerminal = (AquilaPluginTerminal) object;
		Terminal terminal = pluginTerminal.createTerminal(driverProps);
		
		if ( isUiMode ) {
			MainFrame frame = new MainFrame();
			frame.setTitle("Aquila: " + selectedDriver);
			locator.setMainFrame(frame);
			addPlugin(frame);
		}
		addCommonPlugins();
		addPlugin(pluginTerminal);
		loadPlugins(ini.get(MAIN_SEC, PLUGINS));
		addLastPlugins();
		
		for ( AquilaPlugin plugin : plugins ) {
			logger.debug("Initialize plugin: " + plugin.getClass());
			plugin.initialize(locator, terminal);
			if ( isUiMode ) {
				logger.debug("Create UI plugin: " + plugin.getClass());
				plugin.createUI(locator);
			}
		}
		logger.debug("Run start sequence");
		starter.start();
	}
	
	private void loadPlugins(String filename) throws Exception {
		if ( filename == null ) {
			logger.info("Plugins file not specified");
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String className = null;
		while ( (className = reader.readLine()) != null ) {
			className = className.trim();
			if ( className.startsWith(";") ) {
				continue;
			}
			addPlugin((AquilaPlugin) Class.forName(className).newInstance());
		}
		reader.close();
	}
	
	private void addCommonPlugins() {
		addPlugin(new ru.prolib.aquila.ui.plugin.UISecuritiesPlugin());
		addPlugin(new ru.prolib.aquila.ui.plugin.UIPositionsPlugin());
		addPlugin(new ru.prolib.aquila.ui.plugin.UIOrdersPlugin());
	}
	
	private void addLastPlugins() {
		addPlugin(new ru.prolib.aquila.ui.plugin.UILogPlugin());
	}
	
	private void addPlugin(AquilaPlugin plugin) {
		plugins.add(plugin);
		starter.add(plugin);
		logger.debug("Register plugin: " + plugin.getClass());
	}
	
	private Properties section2Props(Section section) {
		Properties props = new Properties();
		for( String key: section.keySet() ) {
			props.setProperty(key, section.get(key));
		}
		return props;
	}
	
	private String selectDriver(Wini drivers) {
		return new DlgDriver(locator.getTexts(), drivers.keySet())
			.selectDriver();
	}
	
	/**
	 * Exit action.
	 */
	@Override
	public void run() {
		try {
			starter.stop();
		} catch ( StarterException e ) { }
		System.exit(0);
	}
	
}
