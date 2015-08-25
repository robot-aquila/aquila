package ru.prolib.aquila.ui.wrapper;

import java.util.List;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.data.row.RowAdapter;
import ru.prolib.aquila.core.text.MsgID;

/**
 * $Id$
 */
@Deprecated
public interface TableModel extends Starter, EventListener {
	/**
	 * Добавляет столбец в модель
	 * 
	 * @param TableColumnWrp column
	 * @throws TableColumnAlreadyExistsException
	 */
	public void addColumn(TableColumnWrp column)
			throws TableColumnAlreadyExistsException;

	/**
	 * Поверяет, не существует ли уже такой столбец
	 * 
	 * @param colId - column ID
	 * @return boolean
	 */
	public boolean isColumnExists(MsgID colId);

	/**
	 * Устанавливает объект-слущатель для прослушивания источника данных 
	 * на предмет появления новой строки
	 * 
	 * @param DataSourceEventTranslator onRowAvailableListener
	 */
	public void setOnRowAvailableListener(
			DataSourceEventTranslator onRowAvailableListener);
	
	/**
	 * Возвращает объект-слущатель для прослушивания источника данных 
	 * на предмет появления новой строки
	 * 
	 * @return DataSourceEventTranslator
	 */
	public DataSourceEventTranslator getOnRowAvailableListener();
	
	/**
	 * Возвращает объект-слущатель для прослушивания источника данных 
	 * на предмет изменения строки
	 * 
	 * @return DataSourceEventTranslator
	 */
	public DataSourceEventTranslator getOnRowChangedListener();
	
	/**
	 * Устанавливает объект-слущатель для прослушивания источника данных 
	 * на предмет изменений строки
	 * 
	 * @param DataSourceEventTranslator onRowChangedListener
	 */
	public void setOnRowChangedListener(
			DataSourceEventTranslator onRowChangedListener);
	
	/**
	 * Возвращает объект столбца по имени
	 * 
	 * @param colId - column ID
	 * @return TableColumn
	 * @throws TableColumnNotExistsException
	 */
	public TableColumnWrp getColumn(MsgID colId) throws TableColumnNotExistsException;
	
	/**
	 * Возвращает объект столбца по индексу
	 * 
	 * @param int i
	 * @return TableColumn
	 * @throws TableColumnNotExistsException
	 */
	public TableColumnWrp getColumn(int i) throws TableColumnNotExistsException;
	
	/**
	 * Возвращает количество столбцов
	 * 
	 * @return int
	 */
	public int getColumnCount();

	/**
	 * @return
	 */
	public List<RowAdapter> getRows();

}