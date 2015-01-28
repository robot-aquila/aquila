package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.*;

/**
 * Интерфейс набора математических функций.
 * <p>
 * 2013-03-04<br>
 * $Id: FMath.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public interface FMath {
	
	/**
	 * Абсолютное значение.
	 * <p>
	 * @param val
	 * @return абсолютное значение
	 */
	public Double abs(Double val);

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
	 * @throws ValueException
	 */
	public Double sma(DataSeries value, int index, int period)
			throws ValueException;
	
	/**
	 * Simple Moving Average (SMA).
	 * <p>
	 * Работает через {@link #sma(DataSeries, int, int)}. Расчитывает значение
	 * простой скользящей средней для последнего элемента. 
	 * <p>
	 * @param value ряд данных
	 * @param period период скользящей средней
	 * @return значение скользящей средней или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double sma(DataSeries value, int period) throws ValueException;
	
	/**
	 * Получить максимальное значение.
	 * <p>
	 * @param values значения
	 * @return максимальное значение или null, если не удалось расчитать
	 */
	public Double max(Double... values);
	
	/**
	 * Получить минимальное значение.
	 * <p>
	 * @param values значения
	 * @return минимальное значение или null, если не удалось расчитать
	 */
	public Double min(Double... values);
	
	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс последнего элемента базы расчета
	 * @param period период расчета максимума
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double max(DataSeries value, int index, int period)
			throws ValueException;
	
	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * Работает через {@link #max(DataSeries, int, int)}. Расчитывает значение
	 * максимума за период относительно последнего элемента в источнике.
	 * <p>
	 * @param value ряд данных
	 * @param period период расчета максимума
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double max(DataSeries value, int period) throws ValueException;
	
	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * @param period период расчета максимума
	 * @param values ряды данных, по которым расчитывается максимум
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double max(int period, DataSeries... values) throws ValueException;
	
	/**
	 * Получить максимальное значение за период.
	 * <p>
	 * @param index индекс последнего элемента базы расчета
	 * @param period период расчета максимума
	 * @param values ряды данных, по которым расчитывается максимум
	 * @return значение максимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double max(int index, int period, DataSeries... values)
			throws ValueException;
	
	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс последнего элемента базы расчета
	 * @param period период расчета минимума
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double min(DataSeries value, int index, int period)
			throws ValueException;
	
	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param value ряд данных
	 * @param period период расчета минимума
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double min(DataSeries value, int period) throws ValueException;
	
	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param period период расчета минимума
	 * @param values ряды данных, по которым расчитывается минимум
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double min(int period, DataSeries... values) throws ValueException;
	
	/**
	 * Получить минимальное значение за период.
	 * <p>
	 * @param index индекс последнего элемента базы расчета
	 * @param period период расчета минимума
	 * @param values ряды данных, по которым расчитывается минимум
	 * @return значение минимума или null, если не удалось расчитать
	 * @throws ValueException
	 */
	public Double min(int index, int period, DataSeries... values)
			throws ValueException;
	
	/**
	 * Проверить наличие null-значений.
	 * <p>
	 * @param value источник значений
	 * @param index индекс последнего элемента базы расчета
	 * @param period количество элементов базы расчета
	 * @return true - есть null-значения в базе ресчета, false - нет
	 * @throws ValueException
	 */
	public boolean hasNulls(Series<?> value, int index, int period)
			throws ValueException;
	
	/**
	 * Проверить наличие null-значений.
	 * <p>
	 * @param value источник значений
	 * @param period количество элементов базы расчета
	 * @return true - есть null-значения в базе ресчета, false - нет
	 * @throws ValueException
	 */
	public boolean hasNulls(Series<?> value, int period) throws ValueException;
	
	/**
	 * True Range (TR).
	 * <p>
	 * @param candles источник свечей
	 * @param index индекс элемента для расчета TR
	 * @return TR или null, если расчитать не удалось
	 * @throws ValueException
	 */
	public Double tr(Series<Candle> candles, int index) throws ValueException;

	/**
	 * True Range (TR).
	 * <p>
	 * @param candles источник свечей
	 * @return TR или null, если расчитать не удалось
	 * @throws ValueException
	 */
	public Double tr(Series<Candle> candles) throws ValueException;
	
	/**
	 * VectorVest Detrended Price Oscillator (DPO).
	 * <p>
	 * Формула DPO из платформы VectorVest.
	 * <p>
	 * @param close ряд данных
	 * @param index индекс элемента для расчета DPO
	 * @param period количество элементов базы расчета
	 * @return значение DPO или null, если расчитать не удалось
	 */
	public Double vv_dpo(DataSeries close, int index, int period)
			throws ValueException;
	
	/**
	 * VectorVest Detrended Price Oscillator (DPO).
	 * <p>
 	 * Формула DPO из платформы VectorVest.
	 * <p>
	 * @param close ряд данных
	 * @param period количество элементов базы расчета
	 * @return значение DPO или null, если расчитать не удалось
	 */
	public Double vv_dpo(DataSeries close, int period)  throws ValueException;
	
	/**
	 * Пересечение нуля сверху-вниз.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс элемента для расчета пересечения
	 * @return true - есть пересечение нуля сверху-вниз, false - нет пересечения
	 * @throws ValueException
	 */
	public boolean crossUnderZero(DataSeries value, int index)
			throws ValueException;
	
	/**
	 * Пересечение нуля сверху-вниз.
	 * <p>
	 * @param value ряд данных
	 * @return true - есть пересечение нуля сверху-вниз, false - нет пересечения
	 * @throws ValueException
	 */
	public boolean crossUnderZero(DataSeries value) throws ValueException;

	/**
	 * Перечение нуля снизу-вверх.
	 * <p>
	 * @param value ряд данных
	 * @param index индекс элемента для расчета пересечения
	 * @return true - есть пересечение нуля снизу-вверх, false - нет пересечения
	 * @throws ValueException
	 */
	public boolean crossOverZero(DataSeries value, int index)
			throws ValueException;
	
	/**
	 * Перечение нуля снизу-вверх.
	 * <p>
	 * @param value ряд данных
	 * @return true - есть пересечение нуля снизу-вверх, false - нет пересечения
	 * @throws ValueException
	 */
	public boolean crossOverZero(DataSeries value) throws ValueException;
	
}
