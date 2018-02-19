package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;

/**
 * Common class to represent a single ruler label.
 * Useful both for value and category axis labels.
 */
public class RLabel {
	private final CDecimal value;
	private final int categoryIndex;
	private final String text;
	private final int coord;
	
	public RLabel(CDecimal value, int categoryIndex, String text, int coord) {
		this.value = value;
		this.categoryIndex = categoryIndex;
		this.text = text;
		this.coord = coord;
	}
	
	/**
	 * Constructor of value axis label.
	 * <p>
	 * @param value - value associated with the label
	 * @param text - label text
	 * @param coord - label start coordinate
	 */
	public RLabel(CDecimal value, String text, int coord) {
		this(value, -1, text, coord);
	}
	
	/**
	 * Constructor of category axis label.
	 * <p>
	 * @param categoryIndex - index of category associated with the label
	 * @param text - label text
	 * @param coord - label start coordinate
	 */
	public RLabel(int categoryIndex, String text, int coord) {
		this(null, categoryIndex, text, coord);
	}
	
	public CDecimal getValue() {
		return value;
	}
	
	public int getCategoryIndex() {
		return categoryIndex;
	}
	
	public String getText() {
		return text;
	}
	
	public int getCoord() {
		return coord;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[value=" + value
				+ " category=" + categoryIndex
				+ " text=" + text
				+ " coord=" + coord
				+ "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RLabel.class ) {
			return false;
		}
		RLabel o = (RLabel) other;
		return new EqualsBuilder()
				.append(o.categoryIndex, categoryIndex)
				.append(o.coord, coord)
				.append(o.text, text)
				.append(o.value, value)
				.isEquals();
	}

}
