package ru.prolib.aquila.ta;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Шаблон бара.
 */
public class BarPattern {
	private final int top,bottom,open,close;
	
	/**
	 * Конструктор
	 * @param top индекс верхнего сегмента бара 
	 * @param bottom индекс сегмента основания бара
	 * @param open индекс сегмента открытия бара
	 * @param close индекс сегмента закрытия бара
	 */
	public BarPattern(int top, int bottom, int open, int close) {
		super();
		this.top = top; 
		this.bottom = bottom;
		this.open = open;
		this.close = close;
	}
	
	public int getHeight() {
		return top - bottom + 1;
	}
	
	public int getTop() {
		return top;
	}
	
	public int getBottom() {
		return bottom;
	}
	
	public int getOpen() {
		return open;
	}
	
	public int getClose() {
		return close;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == null ) {
			return false;
		}
		if ( o == this ) {
			return true;
		}
		if ( o.getClass() != getClass() ) {
			return false;
		}
		BarPattern other = (BarPattern)o;
		return new EqualsBuilder()
			.append(top, other.top)
			.append(bottom, other.bottom)
			.append(open, other.open)
			.append(close, other.close)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(top)
			.append(bottom)
			.append(open)
			.append(close)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return "T:" + top + " B:" + bottom + " O:" + open + " C:" + close;
	}

}
