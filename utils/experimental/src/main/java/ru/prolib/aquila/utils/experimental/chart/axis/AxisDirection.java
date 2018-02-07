package ru.prolib.aquila.utils.experimental.chart.axis;

public enum AxisDirection {
	RIGHT(true),
	UP(false);
	
	private final boolean horizontal;
	
	private AxisDirection(boolean horizontal) {
		this.horizontal = horizontal;
	}
	
	public boolean isHorizontal() {
		return horizontal;
	}
	
	public boolean isVertical() {
		return ! horizontal;
	}
	
}
