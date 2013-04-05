package ru.prolib.aquila.core.data;

/**
 * Геттер компонента свечи.
 * <p>
 * @param <T> тип возвращаемого значения
 * <p>
 * 2012-04-26<br>
 * $Id: GCandlePart.java 556 2013-03-04 17:18:03Z whirlwind $
 */
abstract public class GCandlePart<T> implements G<T> {
	
	/**
	 * Получить компоненту свечи.
	 * <p>
	 * @param candle свеча
	 * @return значение компоненты
	 */
	abstract protected T getPart(Candle candle);

	@Override
	public T get(Object source) {
		return source instanceof Candle ? getPart((Candle) source) : null;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == getClass();
	}

}
