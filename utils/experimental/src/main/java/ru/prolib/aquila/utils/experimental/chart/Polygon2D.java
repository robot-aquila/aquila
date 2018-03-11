package ru.prolib.aquila.utils.experimental.chart;

import java.awt.Polygon;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class Polygon2D extends Polygon {
	private static final long serialVersionUID = 1L;
	
	public Polygon2D() {
		
	}
	
	public Polygon2D addPointEx(int x, int y) {
		addPoint(x, y);
		return this;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Polygon2D.class ) {
			return false;
		}
		Polygon2D o = (Polygon2D) other;
		return new EqualsBuilder()
				.append(o.npoints, npoints)
				.append(o.xpoints, xpoints)
				.append(o.ypoints, ypoints)
				.isEquals();
	}

}
