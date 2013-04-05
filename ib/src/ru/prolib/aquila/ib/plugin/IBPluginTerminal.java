package ru.prolib.aquila.ib.plugin;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.SecurityNotExistsException;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ib.IBFactory;
import ru.prolib.aquila.ui.AquilaPluginTerminal;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.ServiceLocator;
import ru.prolib.aquila.ui.plugin.UISecuritiesPlugin;

/**
 * Плагин для использования терминала в рамках Aquila UI.
 * <p>
 * 2013-02-38<br>
 * $Id: IBPluginTerminal.java 557 2013-03-04 17:19:49Z whirlwind $
 */
public class IBPluginTerminal implements AquilaPluginTerminal, EventListener {
	private static final Logger logger;
	public static final String MENU_SECURITY_REQUEST = "MENU_SECURITY_REQUEST";
	
	private Terminal terminal;
	private EventType onSecurityRequest;
	private AquilaUI facade;
	
	static {
		logger = LoggerFactory.getLogger(IBPluginTerminal.class);
	}
	
	public IBPluginTerminal() {
		super();
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		this.facade = facade;
		onSecurityRequest = facade.getMainMenu()
			.getMenu(UISecuritiesPlugin.MENU_SECURITY)
			.addItem(MENU_SECURITY_REQUEST, MENU_SECURITY_REQUEST)
			.OnCommand();
		onSecurityRequest.addListener(this);
		logger.warn("TODO: language pack not exists");
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void start() throws StarterException {
		
	}

	@Override
	public void stop() throws StarterException {
		
	}

	@Override
	public Terminal createTerminal(Properties props) throws Exception {
		return new IBFactory().createTerminal(props);
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(onSecurityRequest) ) {
			DlgRequestSecurity dlg =
				new DlgRequestSecurity(facade.getMainFrame());
			SecurityDescriptor descr = dlg.getDescriptor();
			if ( descr != null && ! terminal.isSecurityExists(descr) ) {
				try {
					terminal.getSecurity(descr);
				} catch ( SecurityNotExistsException e ) {
					logger.error("Security not found: {}", descr);
				} catch ( SecurityException e ) {
					logger.error("Unhandled exception: ", e);
				}
			}
		}
	}

}
