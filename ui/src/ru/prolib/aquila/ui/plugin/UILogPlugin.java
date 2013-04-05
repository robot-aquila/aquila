package ru.prolib.aquila.ui.plugin;

import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.AquilaPlugin;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.LogTab;
import ru.prolib.aquila.ui.ServiceLocator;

/**
 * Плагин отображающий журнал во вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UILogPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UILogPlugin implements AquilaPlugin {
	public static final String TITLE = "TAB_LOG";
	private LogTab panel;
	
	public UILogPlugin() {
		super();
	}

	@Override
	public void start() throws StarterException {

	}

	@Override
	public void stop() throws StarterException {

	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {

	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		panel = new LogTab();
		facade.addTab(TITLE, panel);
	}

}
