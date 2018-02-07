package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;

public class ValueAxisViewportImpl implements ValueAxisViewport {
	private Range<CDecimal> valueRange;

	@Override
	public synchronized Range<CDecimal> getValueRange() {
		return valueRange;
	}

	@Override
	public synchronized void setValueRange(Range<CDecimal> range) {
		this.valueRange = range;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ValueAxisViewportImpl.class ) {
			return false;
		}
		ValueAxisViewportImpl o = (ValueAxisViewportImpl) other;
		return new EqualsBuilder()
				.append(o.valueRange, valueRange)
				.isEquals();
	}

}
