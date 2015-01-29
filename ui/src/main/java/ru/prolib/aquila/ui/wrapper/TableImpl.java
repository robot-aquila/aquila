package ru.prolib.aquila.ui.wrapper;

import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.data.row.RowAdapter;

/**
 * $Id: Table.java 575 2013-03-13 23:40:00Z huan.kaktus $
 */
public class TableImpl implements Table {

	private TableModel model;
	private JTable underlayed;
	
	private EventDispatcher dispatcher;
	private EventType onRowSelected;
	
	public TableImpl(TableModel model, EventDispatcher dispatcher, EventType onRowSelected) {
		super();
		this.model = model;
		this.dispatcher = dispatcher;
		this.onRowSelected = onRowSelected;
	}
	
	public TableModel getModel() {
		return model;
	}
	
	public EventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public EventType OnRowSelected() {
		return onRowSelected;
	}
	
	public JTable getUnderlayed() {
		return underlayed;
	}

	/**
	 * Только для тестов
	 * @param u
	 */
	public void setUnderlayed(JTable u) {
		underlayed = u;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#start()
	 */
	@Override
	public void start() throws StarterException {
		model.start();
		underlayed = new JTable((AbstractTableModel) model);
		for(int i = 0; i < model.getColumnCount(); i++) {			
			try {
				TableColumnWrp col = model.getColumn(i);				
				col.setUnderlayed(underlayed.getColumnModel().getColumn(i));
				col.start();
				
			} catch (TableColumnNotExistsException e) {
				throw new StarterException(e);
			}			
		}
		underlayed.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting() ) { fireOnRowSelected(); }				
			}			
		});
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#stop()
	 */
	@Override
	public void stop() throws StarterException {
		for(int i = 0; i < model.getColumnCount(); i++) {			
			try {
				model.getColumn(i).stop();
				
			} catch (TableColumnNotExistsException e) {
				throw new StarterException(e);
			}			
		}
		
	}
	
	private void fireOnRowSelected() {
		dispatcher.dispatch(new EventImpl(onRowSelected));
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ui.wrapper.Table#getSelectedRowCount()
	 */
	@Override
	public int getSelectedRowCount() {
		return underlayed.getSelectedRowCount();
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.ui.wrapper.Table#getSelectedRows()
	 */
	@Override
	public List<RowAdapter> getSelectedRows() {
		int[] selectedIndex = underlayed.getSelectedRows();
		List<RowAdapter> rows = model.getRows();
		List<RowAdapter> selected = new Vector<RowAdapter>();
		for(int i = 0; i < selectedIndex.length; i++) {
			selected.add(rows.get(selectedIndex[i]));
		}
		return selected;
	}
}
