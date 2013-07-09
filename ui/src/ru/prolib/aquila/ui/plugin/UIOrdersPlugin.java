package ru.prolib.aquila.ui.plugin;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.wrapper.*;

/**
 * Плагин отображающий заявки в таблицах на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UIOrdersPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UIOrdersPlugin implements AquilaPlugin, EventListener {
	public static final String TEXT_SECT = "UIOrdersPlugin";
	public static final String TITLE = "TAB_ORDERS";
	public static final String MENU_ORDER = "MENU_ORDER";
	public static final String MENU_ORDER_CANCEL = "MENU_ORDER_CANCEL";

	private Terminal terminal;
	private OrdersTableModel model;
	private JTable table;
	private MenuItem cmdCancel;

	@Override
	public void start() throws StarterException {
		model.start();
	}

	@Override
	public void stop() throws StarterException {
		model.stop();
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		model = new OrdersTableModel(terminal, facade.getTexts());
		table = new JTable(model);
		table.getSelectionModel()
			.addListSelectionListener(new ListSelectionListener() {
				@Override public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting() ) { onTableRowSelect(); } }
    		});
		
		ClassLabels text = facade.getTexts().get(TEXT_SECT);
        JPanel panel = new JPanel(new BorderLayout());
		facade.addTab(facade.getTexts().get(TEXT_SECT).get(TITLE), panel);
        panel.add(new JScrollPane(table));
		
		Menu menu = facade.getMainMenu()
			.addMenu(MENU_ORDER, text.get(MENU_ORDER));
		cmdCancel = menu.addItem(MENU_ORDER_CANCEL,text.get(MENU_ORDER_CANCEL));
		cmdCancel.OnCommand().addListener(this);
		cmdCancel.setEnabled(false); // Изначально ведь ничего не выбрано
	}
	
	private void onTableRowSelect() {
		cmdCancel.setEnabled(table.getSelectedRowCount() > 0);
	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
	
}
