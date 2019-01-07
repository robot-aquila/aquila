package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TStampedVal<T> implements TStamped {
	private final Instant time;
	private final T value;
	
	public TStampedVal(Instant time, T value) {
		this.time = time;
		this.value = value;
	}

	@Override
	public Instant getTime() {
		return time;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(100976245, 905)
				.append(time)
				.append(value)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TStampedVal.class ) {
			return false;
		}
		TStampedVal<?> o = (TStampedVal<?>) other;
		return new EqualsBuilder()
				.append(o.time, time)
				.append(o.value, value)
				.build();
	}

}
