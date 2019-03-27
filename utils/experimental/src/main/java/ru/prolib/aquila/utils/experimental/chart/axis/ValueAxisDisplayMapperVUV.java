package ru.prolib.aquila.utils.experimental.chart.axis;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.math.RoundingMode;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

/**
 * The mapper to display value axis which points up. This implementation is for
 * case when range of displaying values is greater than display height.
 */
public class ValueAxisDisplayMapperVUV implements ValueAxisDisplayMapper {
	private static final int BASE_SCALE = 5;
	private final Range<CDecimal> valueRange;
	private final int y, height, scale;
	private final CDecimal ratio;

	public ValueAxisDisplayMapperVUV(int y, int height, Range<CDecimal> valueRange) {
		this.valueRange = valueRange;
		this.y = y;
		this.height = height;
		this.scale = Math.max(valueRange.getMin().getScale(), valueRange.getMax().getScale());
		CDecimal vr = valueRange.getMax().subtract(valueRange.getMin());
		CDecimal dh = of((long) height).withUnit(vr.getUnit());
		this.ratio = vr.divideExact(dh, BASE_SCALE + scale, RoundingMode.CEILING);
		if ( ratio.compareTo(CDecimalBD.of(1L)) < 0 ) {
			throw new IllegalArgumentException("Range " + valueRange
					+ " is to short for height " + height + ": ratio=" + ratio);
		}
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
				.divide(ratio)
				.toBigDecimal()
				.intValue();
		int r = y + height - 1 - offset;
		return r < y ? y : r;
	}

	@Override
	public CDecimal toValue(int display) {
		int offset = height - 1 - (display - y);
		return CDecimalBD.of((long)offset)
				.multiply(ratio)
				.add(valueRange.getMin())
				.withScale(scale);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ValueAxisDisplayMapperVUV.class ) {
			return false;
		}
		ValueAxisDisplayMapperVUV o = (ValueAxisDisplayMapperVUV) other;
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
