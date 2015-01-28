package ru.prolib.aquila.core.data;

/**
 * Интерфейс сеттера.
 * <p>
 * @param <T> - тип объекта доступа
 * <p>
 * 2012-08-22<br>
 * $Id: S.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public interface S<T> {
	
	/**
	 * Установить элемент.
	 * <p>
	 * @param object объект модификации
	 * @param value значение элемента
	 * @throws ValueException
	 */
	public void set(T object, Object value) throws ValueException;

}
