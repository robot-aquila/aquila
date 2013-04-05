package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Стандартное отклонение.
 * 
 * Пошагово вычисление стандартного отклонения:
 * 1.вычисляем среднее арифметическое выборки данных
 * 2.отнимаем это среднее от каждого элемента выборки
 * 3.все полученные разницы возводим в квадрат
 * 4.суммируем все полученные квадраты
 * 5.делим полученную сумму на количество элементов в выборке
 * (или на n-1, если n>30)
 * 6.вычисляем квадратный корень из полученного частного (именуемого дисперсией)
 * 
 * отсюда: http://berg.com.ua/tech/indicators-overlays/stdev/
 *
 * 2012-02-07
 * $Id: Stdev.java 205 2012-04-06 15:41:16Z whirlwind $
 */
public class Stdev extends BaseIndicator1SP<Double> {
	
	/**
	 * Конструктор.
	 * @param src источник значений
	 * @param period период
	 */
	public Stdev(Value<Double> src, int period) {
		super(src, period);
	}

	@Override
	public synchronized Double calculate() throws ValueException {
		int length = src.getLength();
		int n = length > period ? period : length;
		if ( length > 1 && hasNoNulls(n) ) {
			double avg = src.get();
			for ( int i = 1; i < n; i ++ ) {
				avg += src.get(-i);
			}
			avg /= n;
			double sum = Math.pow(src.get() - avg, 2);
			for ( int i = 1; i < n; i ++ ) {
				sum += Math.pow(src.get(-i) - avg, 2);
			}
			double quo = sum / (n - 1);
			double dev = Math.sqrt(quo);
			return dev;
		}
		return null;
	}
	
	final private boolean hasNoNulls(int lastCount) throws ValueException {
		int length = src.getLength();
		for ( int i = length - lastCount; i < length; i ++ ) {
			if ( src.get(i) == null ) {
				return false;
			}
		}
		return true;
	}

}
