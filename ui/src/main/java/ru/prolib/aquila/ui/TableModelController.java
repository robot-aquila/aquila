package ru.prolib.aquila.ui;

import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TableModelController extends WindowAdapter implements ComponentListener {
	private ITableModel tableModel;
	private boolean subscribed = false;
	
	public TableModelController(ITableModel tableModel) {
		super();
		this.tableModel = tableModel;
	}
	
	public TableModelController(ITableModel tableModel, Window window) {
		super();
		this.tableModel = tableModel;
		window.addWindowListener(this);
		window.addComponentListener(this);
	}
	
	private void subscribe() {
		if ( ! subscribed ) {
			tableModel.startListeningUpdates();
			subscribed = true;
		}
	}
	
	private void unsubscribe() {
		if ( subscribed ) {
			tableModel.stopListeningUpdates();
			subscribed = false;
		}
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		unsubscribe();
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		unsubscribe();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		unsubscribe();
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentResized(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {
		subscribe();
	}

}
