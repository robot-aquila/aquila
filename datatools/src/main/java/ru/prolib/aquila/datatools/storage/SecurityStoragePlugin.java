package ru.prolib.aquila.datatools.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ui.*;

public class SecurityStoragePlugin implements AquilaPlugin, EventListener {
	private static final Logger logger;
	private static final String VERSION;
	
	static {
		logger = LoggerFactory.getLogger(SecurityStoragePlugin.class);
		VERSION = "0.1.0";
	}
	
	private Terminal terminal;
	private SecurityStorageService securityStorageService;
	
	public SecurityStoragePlugin() {
		super();
	}

	@Override
	public void start() throws StarterException {
		terminal.onSecurityUpdate().addListener(this);
		terminal.onSecurityAvailable().addListener(this);
		for ( Security security : terminal.getSecurities() ) {
			securityStorageService.snapshotSessionAttributes(security);
		}
		logger.info("Update service started (v{})", VERSION);
	}

	@Override
	public void stop() throws StarterException {
		terminal.onSecurityUpdate().removeListener(this);
		terminal.onSecurityAvailable().removeListener(this);
		logger.info("Update service stopped (v{})", VERSION);
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal, String arg)
			throws Exception
	{
		this.terminal = terminal;
		//this.securityStorageService = (SecurityStorageService)
		//	locator.getApplicationContext().getBean("securityStorageService");
		throw new UnsupportedOperationException("The program is outdated");
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {

	}

	@Override
	public void onEvent(Event event) {
		SecurityEvent e = (SecurityEvent) event;
		securityStorageService.snapshotSessionAttributes(e.getSecurity());
	}

}
