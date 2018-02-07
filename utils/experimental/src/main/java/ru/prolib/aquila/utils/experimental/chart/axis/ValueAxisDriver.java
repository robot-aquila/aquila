package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public interface ValueAxisDriver extends AxisDriver {

	/**
	 * Create axis to display mapper.
	 * <p>
	 * @param segment - display segment representing axis values
	 * @param viewport - viewport
	 * @return display mapper of an axis
	 */
	ValueAxisDisplayMapper createMapper(Segment1D segment, ValueAxisViewport viewport);

}
