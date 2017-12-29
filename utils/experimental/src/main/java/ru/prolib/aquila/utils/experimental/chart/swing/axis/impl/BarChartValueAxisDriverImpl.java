package ru.prolib.aquila.utils.experimental.chart.swing.axis.impl;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BarChartValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.BarChartViewport;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;

public class BarChartValueAxisDriverImpl implements BarChartValueAxisDriver {
	private final int STATE_INIT = 0;
	private final int STATE_VIEWPORT_UPDATED = 1;
	private final int STATE_LAYOUT_UPDATED = 2;
	
	private BarChartViewport viewport;
	private ChartLayout layout;
	private ValueCoordConverter coordConverter;

	@Override
	public int toDisplay(CDecimal value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CDecimal toValue(int display) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Range<CDecimal> getVisibleValueRangle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateViewport(BarChartViewport viewport) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLayout(ChartLayout layout) {
		// TODO Auto-generated method stub

	}

}
