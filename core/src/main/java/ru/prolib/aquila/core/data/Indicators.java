package ru.prolib.aquila.core.data;

/**
 * Common indicators.
 * <p>
 * 2013-03-04<br>
 * $Id: FMathImpl.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class Indicators {
	
	public Indicators() {
		super();
	}
	
	/**
	 * Получить максимальное значение.
	 * <p>
	 * @param values значения
	 * @return максимальное значение или null, если не удалось расчитать
	 */
	public Double max(Double... values) {
		Double max = null;
		for ( Double c : values ) {
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
	public Double min(Double... values) {
		Double min = null;
		for ( Double c : values ) {
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
	public Double abs(Double val) {
		return val == null ? null : Math.abs(val);
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
	public Double sma(Series<Double> value, int period) throws ValueException {
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
	public Double sma(Series<Double> value, int index, int period)
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
	public Double vvdpo(Series<Double> close, int index, int period)
			throws ValueException
	{
		if ( close.getLength() == 0 ) {
			return null;
		}
		
		int usePeriod = (int) Math.round(period / 2d + 1d);
		Double price = close.get(index);
		int useIndex = index - usePeriod;
		if ( useIndex < 0 ) {
			return null;
		}
		
		Double sma = sma(close, useIndex, period);
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

	/**
	 * True Range (TR).
	 * <p>
	 * @param value источник свечей
	 * @param index индекс элемента для расчета TR
	 * @return TR или null, если расчитать не удалось
	 * @throws ValueException - ошибка доступа к данным
	 */
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

	/**
	 * True Range (TR).
	 * <p>
	 * @param value источник свечей
	 * @return TR или null, если расчитать не удалось
	 * @throws ValueException - ошибка доступа к данным
	 */
	public Double tr(Series<Candle> value) throws ValueException {
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
	public Double vvdpo(Series<Double> close, int period) throws ValueException {
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
	public Double max(Series<Double> value, int index, int period)
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
	public Double max(Series<Double> value, int period) throws ValueException {
		return max(value, getLastIndex(value), period);
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
	public final Double max(int period, Series<Double>... values)
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
	public final Double max(int index, int period, Series<Double>... values)
			throws ValueException
	{
		Double max[] = new Double[values.length];
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
	public Double min(Series<Double> value, int index, int period)
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

	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param value ряд данных
	 * @param period период расчета минимума
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException - ошибка доступа к данным
	 */
	public Double min(Series<Double> value, int period) throws ValueException {
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
	public final Double min(int period, Series<Double>... values)
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
	public final Double min(int index, int period, Series<Double>... values)
			throws ValueException
	{
		Double min[] = new Double[values.length];
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
	public boolean crossUnderZero(Series<Double> value, int index)
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

	/**
	 * Пересечение нуля сверху-вниз.
	 * <p>
	 * @param value ряд данных
	 * @return true - есть пересечение нуля сверху-вниз, false - нет пересечения
	 * @throws ValueException - ошибка доступа к данным
	 */
	public boolean crossUnderZero(Series<Double> value) throws ValueException {
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
	public boolean crossOverZero(Series<Double> value, int index)
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

	/**
	 * Перечение нуля снизу-вверх.
	 * <p>
	 * @param value ряд данных
	 * @return true - есть пересечение нуля снизу-вверх, false - нет пересечения
	 * @throws ValueException - ошибка доступа к данным
	 */
	public boolean crossOverZero(Series<Double> value) throws ValueException {
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
	 * @throws IllegalArgumentException - period too low
	 */
	public Double qema(Series<Double> value, int index, int period)
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
		int pos = index - start,
			period_minus1 = period - 1, period_plus1 = period + 1;
		Double prev = null, curr, result = null;
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
		
		for ( ++pos; pos <= index; pos ++ ) {
			curr = value.get(pos);
			if ( curr != null ) {
				// TODO: check for null
				result = (prev * period_minus1 + 2 * curr) / period_plus1;
				prev = result;
			}
		}
		return result;
	}
	
	private Double qemaFirst(Series<Double> value, int index, int period)
		throws ValueException
	{
		int start = getStartIndex(index, period);
		if ( start < 0 ) {
			return null;
		}
		Double prev = value.get(start), curr = null;
		if ( prev == null ) {
			return null;
		}
		int period_minus1 = period - 1, period_plus1 = period + 1;
		for ( int i = start + 1; i <= index; i ++ ) {
			curr = value.get(i);
			if ( curr == null ) {
				return null;
			}
			prev = (prev * period_minus1 + 2 * curr) / period_plus1;
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
	 * @throws IllegalArgumentException - period too low
	 */
	public Double qatr(Series<Candle> candles, int index, int period)
		throws ValueException
	{
		if ( period < 2 ) {
			throw new IllegalArgumentException("Period too low");
		}
		index = makeIndexPositive(candles, index);
		int start = getStartIndex(index, period), period_minus1 = period - 1;
		if ( start < 0 ) {
			return null;
		}
		Double prev = 0d;
		for ( int i = 0; i < period; i ++ ) {
			prev += tr(candles, i);
		}
		prev /= period;
		if ( index == period_minus1 ) {
			return prev;
		}
		for ( int i = period; i <= index; i ++ ) {
			prev = (prev * period_minus1 + tr(candles, i)) / period;
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
	 * @throws IllegalArgumentException - period too low
	 */
	public Double qatr(Series<Candle> candles, int period)
		throws ValueException
	{
		return qatr(candles, candles.getLength() - 1, period);
	}

}
