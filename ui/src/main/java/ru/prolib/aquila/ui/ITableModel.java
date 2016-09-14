package ru.prolib.aquila.ui;

import javax.swing.table.TableModel;

public interface ITableModel extends TableModel {
	
	/**
	 * Return column index by ID.
	 * <p>
	 * @param columnID - column ID
	 * @return index of the specified column
	 */
	public int getColumnIndex(int columnID);
	
	/**
	 * Return column ID by its index.
	 * <p>
	 * @param columnIndex - the column index
	 * @return ID of the specified column
	 */
	public int getColumnID(int columnIndex);
	
	/**
	 * Close all used resources and unsubscribe from events.
	 */
	public void close();
	
	public void startListeningUpdates();
	
	public void stopListeningUpdates();

}
