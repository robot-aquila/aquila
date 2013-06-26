package ru.prolib.aquila.ib.plugin;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.ui.IBCacheWindow;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.plugin.UISecuritiesPlugin;
import ru.prolib.aquila.ui.wrapper.MenuException;

/**
 * Плагин для использования терминала в рамках Aquila UI.
 * <p>
 * 2013-02-38<br>
 * $Id: IBPluginTerminal.java 557 2013-03-04 17:19:49Z whirlwind $
 */
public class IBPluginTerminal implements AquilaPluginTerminal, EventListener {
	private static final Logger logger;
	public static final String TEXT_SECTION = "IB";
	public static final String MENU_SECURITY_REQUEST = "MENU_SECURITY_REQUEST";
	public static final String MENU_SHOW_CACHE = "MENU_SHOW_CACHE";
	
	private IBEditableTerminal terminal;
	private ClassLabels labels;
	private EventType onSecurityRequest, onShowCache;
	private AquilaUI facade;
	private IBCacheWindow winCache;
	
	static {
		logger = LoggerFactory.getLogger(IBPluginTerminal.class);
	}
	
	public IBPluginTerminal() {
		super();
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		if ( terminal == null ) {
			logger.warn("Additional functionality disabled");
		}
		this.facade = facade;
		labels = facade.getTexts().get(TEXT_SECTION);
		createMenu();
		winCache = new IBCacheWindow(terminal, facade.getMainFrame(), labels);
		winCache.init();
	}
	
	private void createMenu() throws MenuException {
		onSecurityRequest = facade.getMainMenu()
			.getMenu(UISecuritiesPlugin.MENU_SECURITY)
			.addItem(MENU_SECURITY_REQUEST, labels.get(MENU_SECURITY_REQUEST))
			.OnCommand();
		onShowCache = facade.getMainMenu()
			.getMenu(MainFrame.MENU_VIEW)
			.addItem(MENU_SHOW_CACHE, labels.get(MENU_SHOW_CACHE))
			.OnCommand();
		
		onSecurityRequest.addListener(this);
		onShowCache.addListener(this);
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {
		if ( terminal instanceof IBEditableTerminal ) {
			this.terminal = (IBEditableTerminal) terminal;
		} else {
			logger.warn("Unexpected terminal type: {}", terminal.getClass());
		}
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public Terminal createTerminal(Properties props) throws Exception {
		return new IBFactory().createTerminal(props);
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(onSecurityRequest) ) {
			SecurityDescriptor descr =
				new DlgRequestSecurity(facade.getMainFrame()).getDescriptor();
			if ( descr != null ) {
				terminal.requestSecurity(descr);
			}
		} else if ( event.isType(onShowCache) ) {
			winCache.showWindow();
		}
	}

}
