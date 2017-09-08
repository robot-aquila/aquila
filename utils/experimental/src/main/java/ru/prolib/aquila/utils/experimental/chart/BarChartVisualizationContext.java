package ru.prolib.aquila.utils.experimental.chart;

public interface BarChartVisualizationContext<TCategory> {

	int getFirstVisibleCategoryIndex();
	
	int getNumberOfVisibleCategories();
	
	double getMinVisibleValue();
	
	double getMaxVisibleValue();
	
	double toValue(int canvasX, int canvasY);
	
	TCategory toCategory(int canvasX, int canvasY);
	
	int toCanvasX(TCategory category);
	
	int toCanvasX(double value);
	
	int toCanvasY(TCategory category);
	
	int toCanvasY(double value);
	
}
