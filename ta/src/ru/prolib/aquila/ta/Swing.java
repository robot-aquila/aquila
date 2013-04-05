package ru.prolib.aquila.ta;

/**
 * Свинг.
 * 
 * Дескриптор колебательного движения от одной разворотной точки до другой.
 * 
 * 2012-02-11
 * $Id: Swing.java 200 2012-02-11 14:03:38Z whirlwind $
 */
public class Swing {
	private final Pivot p1;
	private final Pivot p2;
	
	public Swing(Pivot p1, Pivot p2) {
		super();
		if ( p1.getId() > p2.getId() ) {
			this.p1 = p2;
			this.p2 = p1;
		} else {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
	
	public boolean isUptrend() {
		return p1.isMin();
	}
	
	public boolean isDowntrend() {
		return p1.isMax();
	}
	
	public int distance() {
		return p2.distance(p1);
	}
	
	public double impulse() {
		if ( isUptrend() ) {
			return p2.getHigh() - p1.getLow();
		} else {
			return p1.getHigh() - p2.getLow();
		}
	}
	
	public Pivot pivot1() {
		return p1;
	}
	
	public Pivot pivot2() {
		return p2;
	}

}
