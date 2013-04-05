package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.indicator.SmmaQuik;

/**
 * Сглаженная скользящая средняя по рецепту из QUIK-а.
 * До тех пор, пока история источника содержит значений в количестве менее или
 * равном количеству установленных периодов, текущее значение индикатора
 * рассчитывается как SMA.
 * 
 * Формул рассчета SMMA было найдено до едрени фени:
 * 
 * адын - http://www.metatrader5.com/ru/terminal/help/analytics/indicators/trend_indicators/ma
 * дыва - http://20minutetraders.com/learn/moving-averages/smoothed-moving-average-calculation
 * тыры - http://enc.fxeuroclub.ru/409/
 * чтыр - метод из документации QUIK-а
 * 
 * Эта реализация по методу чтыр.
 */
@Deprecated
public class QuikSmma extends ValueImpl<Double> {
	private final SmmaQuik ma;
	
	public QuikSmma(Value<Double> iValue, int periods) {
		this(iValue, periods, ValueImpl.DEFAULT_ID);
	}
	
	public QuikSmma(Value<Double> iValue, int periods, String id) {
		super(id);
		ma = new SmmaQuik(iValue, periods);
	}
	
	public int getPeriods() {
		return ma.getPeriod();
	}
	
	public Value<Double> getSourceValue() {
		return ma.getSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(ma.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
