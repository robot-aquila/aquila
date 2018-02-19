package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import java.time.LocalTime;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class MTFLabel {
	private final LocalTime time;
	private final String text;
	private final boolean isHourBoundary;
	
	/**
	 * Constructor.
	 * <p>
	 * @param time - time associated with the label
	 * @param text - label text
	 * @param isHourBoundary - true if the label is first in hour
	 */
	public MTFLabel(LocalTime time, String text, boolean isHourBoundary) {
		this.time = time;
		this.text = text;
		this.isHourBoundary = isHourBoundary;
	}
	
	/**
	 * Get time associated with the label.
	 * <p>
	 * @return label time
	 */
	public LocalTime getTime() {
		return time;
	}
	
	/**
	 * Get label text.
	 * <p>
	 * @return text of label
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Test that the label is hour boundary.
	 * <p>
	 * @return true if this label is first in hour
	 */
	public boolean isHourBoundary() {
		return isHourBoundary;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[time=" + time
				+ " text=" + text
				+ " hourBoundary=" + isHourBoundary + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MTFLabel.class ) {
			return false;
		}
		MTFLabel o = (MTFLabel) other;
		return new EqualsBuilder()
				.append(time, o.time)
				.append(text, o.text)
				.append(isHourBoundary, o.isHourBoundary)
				.isEquals();
	}

}
