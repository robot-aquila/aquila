package ru.prolib.aquila.core.BusinessEntities;

public class DoubleUtils {
	private int scale;
	private double epsilon;
	private double power;
	
	public DoubleUtils(int scale) {
		setScale(scale);
	}
	
	public DoubleUtils() {
		this(2);
	}
	
	public void setScale(int scale) {
		this.scale = scale;
		epsilon = Math.pow(10, -scale - 1) * 5;
		power = Math.pow(10, scale);
	}
	
	public int getScale() {
		return scale;
	}
	
	public double getEpsilon() {
		return epsilon;
	}
	
	public double getPower() {
		return power;
	}
	
	public boolean isEquals(double a, double b) {
		return Math.abs(a - b) <= epsilon;
	}
	
	public double round(double a) {
		return (a > 0 ? Math.floor(a * power + 0.5d) : Math.ceil(a * power - 0.5d)) / power;
	}

}
