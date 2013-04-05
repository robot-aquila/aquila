package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Заготовка типового индикатора. Используется для индикаторов с двумя
 * источниками значений. 
 * 
 * $Id: BaseIndicator2S.java 205 2012-04-06 15:41:16Z whirlwind $
 */
abstract public class BaseIndicator2S<T,P> implements ValueSource<T> {
	protected final Value<P> src1, src2;

	/**
	 * Конструктор.
	 * @param src1 первый источник значений
	 * @param src2 второй источник значений
	 */
	public BaseIndicator2S(Value<P> src1, Value<P> src2) {
		super();
		if ( src1 == null ) {
			throw new NullPointerException("First source cannot be null");
		}
		if ( src2 == null ) {
			throw new NullPointerException("Second source cannot be null");
		}
		this.src1 = src1;
		this.src2 = src2;
	}
	
	/**
	 * Получить первый источник значений
	 * @return
	 */
	final public Value<P> getFirstSource() {
		return src1;
	}
	
	/**
	 * Получить второй источник значений
	 * @return
	 */
	final public Value<P> getSecondSource() {
		return src2;
	}

}
