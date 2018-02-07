package ru.prolib.aquila.utils.experimental.chart;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 1-dimensional segment.
 */
public class Segment1D {
	private final int start, length;
	
	public Segment1D(int start, int length) {
		this.start = start;
		this.length = length;
	}
	
	/**
	 * Get start coordinate of segment.
	 * <p>
	 * @return segment start coordinate
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Get length of segment.
	 * <p>
	 * @return segment length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Get end coordinate of segment.
	 * <p>
	 * @return segment end coordinate
	 */
	public int getEnd() {
		return start + length - 1;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Segment1D.class ) {
			return false;
		}
		Segment1D o = (Segment1D) other;
		return new EqualsBuilder()
				.append(o.start, start)
				.append(o.length, length)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("start", start)
				.append("length", length)
				.toString();
	}

}
