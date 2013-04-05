package ru.prolib.aquila.ta;

import ru.prolib.aquila.core.data.Candle;



public class BarPatternBuilderImpl implements BarPatternBuilder {
	
	public BarPatternBuilderImpl() {
		super();
	}

	@Override
	public BarPattern
		buildBarPattern(double zeroPrice, double segmentHeight, Candle bar) {
		return new BarPattern(
			Math.round((float)((bar.getHigh() - zeroPrice) / segmentHeight)),
			Math.round((float)((bar.getLow() - zeroPrice) / segmentHeight)),
			Math.round((float)((bar.getOpen() - zeroPrice) / segmentHeight)),
			Math.round((float)((bar.getClose() - zeroPrice) / segmentHeight))
		);
	}

}
