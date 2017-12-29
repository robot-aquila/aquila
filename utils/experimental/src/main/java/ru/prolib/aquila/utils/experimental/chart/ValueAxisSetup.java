package ru.prolib.aquila.utils.experimental.chart;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

@Deprecated
public class ValueAxisSetup {
	private final CDecimal minVisibleValue, maxVisibleValue;
	private final int lengthPx, gridStepCount, gridStepPx;
	
	public ValueAxisSetup(CDecimal minVisibleValue, CDecimal maxVisibleValue,
			int lengthPx, int gridStepCount, int gridStepPx)
	{
		this.minVisibleValue = minVisibleValue;
		this.maxVisibleValue = maxVisibleValue;
		this.lengthPx = lengthPx;
		this.gridStepCount = gridStepCount;
		this.gridStepPx = gridStepPx;
	}
	
	/**
	 * Get minimum visible value.
	 * <p>
	 * @return minimum visible value
	 */
	public CDecimal getMinVisibleValue() {
		return minVisibleValue;
	}
	
	/**
	 * Get maximum visible value.
	 * <p>
	 * @return maximum value
	 */
	public CDecimal getMaxVisibleValue() {
		return maxVisibleValue;
	}
	
	/**
	 * Get visible space length in pixels.
	 * <p>
	 * @return length in pixels
	 */
	public int getLengthPx() {
		return lengthPx;
	}
	
	/**
	 * Get count of grid steps projected to visible space.
	 * <p>
	 * @return number of grid steps
	 */
	public int getGridStepCount() {
		return gridStepCount;
	}
	
	/**
	 * Get size of grid step in pixels.
	 * <p>
	 * @return size in pixels
	 */
	public int getGridStepPx() {
		return gridStepPx;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ValueAxisSetup.class ) {
			return false;
		}
		ValueAxisSetup o = (ValueAxisSetup) other;
		return new EqualsBuilder()
				.append(o.gridStepCount, gridStepCount)
				.append(o.gridStepPx, gridStepPx)
				.append(o.lengthPx, lengthPx)
				.append(o.maxVisibleValue, maxVisibleValue)
				.append(o.minVisibleValue, minVisibleValue)
				.isEquals();
	}

}
