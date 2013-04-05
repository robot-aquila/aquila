package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Сглаженная скользящая средняя по рецепту из QUIK-а.
 * До тех пор, пока история источника содержит значений в количестве менее или
 * равном установленному периоду, текущее значение индикатора рассчитывается
 * как SMA.
 * 
 * Формул рассчета SMMA было найдено до едрени фени:
 * 
 * адын - http://www.metatrader5.com/ru/terminal/help/analytics/indicators/trend_indicators/ma
 * дыва - http://20minutetraders.com/learn/moving-averages/smoothed-moving-average-calculation
 * тыры - http://enc.fxeuroclub.ru/409/
 * чтыр - метод из документации QUIK-а
 * 
 * Эта реализация по методу чтыр.
 * 
 * TODO: добавить обработку null в источнике
 */
public class SmmaQuik extends BaseIndicator1SP<Double> {
	private Double previous = null;

	/**
	 * Конструктор.
	 * @param src источник значений
	 * @param period период
	 */
	public SmmaQuik(Value<Double> src, int period) {
		super(src, period);
	}

	@Override
	public synchronized Double calculate() throws ValueException {
		int length = src.getLength();
		if ( length == 0 ) return null;
		double sum = 0.0d;
		int divisor = length;
		if ( length <= period ) {
			for ( int i = 0; i < length; i ++ ) {
				sum += src.get(i);
			}
		} else {
			for ( int i = length - period; i < length; i ++ ) {
				sum += src.get(i);
			}
			sum = sum - previous + src.get();
			divisor = period;
		}
		return previous = sum / divisor;
	}

}
