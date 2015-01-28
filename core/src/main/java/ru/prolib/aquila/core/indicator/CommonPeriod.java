package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.EditableSeries;

/**
 * Заготовка под типовой индикатор, основанный на периоде.
 * <p>
 * 2012-05-14<br>
 * $Id: CommonPeriod.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public abstract class CommonPeriod extends Common<Double> {
	protected final int period;

	/**
	 * Создать объект.
	 * <p>
	 * @param target целевое значение
	 * @param period период индикатора
	 */
	public CommonPeriod(EditableSeries<Double> target, int period) {
		super(target);
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period cannot be less than 2");
		}
		this.period = period;
	}

	/**
	 * Получить период индикатора
	 * <p>
	 * @return период индикатора
	 */
	public int getPeriod() {
		return period;
	}

}