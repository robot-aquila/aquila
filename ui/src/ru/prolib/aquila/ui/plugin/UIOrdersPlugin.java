package ru.prolib.aquila.ui.plugin;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.AquilaPlugin;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.OrdersTableModel;
import ru.prolib.aquila.ui.ServiceLocator;
import ru.prolib.aquila.ui.StopOrdersTableModel;
import ru.prolib.aquila.ui.wrapper.Menu;
import ru.prolib.aquila.ui.wrapper.MenuItem;

/**
 * Плагин отображающий заявки в таблицах на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UIOrdersPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UIOrdersPlugin implements AquilaPlugin, EventListener {
	public static final String TEXT_SECT = "UIOrdersPlugin";
	public static final String TITLE = "TAB_ORDERS";
	public static final String TITLE_STOP = "TAB_STOP_ORDERS";
	public static final String MENU_ORDER = "MENU_ORDER";
	public static final String MENU_ORDER_CANCEL = "MENU_ORDER_CANCEL";

	private Terminal terminal;
	private StopOrdersTableModel stopOrdersModel;
	private OrdersTableModel ordersModel;
	private JTable ordersTable, stopOrdersTable;
	private MenuItem cmdCancel;

	@Override
	public void start() throws StarterException {

	}

	@Override
	public void stop() throws StarterException {

	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		stopOrdersModel = new StopOrdersTableModel(terminal, facade.getTexts());
		stopOrdersModel.start();
		ordersModel = new OrdersTableModel(terminal, facade.getTexts());
		ordersModel.start();
		
		stopOrdersTable = new JTable(stopOrdersModel);
		// TODO: Что это за хрень? Непонятно, что будут проблемы
		// каждый раз при изменении состава колонок или смене порядка?
		// Неужели трудно догадаться, что нужно сделать класс таблицы,
		// который позволит делать это по идентификатору колонки например?
		stopOrdersTable.getColumnModel().getColumn(5).setPreferredWidth(200);
		
		ordersTable = new JTable(ordersModel);
		ordersTable.getColumnModel().getColumn(5).setPreferredWidth(200);
		ordersTable.getSelectionModel()
			.addListSelectionListener(new ListSelectionListener() {
				@Override public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting() ) { onTableRowSelect(); } }
    		});
		
		ClassLabels text = facade.getTexts().get(TEXT_SECT);
		JTabbedPane panel = new JTabbedPane();
		panel.add(text.get(TITLE_STOP), new JScrollPane(stopOrdersTable));
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				new JScrollPane(ordersTable), panel);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(250);
		facade.addTab(text.get(TITLE), split);
		
		Menu menu = facade.getMainMenu()
			.addMenu(MENU_ORDER, text.get(MENU_ORDER));
		cmdCancel = menu.addItem(MENU_ORDER_CANCEL,text.get(MENU_ORDER_CANCEL));
		cmdCancel.OnCommand().addListener(this);
		cmdCancel.setEnabled(false); // Изначально ведь ничего не выбрано
	}
	
	private void onTableRowSelect() {
		if ( ordersTable.getSelectedRowCount() > 0
				|| stopOrdersTable.getSelectedRowCount() > 0 )
		{
			cmdCancel.setEnabled(true);
		} else {
			cmdCancel.setEnabled(false);
		}
	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
	
}
