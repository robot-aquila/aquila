package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.StringUtils;

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
	
	/**
	 * Get number of decimals.
	 * <p>
	 * This method trying to determine scale value converting the double to the
	 * string and counting the number non-zero trailing digits. This will work
	 * for double value which is used only to storing values. If the double is a
	 * result of some math calculations the result of this call may be wrong.
	 * Use it with caution.
	 * <p>
	 * @param value - value to determine scale of
	 * @return number of significant decimals
	 */
	public int scaleOf(double value) {
		String dummy = String.valueOf(value);
		dummy = StringUtils.stripEnd(dummy, "0");
		int r = StringUtils.lastIndexOf(dummy, '.'), scale = 0;
		if ( r > -1 ) {
			scale = dummy.length() - r - 1;
		}
		return scale;
	}

}
