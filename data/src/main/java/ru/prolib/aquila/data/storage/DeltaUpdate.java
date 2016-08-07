package ru.prolib.aquila.data.storage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class DeltaUpdate {
	private final Instant updateTime;
	private final boolean isSnapshot;
	private final Map<Integer, Object> contents;

	public DeltaUpdate(Instant updateTime, boolean isSnapshot, Map<Integer, Object> contents) {
		this.updateTime = updateTime;
		this.isSnapshot = isSnapshot;
		this.contents = new HashMap<>(contents);
	}
	
	public Instant getUpdateTime() {
		return updateTime;
	}
	
	public boolean isSnapshot() {
		return isSnapshot;
	}
	
	public Map<Integer, Object> getContents() {
		return contents;
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
