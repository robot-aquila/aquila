package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;

public interface ValueAxisDisplayMapper extends AxisDisplayMapper {

	Range<CDecimal> getValueRange();
	CDecimal getMinValue();
	CDecimal getMaxValue();

	/**
	 * Convert from values area to display area.
	 * <p>
	 * @param value - value to convert
	 * @return appropriate display coordinate
	 */
	int toDisplay(CDecimal value);
	
	/**
	 * Convert from display area to values area.
	 * <p>
	 * @param display - coordinate of screen to convert from
	 * @return appropriate value
	 */
	CDecimal toValue(int display);
	
}
