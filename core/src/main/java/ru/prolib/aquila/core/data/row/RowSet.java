package ru.prolib.aquila.core.data.row;

/**
 * Интерфейс набора рядов.
 * <p>
 * 2012-08-10<br>
 * $Id: DDERowSet.java 251 2012-08-11 10:48:18Z whirlwind $
 */
public interface RowSet extends Row {
	
	/**
	 * Перейти к следующему ряду.
	 * <p>
	 * @return true в случае успеха, false при достижении конца набора
	 * @throws RowSetException - If error occurred.
	 */
	public boolean next() throws RowSetException;
	
	/**
	 * Сбросить указатель на начало набора.
	 * <p>
	 * Фактически вызов данного метода приводит объект в первоначальное
	 * состояние, соответствующее состоянию до первого вызова {@link #next()}.
	 * @throws RowSetException - If error occurred.
	 */
	public void reset() throws RowSetException;
	
	/**
	 * Завершить работу с набором.
	 */
	public void close();

}
