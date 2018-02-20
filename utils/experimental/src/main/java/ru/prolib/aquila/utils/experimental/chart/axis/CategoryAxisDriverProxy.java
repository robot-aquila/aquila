package ru.prolib.aquila.utils.experimental.chart.axis;

import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;

public interface CategoryAxisDriverProxy {
	
	void registerForRulers(ChartSpaceManager spaceManager);
	CategoryAxisDisplayMapper getCurrentMapper();
	CategoryAxisRulerRenderer getRulerRenderer(String rendererID);

}
