package ru.prolib.aquila.utils.experimental.chart;

abstract public class ChartElementImpl implements ChartElement {
	protected boolean visible = true;

	@Override
	public synchronized boolean isVisible() {
		return visible;
	}

	@Override
	public synchronized void setVisible(boolean visible) {
		this.visible = visible;
	}

}
