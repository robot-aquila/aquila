package ru.prolib.aquila.utils.experimental.chart;

public interface BarChartAxis<TCategory> {
	
	BarChartAxis<TCategory> setVisible(boolean visible);
	
	void paint(BarChartVisualizationContext<TCategory> context);

}
