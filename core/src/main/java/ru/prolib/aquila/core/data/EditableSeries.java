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
	 * Добавить значение в конец последовательности.
	 * <p>
	 * @param value - value to add.
	 * @throws ValueException - If error occurred.
	 */
	public void add(T value) throws ValueException;
	
	/**
	 * Удалить все значения.
	 */
	public void clear();
	
	void truncate(int new_length);

}
