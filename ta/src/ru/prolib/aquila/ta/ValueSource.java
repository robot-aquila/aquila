package ru.prolib.aquila.ta;

/**
 * Источник значений.
 */
public interface ValueSource<T> {
	
	/**
	 * Расчитать текущее значение.
	 * 
	 * @return текущее значение
	 * @throws ValueException
	 */
	public T calculate() throws ValueException;

}
