package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.axis.AxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerID;

/**
 * Менеджер пространства диаграммы.
 */
public interface ChartSpaceManager {
	
	void registerAxis(AxisDriver driver);
	void setRulerVisibility(ChartRulerID rulerID, boolean visible);
	void setRulerDisplayPriority(ChartRulerID rulerID, int priority);
	ChartSpaceLayout prepareLayout(Segment1D displaySpace, Segment1D dataSpace, Object device);
	ChartSpaceLayout prepareLayout(Segment1D displaySpace, int rulersMaxSpace, Object device);

}
