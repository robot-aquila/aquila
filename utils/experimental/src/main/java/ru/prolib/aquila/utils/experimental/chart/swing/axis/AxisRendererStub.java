package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.ChartElementImpl;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisRenderer;

public class AxisRendererStub extends ChartElementImpl
	implements AxisRenderer
{
	private AxisPosition axisPosition;
	
	public AxisRendererStub(AxisPosition axisPosition) {
		this.axisPosition = axisPosition;
	}

	@Override
	public AxisPosition getAxisPosition() {
		return axisPosition;
	}

	@Override
	public void setAxisPosition(AxisPosition position) {
		this.axisPosition = position;
	}

	@Override
	public Rectangle getPaintArea(ChartLayout layout, Object device) {
		return null;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {

	}

	@Override
	public void paint(BCDisplayContext context, Object device) {

	}

}
