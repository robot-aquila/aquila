package ru.prolib.aquila.ui.wrapper;

import java.util.List;

import javax.swing.JTable;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.data.row.RowAdapter;

/**
 * $Id$
 */
public interface Table extends Starter {
	/**
	 * Возвращает объект типа JTable
	 * @return JTable
	 */
	public JTable getUnderlayed();
	
	/**
	 * Возвращает количесво выделенных строк
	 * @return Integer
	 */
	public int getSelectedRowCount();
	
	/**
	 * Возвращает тип события выбора строк
	 * @return EventType
	 */
	public EventType OnRowSelected();
	/**
	 * Возвращает список выбранных строк, завернутых в RowAdapter
	 * 
	 * @return List<RowAdapter>
	 */
	public List<RowAdapter> getSelectedRows();
}
