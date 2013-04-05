package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Экспоненциальная скользящая средняя.
 * 
 * Рассчитывается по формуле:
 * 
 *   K = 2 / (N + 1)
 *   ЕМА = Р_тек * К + ЕМА_пред * (1 - К)
 *   
 * где 
 * ЕМА - экспоненциальное скользящее среднее;
 * N - период усреднения;
 * Р_тек - текущее значение источника (например цена);
 * ЕМА_пред - предыдущее значение ЕМА.
 * 
 * Если история отсутствует, то устанавливается EMA = P_тек  
 */
public class Ema extends BaseIndicator1SP<Double> {
	private final double factorCurr;
	private final double factorPrev;
	private Double previous = null;
	
	/**
	 * Конструктор.
	 * 
	 * @param source источник значений
	 * @param period период
	 */
	public Ema(Value<Double> src, int period) {
		super(src, period);
		factorCurr = 2.0d / (period + 1);
		factorPrev = 1.0d - factorCurr;
	}

	@Override
	public synchronized Double calculate() throws ValueException {
		Double current,value;
		current = value = src.get();
		if ( previous == null ) {
			value = current;
		} else if ( current != null ) {
			value = current * factorCurr + previous * factorPrev;
		}
		previous = value;
		return value;
	}

}
