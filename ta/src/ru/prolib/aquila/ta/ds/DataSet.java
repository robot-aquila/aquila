package ru.prolib.aquila.ta.ds;

import java.util.Date;

/**
 * Интерфейс набора именованных данных.
 * Используется для унификации доступа к наборам базовых типов.
 * 
 * $Id$
 */
public interface DataSet {
	
	/**
	 * Получить число двойной точности
	 * @param name наименование элемента 
	 * @return
	 * @throws DataSetException
	 */
	public Double getDouble(String name) throws DataSetException;
	
	/**
	 * Получить строку
	 * @param name наименование элемента
	 * @return
	 * @throws DataSetException
	 */
	public String getString(String name) throws DataSetException;
	
	/**
	 * Получить дату
	 * @param name наименование элемента
	 * @return
	 * @throws DataSetException
	 */
	public Date getDate(String name) throws DataSetException;
	
	/**
	 * Получить целое
	 * @param name наименование элемента
	 * @return
	 * @throws DataSetException
	 */
	public Long getLong(String name) throws DataSetException;

}
