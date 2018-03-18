package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;

/**
 * Common TA functions.
 * <p>
 * 2013-03-04<br>
 * $Id: FMathImpl.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TAMath {
	
	/**
	 * This class is used to pass result of some math functions.
	 */
	static class SN {
		private final CDecimal sum;
		private final int num;
		
		SN(CDecimal sum, int num) {
			this.sum = sum;
			this.num = num;
		}
		
		public CDecimal getSum() {
			return sum;
		}
		
		public int getNum() {
			return num;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != SN.class ) {
				return false;
			}
			SN o = (SN) other;
			return new EqualsBuilder()
				.append(sum, o.sum)
				.append(num, o.num)
				.isEquals();
		}
		
	}
	
	/**
	 * This helper is used to permit null values for series to calculate
	 * correlation coefficient. To make it work null values should be at same
	 * position in both series. This helper checks if there is a null value at
	 * same position in the second series. If so then null value will be
	 * returned independently of value from primary series.
	 */
	static class CorrelationHelperSeries implements Series<CDecimal> {
		private final Series<CDecimal> x, y;
		
		CorrelationHelperSeries(Series<CDecimal> x, Series<CDecimal> y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String getId() {
			return x.getId();
		}

		@Override
		public CDecimal get() throws ValueException {
			return get(x.getLength() - 1);
		}

		@Override
		public CDecimal get(int index) throws ValueException {
			CDecimal cx = x.get(index), cy = y.get(index);
			return cx != null && cy != null ? cx : null;
		}

		@Override
		public int getLength() {
			return x.getLength();
		}

		@Override
		public LID getLID() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void lock() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void unlock() {
			throw new UnsupportedOperationException();			
		}
		
	}
	
	private static final TAMath instance = new TAMath();
	
	public static TAMath getInstance() {
		return instance;
	}
	
	/**
	 * Получить максимальное значение.
	 * <p>
	 * @param values значения
	 * @return максимальное значение или null, если не удалось расчитать
	 */
	public CDecimal max(CDecimal... values) {
		CDecimal max = null;
		for ( CDecimal c : values ) {
			if ( c != null && (max == null || c.compareTo(max) > 0) ) {
				max = c;
			}
		}
		return max;
	}
	
	/**
	 * Получить минимальное значение.
	 * <p>
	 * @param values значения
	 * @return минимальное значение или null, если не удалось расчитать
	 */
	public CDecimal min(CDecimal... values) {
		CDecimal min = null;
		for ( CDecimal c : values ) {
			if ( c != null && (min == null || c.compareTo(min) < 0) ) {
				min = c;
			}
		}
		return min;
	}

	/**
	 * Абсолютное значение.
	 * <p>
	 * @param val - value
	 * @return абсолютное значение
	 */
	public CDecimal abs(CDecimal val) {
		return val == null ? null : val.abs();
	}

	/**
	 * Проверить наличие null-значений.
	 * <p>
	 * @param value источник значений
	 * @param index индекс последнего элемента базы расчета
	 * @param period количество элементов базы расчета
	 * @return true - есть null-значения в базе ресчета, false - нет
	 * @throws ValueException - ошибка доступа к данным
	 */
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

	/**
	 * Проверить наличие null-значений.
	 * <p>
	 * @param value источник значений
	 * @param period количество элементов базы расчета
	 * @return true - есть null-значения в базе ресчета, false - нет
	 * @throws ValueException - ошибка доступа к данным
	 */
	public boolean hasNulls(Series<?> value, int period) throws ValueException {
		return hasNulls(value, getLastIndex(value), period);
	}

	/**
	 * Simple Moving Average (SMA).
	 * <p>
	 * Рассчитывает значение простой скользящей средней для последнего элемента. 
	 * <p>
	 * @param value ряд данных
	 * @param period период скользящей средней
	 * @return значение скользящей средней или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal sma(Series<CDecimal> value, int period) throws ValueException {
		return sma(value, getLastIndex(value), period);
	}

	/**
	 * Simple Moving Average (SMA).
	 * <p>
	 * Рассчитывается как среднее арифметическое ряда данных за указанный
	 * период. Если период больше чем количество значений в базе расчета, то
	 * возвращается null.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс последнего элемента базы расчета
	 * @param period период скользящей средней
	 * @return значение скользящей средней или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal sma(Series<CDecimal> value, int index, int period)
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
		CDecimal sum = CDecimalBD.ZERO;
		for ( ; start <= index; start ++ ) {
			sum = sum.add(value.get(start));
		}
		return sum.withScale(7 + sum.getScale()).divide((long) period);
	}
	
	/**
	 * VectorVest Detrended Price Oscillator (DPO).
	 * <p>
 	 * Формула DPO из платформы VectorVest.
	 * <p>
	 * @param close ряд данных
	 * @param index индекс элемента для рассчета значения
	 * @param period количество элементов базы расчета
	 * @throws ValueException - ошибка доступа к данным
	 * @return значение DPO или null, если расчитать не удалось
	 */
	public CDecimal vvdpo(Series<CDecimal> close, int index, int period)
			throws ValueException
	{
		if ( close.getLength() == 0 ) {
			return null;
		}
		
		int usePeriod = (int) Math.round(period / 2d + 1d);
		CDecimal price = close.get(index);
		int useIndex = index - usePeriod;
		if ( useIndex < 0 ) {
			return null;
		}
		
		CDecimal sma = sma(close, useIndex, period);
		if ( price != null && sma != null ) {
			return price.subtract(sma);
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

	/**
	 * True Range (TR).
	 * <p>
	 * @param value источник свечей
	 * @param index индекс элемента для расчета TR
	 * @return TR или null, если расчитать не удалось
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal tr(Series<Candle> value, int index) throws ValueException {
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
				abs(curr.getHigh().subtract(prev.getClose())),
				abs(curr.getLow().subtract(prev.getClose())));
	}

	/**
	 * True Range (TR).
	 * <p>
	 * @param value источник свечей
	 * @return TR или null, если расчитать не удалось
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal tr(Series<Candle> value) throws ValueException {
		return tr(value, getLastIndex(value));
	}

	/**
	 * VectorVest Detrended Price Oscillator (DPO).
	 * <p>
	 * Формула DPO из платформы VectorVest.
	 * <p>
	 * @param close ряд данных
	 * @param period количество элементов базы расчета
	 * @throws ValueException - ошибка доступа к данным
	 * @return значение DPO или null, если расчитать не удалось
	 */
	public CDecimal vvdpo(Series<CDecimal> close, int period) throws ValueException {
		return vvdpo(close, getLastIndex(close), period);
	}

	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс последнего элемента базы расчета
	 * @param period период расчета максимума
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal max(Series<CDecimal> value, int index, int period)
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
		CDecimal max = null;
		for ( ; start <= index; start ++ ) {
			CDecimal cur = value.get(start);
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
	
	public Long maxL(Series<Long> value, int index, int period)
		throws ValueException
	{
		Long max = null;
		index = makeIndexPositive(value, index);
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			start = 0;
		}
		for ( ; start <= index; start ++ ) {
			Long cur = value.get(start);
			if ( cur != null ) {
				if ( max == null || cur.compareTo(max) > 0 ) {
					max = cur;
				}
			}
		}
		return max;
	}
	
	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * Раcсчитывает значение максимума за период с конца ряда данных.
	 * <p>
	 * @param value ряд данных
	 * @param period период расчета максимума
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal max(Series<CDecimal> value, int period) throws ValueException {
		return max(value, getLastIndex(value), period);
	}

	public Long maxL(Series<Long> value, int period) throws ValueException {
		return maxL(value, getLastIndex(value), period);
	}

	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * @param period период расчета максимума
	 * @param values ряды данных, по которым расчитывается максимум
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	@SafeVarargs
	public final CDecimal max(int period, Series<CDecimal>... values)
			throws ValueException
	{
		if ( values.length == 0 ) {
			return null;
		}
		return max(getLastIndex(values[0]), period, values);
	}

	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * Рассчитывает значение максимума за период относительно указанной позиции
	 * в ряду.
	 * <p>
	 * @param index позиция последнего элемента в ряду
	 * @param period период расчета максимума
	 * @param values ряды данных, по которым расчитывается максимум
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	@SafeVarargs
	public final CDecimal max(int index, int period, Series<CDecimal>... values)
			throws ValueException
	{
		CDecimal max[] = new CDecimal[values.length];
		for ( int i = 0; i < values.length; i ++ ) {
			max[i] = max(values[i], index, period);
		}
		return max(max);
	}

	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс последнего элемента базы расчета
	 * @param period период расчета минимума
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal min(Series<CDecimal> value, int index, int period)
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
		CDecimal min = null;
		for ( ; start <= index; start ++ ) {
			CDecimal cur = value.get(start);
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

	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param value ряд данных
	 * @param period период расчета минимума
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	public CDecimal min(Series<CDecimal> value, int period) throws ValueException {
		return min(value, getLastIndex(value), period);
	}

	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param period период расчета минимума
	 * @param values ряды данных, по которым расчитывается минимум
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	@SafeVarargs
	public final CDecimal min(int period, Series<CDecimal>... values)
			throws ValueException
	{
		if ( values.length == 0 ) {
			return null;
		}
		return min(getLastIndex(values[0]), period, values);
	}

	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param index индекс последнего элемента базы расчета
	 * @param period период расчета минимума
	 * @param values ряды данных, по которым расчитывается минимум
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	@SafeVarargs
	public final CDecimal min(int index, int period, Series<CDecimal>... values)
			throws ValueException
	{
		CDecimal min[] = new CDecimal[values.length];
		for ( int i = 0; i < values.length; i ++ ) {
			min[i] = min(values[i], index, period);
		}
		return min(min);
	}

	/**
	 * Пересечение нуля сверху-вниз.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс элемента для расчета пересечения
	 * @return true - есть пересечение нуля сверху-вниз, false - нет пересечения
	 * @throws ValueException - ошибка доступа к данным
	 */
	public boolean crossUnderZero(Series<CDecimal> value, int index)
			throws ValueException
	{
		if ( value.getLength() < 2 ) {
			return false;
		}
		index = makeIndexPositive(value, index);
		if ( index - 1 < 0 ) {
			return false;
		}
		CDecimal prev = value.get(index - 1);
		CDecimal curr = value.get(index);
		return prev != null && curr != null
			&& prev.compareTo(CDecimalBD.ZERO) > 0 && curr.compareTo(CDecimalBD.ZERO) < 0;
	}

	/**
	 * Пересечение нуля сверху-вниз.
	 * <p>
	 * @param value ряд данных
	 * @return true - есть пересечение нуля сверху-вниз, false - нет пересечения
	 * @throws ValueException - ошибка доступа к данным
	 */
	public boolean crossUnderZero(Series<CDecimal> value) throws ValueException {
		return crossUnderZero(value, getLastIndex(value));
	}

	/**
	 * Перечение нуля снизу-вверх.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс элемента для расчета пересечения
	 * @return true - есть пересечение нуля снизу-вверх, false - нет пересечения
	 * @throws ValueException - ошибка доступа к данным
	 */
	public boolean crossOverZero(Series<CDecimal> value, int index)
			throws ValueException
	{
		if ( value.getLength() < 2 ) {
			return false;
		}
		index = makeIndexPositive(value, index);
		if ( index - 1 < 0 ) {
			return false;
		}
		CDecimal prev = value.get(index - 1);
		CDecimal curr = value.get(index);
		return prev != null && curr != null
			&& prev.compareTo(CDecimalBD.ZERO) < 0 && curr.compareTo(CDecimalBD.ZERO) > 0;
	}

	/**
	 * Перечение нуля снизу-вверх.
	 * <p>
	 * @param value ряд данных
	 * @return true - есть пересечение нуля снизу-вверх, false - нет пересечения
	 * @throws ValueException - ошибка доступа к данным
	 */
	public boolean crossOverZero(Series<CDecimal> value) throws ValueException {
		return crossOverZero(value, getLastIndex(value));
	}
	
	/**
	 * Exponential Moving Average (EMA) по формуле QUIK.
	 * <p>
	 * Расчет выполняется по формуле:
	 * <pre>
	 * 		EMAi = (EMAi - 1 * (n - 1) + 2 * Pi) / (n + 1)
	 * </pre>
	 * где Pi - значение цены в текущем периоде, EMAi - значение EMA текущего
	 * периода, EMAi-1 - значение EMA предыдущего периода. В качестве первого
	 * значения берется значение источника как есть. 
	 * <p>
	 * @param value ряд данных
	 * @param index индекс элемента ряда для которого рассчитывается значение
	 * @param period период скользящей средней
	 * @return значение скользящей средней или null, если не удалось расчитать
	 * @throws ValueException - error accessing data
	 * @throws IllegalArgumentException - period too short
	 */
	public CDecimal qema(Series<CDecimal> value, int index, int period)
			throws ValueException, IllegalArgumentException
	{
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period too low");
		}
		index = makeIndexPositive(value, index);
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			return null;
		} else if ( start == 0 ) {
			return qemaFirst(value, index, period);
		}
		
		// Если start > 0, значит у нас будет более одной итерации.
		// Определяем позицию первого элемента, для которого возможно
		// рассчитать начальное значение EMA:
		// NOTE: Варианты типа Min(start, period * X) дают результат,
		// который не соответствует результату рассчетов в QUIK.
		int pos = index - start;
		CDecimal prev = null;
		// Ожидаем последовательность длинной period, которая не содержит null
		for ( ; pos < index; pos ++ ) {
			prev = qemaFirst(value, pos, period);
			if ( prev != null ) {
				break;
			}
		}
		if ( prev == null ) {
			return null; // Couldn't obtain start EMA value
		}
		CDecimal curr, result = null,
			d_period_minus1 = CDecimalBD.of((long)(period - 1)),
			d_period_plus1 = CDecimalBD.of((long)(period + 1));
		for ( ++pos; pos <= index; pos ++ ) {
			curr = value.get(pos);
			if ( curr != null ) {
				// TODO: check for null
				result = prev.multiply(d_period_minus1)
						.add(curr.multiply(2L))
						.divide(d_period_plus1);
				prev = result;
			}
		}
		return result;
	}
	
	private CDecimal qemaFirst(Series<CDecimal> value, int index, int period)
		throws ValueException
	{
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			return null;
		}
		CDecimal prev = value.get(start), curr = null;
		if ( prev == null ) {
			return null;
		}
		int scale = 7 + prev.getScale();
		CDecimal d_period_minus1 = CDecimalBD.of((long)(period - 1)).withScale(scale),
				d_period_plus1 = CDecimalBD.of((long)(period+ 1)).withScale(scale);
		for ( int i = start + 1; i <= index; i ++ ) {
			curr = value.get(i);
			if ( curr == null ) {
				return null;
			}
			prev = prev.multiply(d_period_minus1)
					.add(curr.multiply(2L))
					.divide(d_period_plus1);
		}
		return prev;
	}
	
	/**
	 * Average True Range (QUIK).
	 * <p>
	 * Calculates ATR using the candle series.
	 * <p>
	 * @param candles - candles
	 * @param index - index of candle to calculate ATR
	 * @param period - ATR period
	 * @return ATR value
	 * @throws ValueException - error accessing data
	 * @throws IllegalArgumentException - period too short
	 */
	public CDecimal qatr(Series<Candle> candles, int index, int period)
		throws ValueException
	{
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period too low");
		}
		index = makeIndexPositive(candles, index);
		int start = getStartIndex(index, period), period_minus1 = period - 1;
		CDecimal d_period_minus1 = CDecimalBD.of((long)period_minus1);
		CDecimal d_period = CDecimalBD.of((long)period);
		if ( start < 0 ) {
			return null;
		}
		CDecimal prev = CDecimalBD.ZERO;
		for ( int i = 0; i < period; i ++ ) {
			prev = prev.add(tr(candles, i));
		}
		prev = prev.withScale(7 + prev.getScale()).divide(d_period);
		if ( index == period_minus1 ) {
			return prev;
		}
		for ( int i = period; i <= index; i ++ ) {
			prev = prev.multiply(d_period_minus1)
					.add(tr(candles, i))
					.divide(d_period);
		}
		return prev;
	}

	/**
	 * Average True Range (QUIK).
	 * <p>
	 * Calculates ATR using the candle series.
	 * <p>
	 * @param candles - candles
	 * @param period - ATR period
	 * @return ATR value
	 * @throws ValueException - error accessing data
	 * @throws IllegalArgumentException - period too short
	 */
	public CDecimal qatr(Series<Candle> candles, int period)
		throws ValueException
	{
		return qatr(candles, candles.getLength() - 1, period);
	}
	
	/**
	 * Get difference between two next values.
	 * <p>
	 * @param series - source data series
	 * @param index - the last value index
	 * @return difference between X(index) and X(index-1) or null if one of
	 * values is null. The result for first element of series is always zero.
	 * @throws ValueOutOfRangeException - index out of range
	 * @throws ValueException - error accessing data
	 */
	public CDecimal delta(Series<CDecimal> series, int index) throws ValueException {
		index = makeIndexPositive(series, index);
		if ( index < 0 ) {
			throw new ValueOutOfRangeException();
		}
		if ( index == 0 ) {
			return CDecimalBD.ZERO;
		}
		CDecimal curr = series.get(index), prev = series.get(index - 1);
		if ( curr == null ) {
			return null;
		}
		if ( prev == null ) {
			return CDecimalBD.ZERO;
		}
		return curr.subtract(prev);
	}
	
	private SN _amean(Series<CDecimal> x) throws ValueException {
		int length = x.getLength(), numx = 0;
		CDecimal sumx = CDecimalBD.ZERO;
		for ( int i = 0; i < length; i ++ ) {
			CDecimal cx = x.get(i);
			if ( cx != null ) {
				sumx = sumx.add(cx);
				numx ++;
			}
		}
		if ( numx == 0 ) {
			throw new ValueException("Not enough elements");
		}
		return new SN(sumx, numx);
	}
	
	/**
	 * Get arithmetic mean value of the series.
	 * <p>
	 * Null values are permitted and will be ignored. Null values reduces amount
	 * of elements which is used as divisor. For example if the series contains
	 * five elements in total and two null elements then divisor will be 3, not
	 * 5. The series must have at leas one non-null element. Otherwise an
	 * exception will be thrown.
	 * <p>
	 * @param x - data series
	 * @param scale - desired result scale
	 * @return arithmetic mean value
	 * @throws ValueException - error accessing data or series does not contain
	 * non-null elements
	 */
	public CDecimal amean(Series<CDecimal> x, int scale) throws ValueException {
		SN r = _amean(x);
		return r.getSum().divideExact((long)r.getNum(), scale);
	}
	
	private SN _covariance(Series<CDecimal> x, Series<CDecimal> y, int scale) throws ValueException {
		CDecimal ameanx = amean(x, scale), ameany = amean(y, scale), sum = CDecimalBD.ZERO;
		int length = x.getLength(), num = 0;
		if ( y.getLength() != length ) {
			throw new ValueException("The series must have the same length");
		}
		for ( int i = 0; i < length; i ++ ) {
			CDecimal cx = x.get(i), cy = y.get(i);
			if ( cx != null || cy != null ) {
				if ( cx != null && cy != null ) {
					num ++;
				}
				CDecimal cx_ = cx == null ? CDecimalBD.ZERO : cx.subtract(ameanx);
				CDecimal cy_ = cy == null ? CDecimalBD.ZERO : cy.subtract(ameany);
				sum = sum.add(cx_.multiply(cy_));
			}
		}
		if ( num == 0 ) {
			throw new ValueException("Not enough elements");
		}
		return new SN(sum, num);
	}
	
	/**
	 * Get covariance.
	 * <p>
	 * The length of both series must be equals. Null values are permitted and
	 * will be ignored. Null values at same position in the both series will
	 * reduce amount of elements which is used as divisor. The series must have
	 * at least one non-null elements. Otherwise an exception will be thrown.
	 * <p>
	 * <b>NOTE:</b> Null values will reduce accuracy of the result. Try avoid
	 * null values in the series. 
	 * <p>
	 * @param x - the first data series 
	 * @param y - the second data series
	 * @param scale - desired result scale
	 * @return covariance value
	 * @throws ValueException - error accessing data, series does not contain
	 * non-null elements or series contain different amount of elements
	 */
	public CDecimal covariance(Series<CDecimal> x, Series<CDecimal> y, int scale) throws ValueException {
		SN r = _covariance(x, y, scale);
		return r.getSum().divideExact((long)r.getNum(), scale);
	}
	
	private SN _variance(Series<CDecimal> x, int scale) throws ValueException {
		CDecimal ameanx = amean(x, scale), sum = CDecimalBD.ZERO;
		int length = x.getLength(), num = 0;
		for ( int i = 0; i < length; i ++ ) {
			CDecimal cx = x.get(i);
			if ( cx != null ) {
				num ++;
				CDecimal x_ = cx.subtract(ameanx);
				sum = sum.add(x_.multiply(x_));
			}
		}
		if ( num == 0 ) {
			throw new ValueException("Not enough elements");
		}
		return new SN(sum, num);
	}
	
	/**
	 * Get variance based on series of data.
	 * <p>
	 * @param x - data series
	 * @param scale - desired result scale
	 * @return variance value
	 * @throws ValueException - an error occurred
	 */
	public CDecimal variance(Series<CDecimal> x, int scale) throws ValueException {
		SN r = _variance(x, scale);
		return r.getSum().divideExact((long)r.getNum(), scale);
	}
	
	/**
	 * Get correlation coefficient of two series.
	 * <p>
	 * Both series must have same amount of elements and must be not empty. If
	 * series is empty or lengths of series are mismatched then exception will
	 * be thrown. Null values are permitted. If null value in one of series then
	 * it will be considered as null values in both series.
	 * <p>
	 * @param x - the first data series
	 * @param y - the second data series
	 * @param scale - desired precision of a result
	 * @return correlation coefficient
	 * @throws ValueException - error accessing or calculating data
	 */
	public CDecimal correlation(Series<CDecimal> x, Series<CDecimal> y, int scale) throws ValueException {
		// This solution will work for series with null values just in one of series. 
		Series<CDecimal> x_ = new CorrelationHelperSeries(x, y),
				y_ = new CorrelationHelperSeries(y, x);
		SN covxy = _covariance(x_, y_, scale), varx = _variance(x_, scale), vary = _variance(y_, scale);
		//return covariance(x_,y_) / (Math.sqrt(variance(x_)) * Math.sqrt(variance(y_)));
		//return covariance(x_,y_) / Math.sqrt(variance(x_) * variance(y_));
		CDecimal sqrt_ = varx.getSum().multiply(vary.getSum()).sqrt(scale);
		return covxy.getSum().divideExact(sqrt_, scale);
	}
	
	private int _cross(Series<CDecimal> x, Series<CDecimal> y, int index) {
		if ( x.getLength() < 2 ) {
			return 0;
		}
		try {
			CDecimal x1 = x.get(index - 1), x2 = x.get(index),
				y1 = y.get(index - 1), y2 = y.get(index);
			if ( x1 == null || x2 == null || y1 == null || y2 == null ) {
				return 0;
			}
			int d1 = x1.compareTo(y1) > 0 ? 1 : -1, d2 = x2.compareTo(y2) > 0 ? 1 : -1;
			return d1 == d2 ? 0 : d2;
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception", e);
		}
	}
	
	/**
	 * Check that x crosses under y at the specified index.
	 * <p>
	 * @param x - first series
	 * @param y - second series
	 * @param index - index to detect crossing
	 * @return true if x crosses y, false otherwise
	 */
	public boolean crossUnder(Series<CDecimal> x, Series<CDecimal> y, int index) {
		return _cross(x, y, index) == -1;
	}
	
	/**
	 * Check that x crosses over y at the specified index.
	 * <p>
	 * @param x - first series
	 * @param y - second series
	 * @param index - index to detect crossing
	 * @return true if x crosses y, false otherwise
	 */
	public boolean crossOver(Series<CDecimal> x, Series<CDecimal> y, int index) {
		return _cross(x, y, index) == 1;
	}

}
