package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.axis.AxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;

/**
 * Manager of chart space.
 */
public interface ChartSpaceManager {
	
	void registerAxis(AxisDriver driver);
	@Deprecated
	void setRulerVisibility(RulerID rulerID, boolean visible);
	@Deprecated
	void setRulerDisplayPriority(RulerID rulerID, int priority);
	RulerSetup getRulerSetup(RulerID rulerID);
	RulerSetup getUpperRulerSetup(String axisID, String rendererID);
	RulerSetup getLowerRulerSetup(String axisID, String rendererID);
	ChartSpaceLayout prepareLayout(Segment1D displaySpace, Segment1D dataSpace, Object device);
	ChartSpaceLayout prepareLayout(Segment1D displaySpace, int rulersMaxSpace, Object device);

}
