package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;

public class SWTimeAxisRulerSetup extends RulerSetup {
	protected boolean showInnerLine, showOuterLine;

	public SWTimeAxisRulerSetup(RulerID rulerID) {
		super(rulerID);
		showInnerLine = true;
		showOuterLine = true;
	}
	
	@Override
	public SWTimeAxisRulerSetup setVisible(boolean visible) {
		super.setVisible(visible);
		return this;
	}
	
	@Override
	public SWTimeAxisRulerSetup setDisplayPriority(int displayPriority) {
		super.setDisplayPriority(displayPriority);
		return this;
	}
	
	public SWTimeAxisRulerSetup setShowInnerLine(boolean show) {
		this.showInnerLine = show;
		return this;
	}
	
	public SWTimeAxisRulerSetup setShowOuterLine(boolean show) {
		this.showOuterLine = show;
		return this;
	}
	
	public boolean isShowInnerLine() {
		return this.showInnerLine;
	}
	
	public boolean isShowOuterLine() {
		return this.showOuterLine;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SWTimeAxisRulerSetup.class ) {
			return false;
		}
		SWTimeAxisRulerSetup o = (SWTimeAxisRulerSetup) other;
		return new EqualsBuilder()
				.append(o.rulerID, rulerID)
				.append(o.displayPriority, displayPriority)
				.append(o.visible, visible)
				.append(o.showInnerLine, showInnerLine)
				.append(o.showOuterLine, showOuterLine)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("rulerID", rulerID)
				.append("visible", visible)
				.append("displayPriority", displayPriority)
				.append("showInnerLine", showInnerLine)
				.append("showOuterLine", showOuterLine)
				.toString();
	}

}
