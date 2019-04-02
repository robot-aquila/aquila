package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class CategoryAxisViewportImpl implements CategoryAxisViewport {
	private Integer first, last, number, preferredNumberOfBars;

	@Override
	public synchronized int getFirstCategory() {
		return first;
	}

	@Override
	public synchronized int getLastCategory() {
		return last;
	}

	@Override
	public synchronized int getNumberOfCategories() {
		return number;
	}

	@Override
	public synchronized void setCategoryRangeByFirstAndNumber(int first, int number) {
		this.first = first;
		this.number = number;
		this.last = first + number - 1;
	}

	@Override
	public synchronized void setCategoryRangeByLastAndNumber(int last, int number) {
		this.last = last;
		this.number = number;
		this.first = last - number + 1;
	}

	@Override
	public synchronized void setCategoryRangeByFirstAndLast(int first, int last) {
		this.first = first;
		this.last = last;
		this.number = last - first + 1;
	}

	@Override
	public synchronized Integer getPreferredNumberOfBars() {
		return preferredNumberOfBars;
	}

	@Override
	public synchronized void setPreferredNumberOfBars(Integer number) {
		preferredNumberOfBars = number;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CategoryAxisViewportImpl.class ) {
			return false;
		}
	CategoryAxisViewportImpl o = (CategoryAxisViewportImpl) other;
		return new EqualsBuilder()
				.append(o.getFirstCategory(), getFirstCategory())
				.append(o.getLastCategory(), getLastCategory())
				.append(o.getPreferredNumberOfBars(), getPreferredNumberOfBars())
				.isEquals();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
