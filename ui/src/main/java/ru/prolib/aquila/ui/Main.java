package ru.prolib.aquila.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.text.Messages;

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
	
	private final List<PluginInfo> plugins = new Vector<PluginInfo>();
	private final StarterQueue starter = new StarterQueue();
	private final ServiceLocator locator;
	
	static {
		logger = LoggerFactory.getLogger(Main.class);
	}
	
	/**
	 * Информация о плагине.
	 */
	static class PluginInfo {
		private final AquilaPlugin instance;
		private final String arg;
		
		PluginInfo(AquilaPlugin instance, String arg) {
			this.instance = instance;
			this.arg = arg;
		}
		
		PluginInfo(AquilaPlugin instance) {
			this(instance, null);
		}
		
		@Override
		public String toString() {
			String result = instance.getClass().toString();
			if ( arg != null ) {
				result += " (arg=" + arg + ")";
			}
			return result;
		}
	}
	
	public Main() {
		super();
		locator = new ServiceLocator(new Messages(), this);
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				try {
					new Main().run(args);
				} catch ( Exception e ) {
					System.err.println("Bootstrap exception: " + e.getMessage());
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		});

	}
	
	private ApplicationContext createAppContext() {
		return new ClassPathXmlApplicationContext("/application-context.xml");
	}
	
	private void run(String[] args) throws Exception {
		locator.setApplicationContext(createAppContext());
		Wini ini = new Wini(new File(MAIN_INI));
		DOMConfigurator.configure(ini.get(MAIN_SEC, LOGGER));

		locator.getTexts();
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
		
		for ( PluginInfo p : plugins ) {
			logger.debug("Initialize plugin: " + p);
			p.instance.initialize(locator, terminal, p.arg);
			if ( isUiMode ) {
				logger.debug("Create UI plugin: " + p);
				p.instance.createUI(locator);
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
			if ( className.length() == 0 || className.startsWith(";") ) {
				continue;
			}
			String chunks[] = StringUtils.split(className, " ", 2);
			String arg = null;
			if ( chunks.length == 2 ) {
				className = chunks[0];
				arg = chunks[1];
			}
			addPlugin((AquilaPlugin)Class.forName(className).newInstance(),arg);
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
	
	private void addPlugin(AquilaPlugin plugin, String arg) {
		PluginInfo p = new PluginInfo(plugin, arg);
		plugins.add(p);
		starter.add(plugin);
		logger.debug("Register plugin: " + p);		
	}
	
	private void addPlugin(AquilaPlugin plugin) {
		addPlugin(plugin, null);
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
		} catch ( StarterException e ) {
			logger.error("Error stop sequence: ", e);
		}
		System.exit(0);
	}
	
}
