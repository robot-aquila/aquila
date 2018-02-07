package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Graphics2D;

import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.ChartElementImpl;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisRenderer;

abstract public class SwingAxisRenderer extends ChartElementImpl implements AxisRenderer {
	protected AxisPosition position;
	
	public SwingAxisRenderer(AxisPosition position) {
		this.position = position;
	}

	@Override
	public synchronized AxisPosition getAxisPosition() {
		return position;
	}

	@Override
	public synchronized void setAxisPosition(AxisPosition position) {
		this.position = position;
	}
	
	@Override
	public synchronized Rectangle getPaintArea(ChartLayout layout, Object device) {
		return getRulerArea(layout, (Graphics2D) device);
	}

	@Override
	public synchronized void paint(BCDisplayContext context, Object device) {
		paintRuler(context, (Graphics2D) device);
	}
	
	abstract protected Rectangle getRulerArea(ChartLayout layout, Graphics2D graphics);
	abstract protected void paintRuler(BCDisplayContext context, Graphics2D graphics);

}
