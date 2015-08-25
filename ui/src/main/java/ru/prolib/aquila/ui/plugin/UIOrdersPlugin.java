package ru.prolib.aquila.ui.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.form.OrderListTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;
import ru.prolib.aquila.ui.wrapper.*;

/**
 * Плагин отображающий заявки в таблицах на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UIOrdersPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UIOrdersPlugin implements AquilaPlugin {
	public static final String MENU_ORDER = CommonMsg.MENU_ORDER.toString();
	public static final String MENU_ORDER_CANCEL = CommonMsg.MENU_ORDER_CANCEL.toString();

	private Terminal terminal;
	private OrderListTableModel model;
	private JTable table;
	private JMenuItem cmdCancel;

	@Override
	public void start() throws StarterException {
		model.start();
	}

	@Override
	public void stop() throws StarterException {
		model.stop();
	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String arg)
	{
		this.terminal = terminal;
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		model = new OrderListTableModel(facade.getTexts());
		model.add(terminal);
		table = new JTable(model);
		table.getSelectionModel()
			.addListSelectionListener(new ListSelectionListener() {
				@Override public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting() ) { onTableRowSelect(); } }
    		});
		
		IMessages texts = facade.getTexts();
        JPanel panel = new JPanel(new BorderLayout());
		facade.addTab(texts.get(CommonMsg.ORDERS), panel);
        panel.add(new JScrollPane(table));
		
		Menu menu = facade.getMainMenu()
			.addMenu(MENU_ORDER, texts.get(CommonMsg.MENU_ORDER));
		cmdCancel = menu.addItem(MENU_ORDER_CANCEL, texts.get(CommonMsg.MENU_ORDER_CANCEL))
				.getUnderlyingObject();
		cmdCancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		cmdCancel.setEnabled(false); // Изначально ведь ничего не выбрано
	}
	
	private void onTableRowSelect() {
		cmdCancel.setEnabled(table.getSelectedRowCount() > 0);
	}
	
}
