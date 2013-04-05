package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

/**
 * Полоса Боллинджера.
 * 
 * 2012-02-07
 * $Id: BollingerBand.java 205 2012-04-06 15:41:16Z whirlwind $
 */
@Deprecated
public class BollingerBand extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.BollingerBand bb;
	
	public BollingerBand(Value<Double> central, Value<Double> stdev, double k) {
		this(central, stdev, k, ValueImpl.DEFAULT_ID);
		
	}
	
	public BollingerBand(Value<Double> central, Value<Double> stdev,
						 double k, String id)
	{
		super(id);
		bb = new ru.prolib.aquila.ta.indicator.BollingerBand(central, stdev, k);
	}
	
	public Value<Double> getCentralLine() {
		return bb.getFirstSource();
	}
	
	public Value<Double> getStandardDeviation() {
		return bb.getSecondSource();
	}
	
	public double getFactor() {
		return bb.getWidth();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(bb.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
