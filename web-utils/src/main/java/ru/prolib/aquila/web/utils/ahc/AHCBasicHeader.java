package ru.prolib.aquila.web.utils.ahc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.message.BasicHeader;

public class AHCBasicHeader extends BasicHeader {
	private static final long serialVersionUID = 1L;

	public AHCBasicHeader(String name, String value) {
		super(name, value);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AHCBasicHeader.class ) {
			return false;
		}
		AHCBasicHeader o = (AHCBasicHeader) other;
		return new EqualsBuilder()
			.append(o.getName(), getName())
			.append(o.getValue(), getValue())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(99173, 53)
			.append(getName())
			.append(getValue())
			.toHashCode();
	}

}
