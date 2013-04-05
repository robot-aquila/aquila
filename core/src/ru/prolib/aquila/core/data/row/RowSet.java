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
	 */
	public boolean next();
	
	/**
	 * Сбросить указатель на начало набора.
	 * <p>
	 * Фактически вызов данного метода приводит объект в первоначальное
	 * состояние, соответствующее состоянию до первого вызова {@link #next()}.
	 */
	public void reset();
	
	/**
	 * Завершить работу с набором.
	 */
	public void close();

}
