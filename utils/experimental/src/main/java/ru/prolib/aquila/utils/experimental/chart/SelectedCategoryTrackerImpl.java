package ru.prolib.aquila.utils.experimental.chart;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SelectedCategoryTrackerImpl implements SelectedCategoryTracker {
	private boolean selected;
	private Integer absoluteIndex, visibleIndex;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public int getAbsoluteIndex() {
		if ( ! selected ) {
			throw new IllegalStateException();
		}
		return absoluteIndex;
	}

	@Override
	public int getVisibleIndex() {
		if ( ! selected ) {
			throw new IllegalStateException();
		}
		return visibleIndex;
	}
	
	public void makeDeselected() {
		this.selected = false;
		this.absoluteIndex = 0;
		this.visibleIndex = 0;
	}
	
	public void makeSelected(int absoluteIndex, int visibleIndex) {
		this.selected = true;
		this.absoluteIndex = absoluteIndex;
		this.visibleIndex = visibleIndex;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SelectedCategoryTrackerImpl.class ) {
			return false;
		}
		SelectedCategoryTrackerImpl o = (SelectedCategoryTrackerImpl) other;
		return new EqualsBuilder()
				.append(o.selected, selected)
				.append(o.absoluteIndex, absoluteIndex)
				.append(o.visibleIndex, visibleIndex)
				.build();
	}

}
