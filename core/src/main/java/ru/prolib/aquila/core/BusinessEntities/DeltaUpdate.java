package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class DeltaUpdate implements TStamped {
	private final Instant updateTime;
	private final boolean isSnapshot;
	private final Map<Integer, Object> contents;

	public DeltaUpdate(Instant updateTime, boolean isSnapshot, Map<Integer, Object> contents) {
		this.updateTime = updateTime;
		this.isSnapshot = isSnapshot;
		this.contents = new HashMap<>(contents);
	}

	@Override
	public Instant getTime() {
		return updateTime;
	}
	
	public boolean isSnapshot() {
		return isSnapshot;
	}
	
	public Map<Integer, Object> getContents() {
		return contents;
	}
	
	public boolean hasContents() {
		return contents.size() > 0;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DeltaUpdate.class ) {
			return false;
		}
		DeltaUpdate o = (DeltaUpdate) other;
		return new EqualsBuilder()
			.append(o.updateTime, updateTime)
			.append(o.isSnapshot, isSnapshot)
			.append(o.contents, contents)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + updateTime +
				(isSnapshot ? " snapshot " : " ") +
				contents + "]";
	}
	
}
