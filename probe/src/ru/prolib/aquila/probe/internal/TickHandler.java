package ru.prolib.aquila.probe.internal;

import ru.prolib.aquila.core.data.Tick;

/**
 * Обработчик тиковых данных.
 */
public interface TickHandler {

	/**
	 * Вызывается при получении первого тика.
	 * <p>
	 * @param firstTick тик данных
	 */
	public void doInitialTask(Tick firstTick);
	
	/**
	 * Вызывается при получении финального тика.
	 * <p>
	 * @param lastTick тик данных
	 */
	public void doFinalTask(Tick lastTick);

	/**
	 * Вызывается при получении тика более поздних суток.
	 * <p>
	 * @param prevDateTick предыдущий тик (предыдущих суток). Может быть null.
	 * @param nextDateTick тик следующих суток
	 */
	public void doDailyTask(Tick prevDateTick, Tick nextDateTick);
	
	/**
	 * Вызывается для каждого тика данных.
	 * <p>
	 * @param tick тик данных
	 * @return задача связанная с тиком
	 */
	public Runnable createTask(Tick tick);
	
}
