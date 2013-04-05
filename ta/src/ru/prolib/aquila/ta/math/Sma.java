package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;

/**
 * Простая скользящая средняя.
 * 
 * Рассчитывается как среднее арифметическое значений источника за указанное
 * количество периодов. Если количество периодов больше чем количество значений
 * в источнике, то за количество периодов берется длина истории.   
 */
@Deprecated
public class Sma extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Sma sma;
	
	public Sma(Value<Double> iValue, int periods) {
		this(iValue, periods, ValueImpl.DEFAULT_ID);
	}
	
	public Sma(Value<Double> iValue, int periods, String id) {
		super(id);
		sma = new ru.prolib.aquila.ta.indicator.Sma(iValue, periods);
	}
	
	public int getPeriods() {
		return sma.getPeriod();
	}
	
	public Value<Double> getSourceValue() {
		return sma.getSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(sma.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
