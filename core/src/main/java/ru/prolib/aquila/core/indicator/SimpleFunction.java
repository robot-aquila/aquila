package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.*;

/**
 * Интерфейс функции индикатора.
 * <p>
 * Предназначен для индикаторов, значения которых расчитываются только на
 * основании исходного ряда (на-лету). К подобным индикаторам относятся например
 * True Range, Highest, Lowest, etc...  
 * <p>
 * @param <T> тип значений исходного ряда
 * @param <R> тип результата
 */
public interface SimpleFunction<T, R> {
	
	/**
	 * Расчитать значение индикатора.
	 * <p>
	 * @param sourceSeries исходный ряд данных
	 * @param index индекс элемента исходного ряда, для которого выполняется
	 * расчет
	 * @return значение индикатора
	 * @throws ValueException исключение расчета
	 */
	public R calculate(Series<T> sourceSeries, int index) throws ValueException;

	/**
	 * Получить идентификатор функции по-умолчанию.
	 * <p>
	 * @return идентификатор по-умолчанию
	 */
	public String getDefaultId();

}
