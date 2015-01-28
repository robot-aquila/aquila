package ru.prolib.aquila.core.indicator.function;

import ru.prolib.aquila.core.indicator.*;

/**
 * Заготовка функции Moving Average.
 */
public abstract class MAFunction implements ComplexFunction<Double, Double> {
	/**
	 * Период по-умолчанию.
	 */
	public static final int DEFAULT_PERIOD = 9;
	protected int period;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param period период скользящей средней
	 */
	public MAFunction(int period) {
		super();
		this.period = period;
	}
	
	/**
	 * Конструктор с периодом по-умолчанию.
	 */
	public MAFunction() {
		this(DEFAULT_PERIOD);
	}
	
	/**
	 * Установить период скользящей средней.
	 * <p>
	 * @param period период
	 */
	public synchronized void setPeriod(int period) {
		this.period = period;
	}
	
	/**
	 * Получить период скользящей средней.
	 * <p>
	 * @return период
	 */
	public synchronized int getPeriod() {
		return period;
	}

}
