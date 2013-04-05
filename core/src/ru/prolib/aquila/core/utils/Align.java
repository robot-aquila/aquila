package ru.prolib.aquila.core.utils;

/**
 * Интерфейс алгоритма выравнивания значения.
 * <p>
 * @param <T> - тип выравниваемого значения
 * <p>
 * 2013-03-02<br>
 * $Id: Align.java 556 2013-03-04 17:18:03Z whirlwind $
 */
public interface Align<T> {
	
	/**
	 * Выровнять значение.
	 * <p>
	 * @param value исходное значение
	 * @return выровненное значение
	 */
	public T align(T value);

}
