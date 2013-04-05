package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;

/**
 * Хранит разницу значений двух источников.
 */
@Deprecated
public class Sub extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Sub sub;
	
	public Sub(Value<Double> source1, Value<Double> source2) {
		this(source1, source2, ValueImpl.DEFAULT_ID);
	}
	
	public Sub(Value<Double> source1, Value<Double> source2, String id) {
		super(id);
		sub = new ru.prolib.aquila.ta.indicator.Sub(source1, source2);
	}
	
	public Value<Double> getSourceValue1() {
		return sub.getFirstSource();
	}
	
	public Value<Double> getSourceValue2() {
		return sub.getSecondSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(sub.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
