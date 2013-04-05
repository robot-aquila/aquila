package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;

/**
 * Медиана.
 * Рассчитывает среднее арифметическое от двух источников.
 * Обычно используется для рассчета средней цены бара по формуле (HIGH+LOW)/2
 */
@Deprecated
public class Median extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Median med;
	
	public Median(Value<Double> src1, Value<Double> src2) {
		this(src1, src2, ValueImpl.DEFAULT_ID);
	}
	
	public Median(Value<Double> src1, Value<Double> src2, String id) {
		super(id);
		med = new ru.prolib.aquila.ta.indicator.Median(src1, src2);
	}
	
	public Value<Double> getSourceValue1() {
		return med.getFirstSource();
	}
	
	public Value<Double> getSourceValue2() {
		return med.getSecondSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(med.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
