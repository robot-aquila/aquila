package ru.prolib.aquila.utils.experimental.chart;

import org.apache.commons.lang3.builder.EqualsBuilder;

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
	
	/**
	 * Test that this segment contains all points of other segment.
	 * <p>
	 * @param other - segment that expected to be contained in 
	 * @return true if other segment is a part of this segment, false otherwise
	 */
	public boolean contains(Segment1D other) {
		int c1 = getStart(), c2 = getEnd(), oc1 = other.getStart(), oc2 = other.getEnd();
		return oc1 >= c1 && oc1 <= c2 && oc2 >= c1 && oc2 <= c2;
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
		return getClass().getSimpleName() + "[start=" + start
				+ ", length=" + length + "]";
	}

}
