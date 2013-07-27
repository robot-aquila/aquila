package ru.prolib.aquila.quik;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.quik.ui.CacheWindow;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.wrapper.MenuException;

public class QUIKPluginTerminal implements AquilaPluginTerminal, EventListener {
	private static final Logger logger;
	public static final String TEXT_SECTION = "Quik";
	public static final String MENU_DDE_CACHE = "MENU_DDE_CACHE";
	
	static {
		logger = LoggerFactory.getLogger(QUIKPluginTerminal.class);
	}

	private QUIKEditableTerminal terminal;
	private ClassLabels labels;
	private AquilaUI facade;
	private EventType onShowDdeCache;
	private CacheWindow winDdeCache;
	
	public QUIKPluginTerminal() {
		super();
	}

	@Override
	public void createUI(AquilaUI facade) {
		if ( terminal == null ) {
			logger.warn("Additional functionality disabled.");
			return;
		}
		this.facade = facade;
		labels = facade.getTexts().get(TEXT_SECTION);
		try {
			createMenu();
		} catch ( MenuException e ) {
			logger.error("Error creating menu: ", e);
		}
		winDdeCache = new CacheWindow(facade.getMainFrame(),terminal,labels);
		winDdeCache.init();
	}
	
	/**
	 * Создать специфические пункты меню.
	 * <p>
	 * @throws MenuException
	 */
	private void createMenu() throws MenuException {
		onShowDdeCache = facade.getMainMenu().getMenu(MainFrame.MENU_VIEW)
			.addItem(MENU_DDE_CACHE, labels.get(MENU_DDE_CACHE))
			.OnCommand();
		onShowDdeCache.addListener(this);
	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String arg)
	{
		if ( terminal instanceof QUIKEditableTerminal ) {
			this.terminal = (QUIKEditableTerminal) terminal;
		} else {
			logger.warn("Unexpected terminal type");
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
		return new QUIKFactory().createTerminal(props);
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(onShowDdeCache) ) {
			winDdeCache.showWindow();
		}
	}

}
