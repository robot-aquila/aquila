package ru.prolib.aquila.utils.experimental.chart;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class Point2D {
	public static final Point2D ZERO = new Point2D(0, 0);
	
	private final int x, y;
	
	public Point2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Point2D.class ) {
			return false;
		}
		Point2D o = (Point2D) other;
		return new EqualsBuilder()
				.append(o.x, x)
				.append(o.y, y)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + x + "," + y + "]";
	}

}
