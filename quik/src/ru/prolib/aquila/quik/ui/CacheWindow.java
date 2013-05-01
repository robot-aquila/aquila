package ru.prolib.aquila.quik.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.ui.ClassLabels;

/**
 * Окно просмотра DDE-кэша.
 */
public class CacheWindow {
	private static final long serialVersionUID = 5687784287521612167L;
	private static final Logger logger;
	
	public static final String TEXT_WIN_CACHE_TITLE = "WIN_CACHE_TITLE";
	public static final String TEXT_TAB_CACHE_ORDERS = "TAB_CACHE_ORDERS";
	public static final String TEXT_TAB_CACHE_TRADES = "TAB_CACHE_TRADES";
	
	static {
		logger = LoggerFactory.getLogger(CacheWindow.class);
	}
	
	private final JDialog window;
	private final QUIKTerminal terminal;
	private final ClassLabels labels;
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private Table ordersCacheTable;
	private Table tradesCacheTable;
	
	public CacheWindow(JFrame owner, QUIKTerminal terminal,
			ClassLabels labels)
	{
		super();
		window = new JDialog(owner);
		this.terminal = terminal;
		this.labels = labels;
	}
	
	public void init() {
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { onHide(); }
		});
		
		TableBuilder builder = new TableBuilder(labels, terminal.getDdeCache());
		ordersCacheTable = builder.createOrdersCacheTable();
		tradesCacheTable = builder.createTradesCacheTable();
		addTab(ordersCacheTable, TEXT_TAB_CACHE_ORDERS);
		addTab(tradesCacheTable, TEXT_TAB_CACHE_TRADES);
		//ordersCacheTable.setFillsViewportHeight(true);
		//JScrollPane scrollPane = new JScrollPane(ordersCacheTable);
		//window.add(scrollPane);
		window.add(tabbedPane);
		window.setTitle(labels.get(TEXT_WIN_CACHE_TITLE));
		window.setPreferredSize(new Dimension(800, 600));
		window.pack();
	}
	
	private void addTab(Table table, String titleId) {
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		tabbedPane.add(labels.get(titleId), scrollPane);
	}
	
	public void showWindow() {
		if ( ! window.isShowing() ) {
			onShow();
			window.setVisible(true);
		}
	}
	
	protected void onHide() {
		ordersCacheTable.stop();
		tradesCacheTable.stop();
		logger.debug("Hide DDE cache window");
	}
	
	protected void onShow() {
		logger.debug("Show DDE cache window");
		tradesCacheTable.start();
		ordersCacheTable.start();
	}

}
