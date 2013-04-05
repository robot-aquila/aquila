package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.DataSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Набор математических функций.
 * <p>
 * 2013-03-04<br>
 * $Id: FMathImpl.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class FMathImpl implements FMath {
	
	public FMathImpl() {
		super();
	}
	
	@Override
	public Double max(Double... values) {
		Double max = null;
		for ( Double c : values ) {
			if ( c != null && (max == null || c.compareTo(max) > 0) ) {
				max = c;
			}
		}
		return max;
	}
	
	@Override
	public Double min(Double... values) {
		Double min = null;
		for ( Double c : values ) {
			if ( c != null && (min == null || c.compareTo(min) < 0) ) {
				min = c;
			}
		}
		return min;
	}

	@Override
	public Double abs(Double val) {
		return val == null ? null : Math.abs(val);
	}

	@Override
	public boolean hasNulls(Series<?> value, int index, int period)
			throws ValueException
	{
		if ( value.getLength() == 0 ) {
			return false;
		}
		index = makeIndexPositive(value, index);
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			start = 0;
		}
		for ( ; start <= index; start++ ) {
			if ( value.get(start) == null ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasNulls(Series<?> value, int period) throws ValueException {
		return hasNulls(value, getLastIndex(value), period);
	}

	@Override
	public Double sma(DataSeries value, int period) throws ValueException {
		return sma(value, getLastIndex(value), period);
	}

	@Override
	public Double sma(DataSeries value, int index, int period)
			throws ValueException
	{
		if ( value.getLength() == 0 ) {
			return null;
		}
		if ( hasNulls(value, index, period) ) {
			return null;
		}
		index = makeIndexPositive(value, index);
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			return null; // недостаточно элементов в базе расчета
		}
		Double sum = 0.0d;
		for ( ; start <= index; start ++ ) {
			sum += value.get(start);
		}
		return sum / period;
	}
	
	@Override
	public Double dpo(DataSeries close, int index, int period)
			throws ValueException
	{
		if ( close.getLength() == 0 ) {
			return null;
		}
		index = makeIndexPositive(close, index);
		if ( index < 0 ) {
			return null;
		}
		Double price = close.get(index);
		Double sma = sma(close, index, period / 2 + 1);
		if ( price != null && sma != null ) {
			return price - sma;
		} else {
			return null;
		}
	}
	
	/**
	 * Получить индекс последнего элемента.
	 * <p>
	 * @param value набор значений
	 * @return индекс последнего элемента
	 */
	private int getLastIndex(Series<?> value) {
		return value.getLength() - 1;
	}
	
	/**
	 * Привести индекс к позитивному значению.
	 * <p>
	 * Если указанный индекс меньше нуля, то есть указывает на смещение
	 * относительно конца последовательности значений, то значение индекса
	 * преобразуется в положительное смещение, указывающее на тот же самый
	 * элемент но уже относительно начала данных.
	 * <p>
	 * @param value набор значений
	 * @param index исходный индекс
	 * @return позитивный индекс
	 */
	private int makeIndexPositive(Series<?> value, int index) {
		if ( index < 0 ) {
			index = value.getLength() - 1 + index;
		}
		return index;
	}
	
	/**
	 * Получить индекс первого элемента.
	 * <p>
	 * Расчитывает индекс первого элемента базы расчета, определяющейся индексом
	 * последнего элемента и количеством элементов базы расчета. Возвращаемое
	 * значение может выходить за границы допустимых значений индекса. Например,
	 * может быть меньше нуля. Полученный индекс должен обязательно проверяться
	 * в вызывающем коде.
	 * <p>
	 * @param index индекс последнего элемента базы расчета
	 * @param period количество элементов
	 * @return индекс первого элемента
	 */
	private int getStartIndex(int index, int period) {
		return index - period + 1;
	}

	@Override
	public Double tr(Series<Candle> value, int index) throws ValueException {
		if ( value.getLength() == 0 ) {
			return null;
		}
		index = makeIndexPositive(value, index);
		Candle curr = value.get(index);
		if ( curr == null ) {
			return null;
		}
		if ( index == 0 ) {
			return curr.getHeight();
		}
		Candle prev = value.get(index - 1);
		if ( prev == null ) {
			return curr.getHeight();
		}
		return max(curr.getHeight(),
				abs(curr.getHigh() - prev.getClose()),
				abs(curr.getLow() - prev.getClose()));
	}

	@Override
	public Double tr(Series<Candle> value) throws ValueException {
		return tr(value, getLastIndex(value));
	}

	@Override
	public Double dpo(DataSeries close, int period) throws ValueException {
		return dpo(close, getLastIndex(close), period);
	}

	@Override
	public Double max(DataSeries value, int index, int period)
			throws ValueException
	{
		if ( value.getLength() == 0 ) {
			return null;
		}
		index = makeIndexPositive(value, index);
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			start = 0;
		}
		Double max = null;
		for ( ; start <= index; start ++ ) {
			Double cur = value.get(start);
			if ( cur != null ) {
				if ( max == null ) {
					max = cur;
				} else if ( cur.compareTo(max) > 0 ) {
					max = cur;
				}
			}
		}
		return max;
	}

	@Override
	public Double max(DataSeries value, int period) throws ValueException {
		return max(value, getLastIndex(value), period);
	}

	@Override
	public Double max(int period, DataSeries... values)
			throws ValueException
	{
		if ( values.length == 0 ) {
			return null;
		}
		return max(getLastIndex(values[0]), period, values);
	}

	@Override
	public Double max(int index, int period, DataSeries... values)
			throws ValueException
	{
		Double max[] = new Double[values.length];
		for ( int i = 0; i < values.length; i ++ ) {
			max[i] = max(values[i], index, period);
		}
		return max(max);
	}

	@Override
	public Double min(DataSeries value, int index, int period)
			throws ValueException
	{
		if ( value.getLength() == 0 ) {
			return null;
		}
		index = makeIndexPositive(value, index);
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			start = 0;
		}
		Double min = null;
		for ( ; start <= index; start ++ ) {
			Double cur = value.get(start);
			if ( cur != null ) {
				if ( min == null ) {
					min = cur;
				} else if ( cur.compareTo(min) < 0 ) {
					min = cur;
				}
			}
		}
		return min;
	}

	@Override
	public Double min(DataSeries value, int period) throws ValueException {
		return min(value, getLastIndex(value), period);
	}

	@Override
	public Double min(int period, DataSeries... values)
			throws ValueException
	{
		if ( values.length == 0 ) {
			return null;
		}
		return min(getLastIndex(values[0]), period, values);
	}

	@Override
	public Double min(int index, int period, DataSeries... values)
			throws ValueException
	{
		Double min[] = new Double[values.length];
		for ( int i = 0; i < values.length; i ++ ) {
			min[i] = min(values[i], index, period);
		}
		return min(min);
	}

	@Override
	public boolean crossUnderZero(DataSeries value, int index)
			throws ValueException
	{
		if ( value.getLength() < 2 ) {
			return false;
		}
		index = makeIndexPositive(value, index);
		if ( index - 1 < 0 ) {
			return false;
		}
		Double prev = value.get(index - 1);
		Double curr = value.get(index);
		return prev != null && curr != null
			&& prev.compareTo(0d) > 0 && curr.compareTo(0d) < 0;
	}

	@Override
	public boolean crossUnderZero(DataSeries value) throws ValueException {
		return crossUnderZero(value, getLastIndex(value));
	}

	@Override
	public boolean crossOverZero(DataSeries value, int index)
			throws ValueException
	{
		if ( value.getLength() < 2 ) {
			return false;
		}
		index = makeIndexPositive(value, index);
		if ( index - 1 < 0 ) {
			return false;
		}
		Double prev = value.get(index - 1);
		Double curr = value.get(index);
		return prev != null && curr != null
			&& prev.compareTo(0d) < 0 && curr.compareTo(0d) > 0;
	}

	@Override
	public boolean crossOverZero(DataSeries value) throws ValueException {
		return crossOverZero(value, getLastIndex(value));
	}

}
