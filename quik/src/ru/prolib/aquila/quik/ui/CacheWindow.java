package ru.prolib.aquila.quik.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;

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
	
	public static final String TEXT_WIN_CACHE_TITLE;
	public static final String TEXT_TAB_CACHE_ORDERS;
	public static final String TEXT_TAB_CACHE_TRADES;
	public static final String TEXT_TAB_CACHE_SECURITIES;
	public static final String TEXT_TAB_CACHE_PORTS_F;
	public static final String TEXT_TAB_CACHE_POSS_F;
	public static final String TEXT_TAB_CACHE_STOP_ORDERS;
	
	static {
		logger = LoggerFactory.getLogger(CacheWindow.class);
		TEXT_WIN_CACHE_TITLE = "WIN_CACHE_TITLE";
		TEXT_TAB_CACHE_ORDERS = "TAB_CACHE_ORDERS";
		TEXT_TAB_CACHE_TRADES = "TAB_CACHE_TRADES";
		TEXT_TAB_CACHE_SECURITIES = "TAB_CACHE_SECURITIES";
		TEXT_TAB_CACHE_PORTS_F = "TAB_CACHE_PORTS_F";
		TEXT_TAB_CACHE_POSS_F = "TAB_CACHE_POSS_F";
		TEXT_TAB_CACHE_STOP_ORDERS = "TAB_CACHE_STOP_ORDERS";
	}
	
	private final JDialog window;
	private final QUIKTerminal terminal;
	private final ClassLabels labels;
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final LinkedList<Table> tables = new LinkedList<Table>();
	
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
		addTab(builder.createOrdersCacheTable(), TEXT_TAB_CACHE_ORDERS);
		addTab(builder.createTradesCacheTable(), TEXT_TAB_CACHE_TRADES);
		addTab(builder.createSecuritiesCacheTable(), TEXT_TAB_CACHE_SECURITIES);
		addTab(builder.createPortfoliosFortsCacheTable(),TEXT_TAB_CACHE_PORTS_F);
		addTab(builder.createPositionsFortsCacheTable(), TEXT_TAB_CACHE_POSS_F);
		addTab(builder.createStopOrdersCacheTable(),TEXT_TAB_CACHE_STOP_ORDERS);

		window.add(tabbedPane);
		window.setTitle(labels.get(TEXT_WIN_CACHE_TITLE));
		window.setPreferredSize(new Dimension(1100, 600));
		window.pack();
	}
	
	private void addTab(Table table, String titleId) {
		tables.add(table);
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
