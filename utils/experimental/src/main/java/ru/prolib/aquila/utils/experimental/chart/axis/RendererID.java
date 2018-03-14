package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RendererID {
	private final String axisID, rendererID;
	
	public RendererID(String axisID, String rendererID) {
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
		if ( other == null || other.getClass() != RendererID.class ) {
			return false;
		}
		RendererID o = (RendererID) other;
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

}
