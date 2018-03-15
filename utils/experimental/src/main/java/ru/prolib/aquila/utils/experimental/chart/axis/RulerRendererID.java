package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RulerRendererID {
	private final String axisID, rendererID;
	
	public RulerRendererID(String axisID, String rendererID) {
		this.axisID = axisID;
		this.rendererID = rendererID;
	}
	
	public String getAxisID() {
		return axisID;
	}
	
	public String getRendererID() {
		return rendererID;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != RulerRendererID.class ) {
			return false;
		}
		RulerRendererID o = (RulerRendererID) other;
		return new EqualsBuilder()
				.append(o.axisID, axisID)
				.append(o.rendererID, rendererID)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("axisID", axisID)
				.append("rendererID", rendererID)
				.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(998271, 117)
				.append(axisID)
				.append(rendererID)
				.toHashCode();
	}

}
