package ru.prolib.aquila.utils.experimental.chart.axis;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.math.RoundingMode;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

/**
 * The mapper to display value axis which points up. This implementation is for
 * case when display height is higher than range of displaying values.
 */
public class ValueAxisDisplayMapperVUD implements ValueAxisDisplayMapper {
	private static final int BASE_SCALE = 5;
	private final Range<CDecimal> valueRange;
	private final int y, height, scale;
	private final CDecimal ratio;
	
	public ValueAxisDisplayMapperVUD(int y, int height, Range<CDecimal> valueRange) {
		this.valueRange = valueRange;
		this.y = y;
		this.height = height;
		this.scale = Math.max(valueRange.getMin().getScale(), valueRange.getMax().getScale());
		CDecimal dh = of((long) height);
		CDecimal vr = valueRange.getMax().subtract(valueRange.getMin());
		if ( vr.compareTo(dh) > 0 ) {
			throw new IllegalArgumentException("Display height " + height
					+ " is too small for " + valueRange);
		}
		int fs = BASE_SCALE + scale;
		CDecimal ratioPerPixel = vr.divideExact(dh, fs, RoundingMode.HALF_UP);
		vr = vr.add(ratioPerPixel);
		this.ratio = dh.divideExact(vr, fs, RoundingMode.DOWN);
	}
	
	@Override
	public int getPlotStart() {
		return y;
	}
	
	@Override
	public int getPlotSize() {
		return height;
	}
	
	@Override
	public Segment1D getPlot() {
		return new Segment1D(y, height);
	}
	
	@Override
	public AxisDirection getAxisDirection() {
		return AxisDirection.UP;
	}
	
	public CDecimal getRatio() {
		return ratio;
	}

	@Override
	public Range<CDecimal> getValueRange() {
		return valueRange;
	}

	@Override
	public CDecimal getMinValue() {
		return valueRange.getMin();
	}

	@Override
	public CDecimal getMaxValue() {
		return valueRange.getMax();
	}

	@Override
	public int toDisplay(CDecimal value) {
		int offset = value.subtract(valueRange.getMin())
				.multiply(ratio)
				.withScale(0, RoundingMode.HALF_UP)
				.toBigDecimal()
				.intValue();
		return y + height - 1 - offset;
	}

	@Override
	public CDecimal toValue(int display) {
		int offset = height - 1 - (display - y);
		return CDecimalBD.of((long)offset)
			.divideExact(ratio, scale + BASE_SCALE, RoundingMode.HALF_UP)
			.add(valueRange.getMin())
			.withScale(scale);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ValueAxisDisplayMapperVUD.class ) {
			return false;
		}
		ValueAxisDisplayMapperVUD o = (ValueAxisDisplayMapperVUD) other;
		return new EqualsBuilder()
				.append(o.y, y)
				.append(o.height, height)
				.append(o.valueRange, valueRange)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("y", y)
				.append("height", height)
				.append("valueRange", valueRange)
				.toString();
	}

}
