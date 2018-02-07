package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public interface CategoryAxisDriver extends AxisDriver {
	
	// TODO:
	//void setPreferredBarSizePx(Integer size);
	//void setPreferredViewportSize(Integer size);
	//Integer getPreferredBarWidthPx();
	//Integer getPreferredViewportSize();

	/**
	 * Create axis to display mapper.
	 * <p>
	 * @param segment - display segment representing axis values
	 * @param viewport - viewport
	 * @return display mapper of an axis
	 */
	CategoryAxisDisplayMapper createMapper(Segment1D segment, CategoryAxisViewport viewport);
	
}
