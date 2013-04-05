package ru.prolib.aquila.ta;

import java.util.LinkedList;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.ds.MarketData;

public class BarSequencePatternBuilderImpl
	implements BarSequencePatternBuilder
{
	private final BarPatternBuilder bb;
	
	public BarSequencePatternBuilderImpl() {
		this(new BarPatternBuilderImpl());
	}
	
	public BarSequencePatternBuilderImpl(BarPatternBuilder barBuilder) {
		super();
		bb = barBuilder;
	}
	
	public BarPatternBuilder getBarPatternBuilder() {
		return bb;
	}

	@Override
	public BarSequencePattern buildBarSequencePattern(MarketData data,
			int startBar, int numBars) throws BarPatternException
	{
		if ( numBars <= 0 ) {
			throw
			new BarPatternException("Num of bars should be greater than zero");
		}
		LinkedList<BarPattern> bars = new LinkedList<BarPattern>();
		try {
			Candle firstBar = data.getBar(startBar);
			double zeroPrice = firstBar.getLow();
			double segmentHeight = (firstBar.getHigh() - zeroPrice) / 3;
			bars.add(bb.buildBarPattern(zeroPrice, segmentHeight, firstBar));
			for ( int i = 1; i < numBars; i ++ ) {
				bars.add(bb.buildBarPattern(zeroPrice, segmentHeight,
					data.getBar(startBar + i)));
			}
		} catch ( ValueException e ) {
			throw
			new BarPatternException("Error during build of the pattern", e);
		}
		return new BarSequencePattern(bars);
	}

}
