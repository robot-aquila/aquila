package ru.prolib.aquila.ui;

import javax.swing.table.TableModel;

import ru.prolib.aquila.core.text.MsgID;

public interface ITableModel extends TableModel {
	
	/**
	 * Return column index by ID.
	 * <p> 
	 * @param msgId - message ID.
	 * @return return index of specified column
	 */
	public int getColumnIndex(MsgID msgID);
	
	/**
	 * Close all used resources and unsubscribe from events.
	 */
	public void close();
	
	public void startListeningUpdates();
	
	public void stopListeningUpdates();

}
