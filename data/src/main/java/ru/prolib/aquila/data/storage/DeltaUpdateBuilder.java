package ru.prolib.aquila.data.storage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class DeltaUpdateBuilder {
	private final Map<Integer, Object> contents;
	private Instant time;
	private boolean isSnapshot;
	
	public DeltaUpdateBuilder() {
		contents = new HashMap<>();
	}
	
	public DeltaUpdateBuilder withTime(Instant time) {
		this.time = time;
		return this;
	}
	
	public DeltaUpdateBuilder withSnapshot(boolean isSnapshot) {
		this.isSnapshot = isSnapshot;
		return this;
	}
	
	public DeltaUpdateBuilder withToken(int token, Object value) {
		contents.put(token, value);
		return this;
	}
	
	public DeltaUpdateBuilder clearTokens() {
		contents.clear();
		return this;
	}
	
	public DeltaUpdate buildUpdate() {
		return new DeltaUpdate(time, isSnapshot, contents);
	}

}
