package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.ChartElement;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;

@Deprecated // TODO: change to ruler renderer
public interface AxisRenderer extends ChartElement {
	
	AxisPosition getAxisPosition();
	void setAxisPosition(AxisPosition position);
	
	/**
	 * Get rectangle of axis painting area.
	 * <p>
	 * The axis painting area may be linked to whole data set but shouldn't be
	 * linked to viewport because it may lead to weird jumping between frames.
	 * <p>
	 * @param layout - chart layout
	 * @param device - painting device (depends on painting method)
	 * @return rectangle of painting area or null if painting is disabled
	 */
	Rectangle getPaintArea(ChartLayout layout, Object device);
	
	void paint(BCDisplayContext context, Object device);

}
