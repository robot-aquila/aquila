package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RulerSetup {
	protected final RulerID rulerID;
	protected boolean visible = true;
	protected int displayPriority = 0;
	
	public RulerSetup(RulerID rulerID, boolean visible, int displayPriority) {
		this.rulerID = rulerID;
		this.visible = visible;
		this.displayPriority = displayPriority;
	}
	
	public RulerSetup(RulerID rulerID) {
		this(rulerID, true, 0);
	}
	
	public RulerSetup setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	public RulerID getRulerID() {
		return rulerID;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public RulerSetup setDisplayPriority(int priority) {
		this.displayPriority = priority;
		return this;
	}
	
	public int getDisplayPriority() {
		return displayPriority;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RulerSetup.class ) {
			return false;
		}
		RulerSetup o = (RulerSetup) other;
		return new EqualsBuilder()
				.append(o.rulerID, rulerID)
				.append(o.displayPriority, displayPriority)
				.append(o.visible, visible)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("rulerID", rulerID)
				.append("visible", visible)
				.append("displayPriority", displayPriority)
				.toString();
	}

}