package ru.prolib.aquila.core.data;

/**
 * Интерфейс доступа к значению типа (геттер).
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2012-08-22<br>
 * $Id: G.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public interface G<R> {
	
	/**
	 * Получить элемент.
	 * <p>
	 * @param source источник
	 * @return значение
	 */
	public R get(Object source);

}