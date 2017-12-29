package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;

public interface BarChartViewport {

	Range<CDecimal> getPreferredValueRange();
	
	Range<CDecimal> getVisibleValueRange();
	
	void setPreferredValueRange(Range<CDecimal> range);
	
	void setVisibleValueRange(Range<CDecimal> range);
	
	int getFirstVisibleCategory();
	
	int getNumberOfVisibleCategories();
	
	void setVisibleCategories(int firstCategory, int numberOfCategories);

}
