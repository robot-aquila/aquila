package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Расчитывает максимальное значение источника за указанный период.
 */
public class Max extends BaseIndicator1SP<Double> {
	
	/**
	 * Конструктор.
	 * @param src источник значений
	 * @param period период
	 */
	public Max(Value<Double> src, int period) {
		super(src, period);
	}

	@Override
	public synchronized Double calculate() throws ValueException {
		int count = period;
		if ( count > src.getLength() ) {
			count = src.getLength();
		}
		if ( count == 0 ) return null;
		Double max = src.get();
		for ( int i = 1; i < count; i ++ ) {
			Double cur = src.get(-i);
			if ( cur != null && (max == null || cur > max) ) {
				max = cur;
			}
		}
		return max;
	}

}
