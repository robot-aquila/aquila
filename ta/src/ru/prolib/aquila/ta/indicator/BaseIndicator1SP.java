package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Заготовка типового индикатора.
 * Используется для индикаторов основанных на периоде с единственным источником
 * значений. 
 * 
 * $Id: BaseIndicator1SP.java 206 2012-04-07 14:06:09Z whirlwind $
 */
abstract public class BaseIndicator1SP<T> implements ValueSource<T> {
	protected final Value<T> src;
	protected final int period;

	/**
	 * Конструктор.
	 * @param src источник значений
	 * @param period период
	 */
	public BaseIndicator1SP(Value<T> src, int period) {
		super();
		if ( src == null ) {
			throw new NullPointerException("Source cannot be null");
		}
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period cannot be less than 2");
		}
		this.src = src;
		this.period = period;
	}
	
	/**
	 * Получить источник значений
	 * @return
	 */
	final public Value<T> getSource() {
		return src;
	}
	
	/**
	 * Получить период
	 * @return
	 */
	final public int getPeriod() {
		return period;
	}

}
