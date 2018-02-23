package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

/**
 * Bar chart display context.
 */
public interface BCDisplayContext {

	CategoryAxisDisplayMapper getCategoryAxisMapper();
	ValueAxisDisplayMapper getValueAxisMapper();
	Rectangle getPlotArea();
	
}
