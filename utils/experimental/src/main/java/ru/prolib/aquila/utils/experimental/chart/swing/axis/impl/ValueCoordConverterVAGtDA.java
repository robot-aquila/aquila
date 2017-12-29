package ru.prolib.aquila.utils.experimental.chart.swing.axis.impl;

import java.math.RoundingMode;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.utils.Range;

public class ValueCoordConverterVAGtDA implements ValueCoordConverter {
	private static final int BASE_SCALE = 5;
	private final Range<CDecimal> valueRange, pixelRange;
	private final CDecimal ratio;
	private final int scale;

	public ValueCoordConverterVAGtDA(Range<CDecimal> valueRange, Range<Integer> pixelRange) {
		this.valueRange = valueRange;
		this.pixelRange = new Range<>(
				CDecimalBD.of((long) pixelRange.getMin()),
				CDecimalBD.of((long) pixelRange.getMax()));
		this.scale = Math.max(valueRange.getMin().getScale(), valueRange.getMax().getScale());
		this.ratio = valueRange.getMax().subtract(valueRange.getMin())
				.divideExact(this.pixelRange.getMax().subtract(this.pixelRange.getMin()),
						BASE_SCALE + scale, RoundingMode.CEILING);
	}
	
	public CDecimal getRatio() {
		return ratio;
	}
	
	public CDecimal getMinValue() {
		return valueRange.getMin();
	}
	
	public CDecimal getMaxValue() {
		return valueRange.getMax();
	}
	
	public int getDisplayMin() {
		return pixelRange.getMin().toBigDecimal().intValue();
	}
	
	public int getDisplayMax() {
		return pixelRange.getMax().toBigDecimal().intValue();
	}
	
	public CDecimal getValueRange() {
		return valueRange.getMax().subtract(valueRange.getMin());
	}
	
	public int getDisplaySize() {
		return pixelRange.getMax().subtract(pixelRange.getMin()).toBigDecimal().intValue();
	}
	
	@Override
	public int toDisplay(CDecimal value) {
		return value.subtract(valueRange.getMin())
				.divideExact(ratio, 0, RoundingMode.HALF_UP)
				.add(pixelRange.getMin())
				.toBigDecimal()
				.intValue();
	}

	@Override
	public CDecimal toValue(int display) {
		return CDecimalBD.of((long) display).subtract(pixelRange.getMin())
				.multiply(ratio)
				.add(valueRange.getMin())
				.withScale(scale, RoundingMode.HALF_UP);
	}

}
