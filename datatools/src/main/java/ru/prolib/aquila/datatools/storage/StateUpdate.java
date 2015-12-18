package ru.prolib.aquila.datatools.storage;

import java.time.LocalDateTime;

public class StateUpdate {
	private LocalDateTime timestamp;
	private byte[] data;
	private boolean isFullRefresh;
	
	public StateUpdate(LocalDateTime timestamp, byte[] data, boolean isFullRefresh) {
		super();
		this.timestamp = timestamp;
		this.data = data;
		this.isFullRefresh = isFullRefresh;
	}
	
	public StateUpdate(LocalDateTime timestamp, byte[] data) {
		this(timestamp, data, false);
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public boolean isFullRefresh() {
		return isFullRefresh;
	}

}
