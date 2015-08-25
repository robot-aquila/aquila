package ru.prolib.aquila.quik.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.ui.table.Table;

/**
 * Окно просмотра DDE-кэша.
 */
public class CacheWindow {
	private static final Logger logger;
	
	public static final String TEXT_WIN_CACHE_TITLE;
	public static final String TEXT_TAB_CACHE_DESCRS;
	public static final String TEXT_TAB_CACHE_TRADES;
	public static final String TEXT_TAB_CACHE_POSITIONS;
	public static final String TEXT_TAB_CACHE_ORDERS;
	public static final String TEXT_TAB_CACHE_OWNTRADES;
	
	static {
		logger = LoggerFactory.getLogger(CacheWindow.class);
		TEXT_WIN_CACHE_TITLE = "WIN_CACHE_TITLE";
		TEXT_TAB_CACHE_ORDERS = "TAB_CACHE_ORDERS";
		TEXT_TAB_CACHE_TRADES = "TAB_CACHE_TRADES";
		TEXT_TAB_CACHE_DESCRS = "TAB_CACHE_DESCRS";
		TEXT_TAB_CACHE_POSITIONS = "TAB_CACHE_POSITIONS";
		TEXT_TAB_CACHE_OWNTRADES = "TAB_CACHE_OWNTRDS";
	}
	
	private final JDialog window;
	private final QUIKTerminal terminal;
	private final IMessages labels;
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final LinkedList<Table> tables = new LinkedList<Table>();
	
	public CacheWindow(JFrame owner, QUIKTerminal terminal, IMessages labels)	{
		super();
		window = new JDialog(owner);
		this.terminal = terminal;
		this.labels = labels;
	}
	
	public void init() {
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { onHide(); }
		});
		TableBuilder builder = new TableBuilder(labels, terminal.getDataCache());
		addTab(builder.createDescriptorsTable(), TEXT_TAB_CACHE_DESCRS);
		addTab(builder.createTradesTable(), TEXT_TAB_CACHE_TRADES);
		addTab(builder.createPositionsCacheTable(), TEXT_TAB_CACHE_POSITIONS);
		addTab(builder.createOrdersTable(), TEXT_TAB_CACHE_ORDERS);
		addTab(builder.createOwnTradesTable(), TEXT_TAB_CACHE_OWNTRADES);

		window.add(tabbedPane);
		window.setTitle(labels.get(TableBuilder.msgID(TEXT_WIN_CACHE_TITLE)));
		window.setPreferredSize(new Dimension(1100, 600));
		window.pack();
	}
	
	private void addTab(Table table, String titleId) {
		tables.add(table);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		tabbedPane.add(labels.get(TableBuilder.msgID(titleId)), scrollPane);
	}
	
	public void showWindow() {
		if ( ! window.isShowing() ) {
			onShow();
			window.setVisible(true);
		}
	}
	
	protected void onHide() {
		for ( Table table : tables ) {
			table.stop();
		}
		logger.debug("Hide DDE cache window");
	}
	
	protected void onShow() {
		logger.debug("Show DDE cache window");
		for ( Table table : tables ) {
			table.start();
		}
	}

}
