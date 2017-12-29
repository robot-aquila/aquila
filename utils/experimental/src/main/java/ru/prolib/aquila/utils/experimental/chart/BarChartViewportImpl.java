package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;

public class BarChartViewportImpl implements BarChartViewport {
	private int first, number;
	private Range<CDecimal> visible, preferred;
	
	public BarChartViewportImpl() {
		
	}

	@Override
	public int getFirstVisibleCategory() {
		return first;
	}

	@Override
	public int getNumberOfVisibleCategories() {
		return number;
	}

	@Override
	public Range<CDecimal> getPreferredValueRange() {
		return preferred;
	}

	@Override
	public Range<CDecimal> getVisibleValueRange() {
		return visible;
	}

	@Override
	public void setVisibleCategories(int firstCategory, int numberOfCategories) {
		this.first = firstCategory;
		this.number = numberOfCategories;
	}

	@Override
	public void setPreferredValueRange(Range<CDecimal> range) {
		this.preferred = range;
	}

	@Override
	public void setVisibleValueRange(Range<CDecimal> range) {
		this.visible = range;
	}

}
