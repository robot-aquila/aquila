package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;


/**
 * Интерфейс редактируемой последовательности значений.
 * <p>
 * @param <T> тип значения
 * <p>
 * 2012-04-17<br>
 * $Id: EditableSeries.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public interface EditableSeries<T> extends Series<T> {
	
	/**
	 * Установить текущее значение.
	 * <p>
	 * @param value значение
	 * @throws ValueException нет значения для замены
	 * (ни одно значение не было добавлено ранее)
	 */
	public void set(T value) throws ValueException;
	
	/**
	 * Установить значение по индексу.
	 * <p>
	 * @param value значение
	 * @param index индекс значения в последовательности
	 * @throws ValueException
	 */
	public void set(T value, int index) throws ValueException;
	
	/**
	 * Добавить значение.
	 * <p>
	 * @param value
	 */
	public void add(T value);
	
	/**
	 * Удалить все значения.
	 */
	public void clear();

}
