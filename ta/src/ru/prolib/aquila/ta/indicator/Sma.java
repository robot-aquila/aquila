package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Простая скользящая средняя.
 * 
 * Рассчитывается как среднее арифметическое значений источника за указанный
 * период. Если период больше чем количество значений в источнике, то за
 * период берется длина истории.   
 */
public class Sma extends BaseIndicator1SP<Double> {

	/**
	 * Конструктор.
	 * @param src источник значений
	 * @param period период
	 */
	public Sma(Value<Double> src, int period) {
		super(src, period);
	}
	
	@Override
	public synchronized Double calculate() throws ValueException {
		int total = src.getLength();
		if ( total == 0 ) return null;
		int count = total > period ? period : total;
		int first = total - count;
		double value = 0.0d;
		for ( int i = 0; i < count; i ++ ) {
			value += src.get(first + i);
		}
		return value / count;
	}

}
