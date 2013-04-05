package ru.prolib.aquila.ui.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.data.G;
/**
 * $Id: TableModel.java 575 2013-03-13 23:40:00Z huan.kaktus $
 */
public class TableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 5073295254324372206L;
	private List<String> index = new Vector<String>();
	private Map<String, TableColumn> cols = new HashMap<String, TableColumn>();
	private Map<String, G<?>> getters = new HashMap<String, G<?>>();
	
	
	public TableModel() {
		super();
	}
	
	public synchronized void addColumn(TableColumn column) throws TableColumnAlreadyExistsException {
		if(isColumnExists(column.getName())) {
			throw new TableColumnAlreadyExistsException(column.getName());
		}
		cols.put(column.getName(), column);
		getters.put(column.getName(), column.getGetter());
		index.add(column.getName());
	}
	
	public synchronized boolean isColumnExists(String colId) {
		return cols.containsKey(colId);
	}
	
	public synchronized TableColumn getColumn(String name) throws TableColumnNotExistsException {
		if(! isColumnExists(name)) {
			throw new TableColumnNotExistsException(name);
		}
		return cols.get(name);
	}
	
	public List<String> getIndex() {
		return index;
	}
	
	public Map<String, G<?>> getGetters() {
		return getters;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
