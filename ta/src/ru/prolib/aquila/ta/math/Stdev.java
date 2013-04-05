package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

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
@Deprecated
public class Stdev extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Stdev stdev;
	
	public Stdev(Value<Double> source, int period) {
		this(source, period, ValueImpl.DEFAULT_ID);
	}
	
	public Stdev(Value<Double> source, int period, String id) {
		super(id);
		stdev = new ru.prolib.aquila.ta.indicator.Stdev(source, period);
	}
	
	public int getPeriods() {
		return stdev.getPeriod();
	}
	
	public Value<Double> getSourceValue() {
		return stdev.getSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(stdev.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
