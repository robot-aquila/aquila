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
	
	static {
		logger = LoggerFactory.getLogger(CacheWindow.class);
	}
	
	private final JDialog window;
	private final QUIKTerminal terminal;
	private final ClassLabels labels;
	private Table ordersCacheTable;
	
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
		ordersCacheTable.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(ordersCacheTable);
		window.add(scrollPane);
		window.setTitle(labels.get(TEXT_WIN_CACHE_TITLE));
		window.setPreferredSize(new Dimension(800, 600));
		window.pack();
	}
	
	public void showWindow() {
		if ( ! window.isShowing() ) {
			onShow();
			window.setVisible(true);
		}
	}
	
	protected void onHide() {
		ordersCacheTable.stop();
		logger.debug("Hide DDE cache window");
	}
	
	protected void onShow() {
		logger.debug("Show DDE cache window");
		ordersCacheTable.start();
	}

}
