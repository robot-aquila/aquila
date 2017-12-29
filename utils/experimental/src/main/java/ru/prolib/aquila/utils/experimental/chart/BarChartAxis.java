package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

public interface BarChartAxis {

	boolean isVisible();

	BarChartAxis setVisible(boolean visible);

	LabelFormatter<?> getLabelFormatter();

	BarChartAxis setLabelFormatter(LabelFormatter<?> labelFormatter);

	void paint(BarChartVisualizationContext context, AxisLabelProvider labelProvider);
	
	/**
	 * Get rectangle of axis painting area.
	 * <p>
	 * @param viewport - viewport data of the painting chart
	 * @param layout - chart layout
	 * @return rectangle of painting area or null if painting is disabled
	 */
	Rectangle getPaintArea(BarChartViewport viewport, ChartLayout layout);

}
