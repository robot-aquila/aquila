package ru.prolib.aquila.ui.plugin;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Securities;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.plugin.getters.GSecurity;
import ru.prolib.aquila.ui.wrapper.DataSourceEventTranslator;
import ru.prolib.aquila.ui.wrapper.Table;
import ru.prolib.aquila.ui.wrapper.TableImpl;
import ru.prolib.aquila.ui.wrapper.TableModel;
import ru.prolib.aquila.ui.wrapper.TableModelImpl;

/**
 * Плагин отображающий доступные инструменты в виде таблицы на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UISecuritiesPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UISecuritiesPlugin implements AquilaPlugin {
	public static final String TEXT_SECT = "UISecuritiesPlugin";
	public static final String TITLE = "TAB_SECURITIES";
	public static final String MENU_SECURITY = "MENU_SEC";
	
	private Terminal terminal;
	private SecuritiesTableCols cols = new SecuritiesTableCols();
	private TableModel model = new TableModelImpl(new GSecurity());
	private Table tb;
	private JPanel panel = new JPanel(new BorderLayout());
	
	public UISecuritiesPlugin() {
		super();		
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public SecuritiesTableCols getTableCols() {
		return cols;
	}
	
	public void setTableCols(SecuritiesTableCols cols) {
		this.cols = cols;
	}
	
	public TableModel getModel() {
		return model;
	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String arg)
	{
		this.terminal = terminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		EventSystem ev = ((ServiceLocator) facade).getEventSystem();
		EventDispatcher dispatcher = ev.createEventDispatcher();
		ClassLabels text = facade.getTexts().get(TEXT_SECT);
		
		cols.addColumnsToModel(model, text);

		DataSourceEventTranslator onRowAvailableListener = new DataSourceEventTranslator(
				dispatcher, ev.createGenericType(dispatcher));
		model.setOnRowAvailableListener(onRowAvailableListener);
		
		
		DataSourceEventTranslator onRowChangedListener = new DataSourceEventTranslator(
				dispatcher, ev.createGenericType(dispatcher));
		model.setOnRowChangedListener(onRowChangedListener);		
		
		tb = new TableImpl(model, dispatcher, ev.createGenericType(dispatcher));		
		tb.start();
		
		facade.addTab(text.get(TITLE), panel);
        panel.add(new JScrollPane(tb.getUnderlayed()));
		
        facade.getMainMenu().addMenu(MENU_SECURITY, text.get(MENU_SECURITY));
	}
	
	@Override
	public void start() throws StarterException {
		((Securities) terminal).OnSecurityAvailable().addListener(
				model.getOnRowAvailableListener());
		((Securities) terminal).OnSecurityChanged().addListener(
				model.getOnRowChangedListener());
	}
	
	@Override
	public void stop() throws StarterException {
		((Securities) terminal).OnSecurityAvailable().removeListener(
				model.getOnRowAvailableListener());
		((Securities) terminal).OnSecurityChanged().removeListener(
				model.getOnRowChangedListener());
	}

}
