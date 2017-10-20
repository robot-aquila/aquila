package ru.prolib.aquila.ib.ui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.table.Table;

/**
 * Окно просмотра кэша данных.
 */
public class IBCacheWindow {
	private static final Logger logger;
	public static final String T_WIN_TITLE;
	public static final String T_TAB_CONTRACTS;
	public static final String T_TAB_EXECUTIONS;
	public static final String T_TAB_ORDERS;
	public static final String T_TAB_ORDERSTATUSES;
	public static final String T_TAB_POSITIONS;
	
	static {
		logger = LoggerFactory.getLogger(IBCacheWindow.class);
		T_WIN_TITLE = "WIN_CACHE_TITLE";
		T_TAB_CONTRACTS = "CACHE_CONTR_TAB";
		T_TAB_EXECUTIONS = "CACHE_EXEC_TAB";
		T_TAB_ORDERS = "CACHE_ORDER_TAB";
		T_TAB_ORDERSTATUSES = "CACHE_ORDERST_TAB";
		T_TAB_POSITIONS = "CACHE_POSITION_TAB";
	}
	
	private final IBEditableTerminal terminal;
	private final JDialog window;
	private final ClassLabels labels;
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final LinkedList<Table> tables = new LinkedList<Table>();
	
	public IBCacheWindow(IBEditableTerminal terminal, JFrame owner,
			ClassLabels labels)
	{
		super();
		window = new JDialog(owner);
		this.terminal = terminal;
		this.labels = labels;
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
	
	public void init() {
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { onHide(); }
		});
		buildTables();
		window.add(tabbedPane);
		window.setTitle(labels.get(T_WIN_TITLE));
		window.setPreferredSize(new Dimension(1100, 600));
		window.pack();
	}
	
	private void buildTables() {
		IBCacheTableBuilder tb =
			new IBCacheTableBuilder(labels, terminal.getCache());
		addTab(tb.createContractTable(), T_TAB_CONTRACTS);
		addTab(tb.createExecTable(), T_TAB_EXECUTIONS);
		addTab(tb.createOrderTable(), T_TAB_ORDERS);
		addTab(tb.createOrderStatusTable(), T_TAB_ORDERSTATUSES);
		addTab(tb.createPositionTable(), T_TAB_POSITIONS);
	}

}
