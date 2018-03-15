package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class GridLinesSetup {
	protected final RulerRendererID rendererID;
	protected boolean visible;
	protected int displayPriority;
	
	public GridLinesSetup(RulerRendererID rendererID, boolean visible, int displayPriority) {
		this.rendererID = rendererID;
		this.visible = visible;
		this.displayPriority = displayPriority;
	}
	
	public GridLinesSetup(RulerRendererID rendererID) {
		this(rendererID, true, 0);
	}
	
	public RulerRendererID getRendererID() {
		return rendererID;
	}
	
	public int getDisplayPriority() {
		return displayPriority;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public GridLinesSetup setDisplayPriority(int displayPriority) {
		this.displayPriority = displayPriority;
		return this;
	}
	
	public GridLinesSetup setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("rendererID", rendererID)
				.append("visible", visible)
				.append("displayPriority", displayPriority)
				.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != GridLinesSetup.class ) {
			return false;
		}
		GridLinesSetup o = (GridLinesSetup) other;
		return new EqualsBuilder()
				.append(o.rendererID, rendererID)
				.append(o.visible, visible)
				.append(o.displayPriority, displayPriority)
				.isEquals();
	}

}
