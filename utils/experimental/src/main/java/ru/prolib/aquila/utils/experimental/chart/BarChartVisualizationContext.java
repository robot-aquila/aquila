package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.RangeInfo;

public interface BarChartVisualizationContext {

	int getFirstVisibleCategoryIndex();
	
	int getNumberOfVisibleCategories();
	
	double getMinVisibleValue();
	
	double getMaxVisibleValue();
	
	double toValue(int canvasX, int canvasY);
	
	int toCategoryIdx(int canvasX, int canvasY);
	
	int toCanvasX(int displayedCategoryIdx);

	int toCanvasX(double value);

	int toCanvasY(int displayedCategoryIdx);
	
	int toCanvasY(double value);

	double getStepX();

	RangeInfo getRangeInfo();

	Rectangle getPlotBounds();
	
}
