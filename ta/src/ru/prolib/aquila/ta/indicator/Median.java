package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Медиана.
 * Рассчитывает среднее арифметическое от двух источников.
 * Обычно используется для рассчета средней цены бара по формуле (HIGH+LOW)/2
 */
public class Median extends BaseIndicator2S<Double, Double> {
	
	/**
	 * Конструктор
	 * @param src1 первый источник значений
	 * @param src2 второй источник значений
	 */
	public Median(Value<Double> src1, Value<Double> src2) {
		super(src1, src2);
	}

	@Override
	public synchronized Double calculate() throws ValueException {
		Double val1 = src1.get();
		Double val2 = src2.get();
		if ( val1 == null || val2 == null ) {
			return null;
		}
		return (val1 + val2) / 2;
	}

}
