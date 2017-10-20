package ru.prolib.aquila.ui.table;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.text.IMessages;

/**
 * Модель таблицы DDE кэша.
 */
public class TableModel extends AbstractTableModel implements Starter {
	private static final long serialVersionUID = -6556813084983548987L;
	protected final IMessages messages;
	protected final Columns columns;
	@SuppressWarnings("rawtypes")
	protected List rows;

	@SuppressWarnings("rawtypes")
	public TableModel(IMessages messages, Columns columns) {
		super();
		this.messages = messages;
		this.columns = columns;
		rows = new Vector();
	}
	
	public Columns getColumns() {
		return columns;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return columns.getCount();
	}
	
	@Override
	public String getColumnName(int index) {
		return messages.get(columns.get(index).getId());
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			return columns.get(columnIndex).getGetter().get(rows.get(rowIndex));
		} catch ( ValueException e ) {
			return e.getMessage();
		}
	}

	protected void invalidate() {
		fireTableDataChanged();
	}

	@Override
	public void start() {
		invalidate();
	}

	@Override
	public void stop() {
		
	}

}