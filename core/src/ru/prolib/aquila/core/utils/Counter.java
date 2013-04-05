package ru.prolib.aquila.core.utils;

/**
 * Интерфейс счетчика.
 * <p>
 * 2012-11-16<br>
 * $Id: Counter.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public interface Counter {

	/**
	 * Получить текущее значение счетчика.
	 * <p>
	 * @return значение счетчика
	 */
	public int get();
	
	/**
	 * Установить значение счетчика.
	 * <p>
	 * @param value значение счетчика
	 */
	public void set(int value);

	/**
	 * Увеличить значение счетчика на единицу.
	 */
	public void increment();
	
	/**
	 * Получить значение счетчика после чего увеличить его на единицу.
	 * <p>
	 * @return текущее значение счетчика
	 */
	public int getAndIncrement();
	
	/**
	 * Увеличить значение счетчика на единицу и получить значение.
	 * <p>
	 * @return значение счетчика
	 */
	public int incrementAndGet();
	
	/**
	 * Получить значение счетчика после чего уменьшить его на единицу.
	 * <p>
	 * @return текущее значение счетчика
	 */
	public int getAndDecrement();
	
	/**
	 * Уменьшить значение счетчика на единицу и получить значение.
	 * <p>
	 * @return значение счетчика
	 */
	public int decrementAndGet();
	
	/**
	 * Уменьшить значение счетчика на единицу.
	 */
	public void decrement();

}