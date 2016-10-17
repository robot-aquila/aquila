package ru.prolib.aquila.core.BusinessEntities;

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
	
	public DeltaUpdateBuilder withTime(String timeString) {
		this.time = Instant.parse(timeString);
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
	
	public DeltaUpdateBuilder withTokens(Map<Integer, Object> tokens) {
		contents.putAll(tokens);
		return this;
	}
	
	public DeltaUpdateBuilder withTokens(DeltaUpdate update) {
		contents.putAll(update.getContents());
		return this;
	}

}
