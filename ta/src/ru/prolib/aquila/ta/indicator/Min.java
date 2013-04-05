package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Рассчитывает минимальное значение источника за указанный период.
 */
public class Min extends BaseIndicator1SP<Double> {

	/**
	 * Конструктор.
	 * @param src источник значений
	 * @param period период
	 */
	public Min(Value<Double> src, int period) {
		super(src, period);
	}

	@Override
	public synchronized Double calculate() throws ValueException {
		int count = period;
		if ( count > src.getLength() ) {
			count = src.getLength();
		}
		if ( count == 0 ) return null;
		Double min = src.get();
		for ( int i = 1; i < count; i ++ ) {
			Double cur = src.get(-i);
			if ( cur != null && (min == null || cur < min) ) {
				min = cur;
			}
		}
		return min;
	}

}
