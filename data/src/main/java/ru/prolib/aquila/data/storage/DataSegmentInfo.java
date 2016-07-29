package ru.prolib.aquila.data.storage;

import java.time.Instant;

/**
 * This class represents a common information about a data segment.
 * Most attributes are optional and may be undefined.
 */
public class DataSegmentInfo {
	private final String sourceID;
	private DataSegmentStatus status;
	private Instant updateTime;
	private String version;
	private Long recordCount;
	
	public DataSegmentInfo(String sourceID) {
		this.sourceID = sourceID;
	}
	
	public String getSourceID() {
		return sourceID;
	}
	
	public DataSegmentStatus getStatus() {
		return status;
	}
	
	public void setStatus(DataSegmentStatus status) {
		this.status = status;
	}
	
	public Instant getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Instant updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public Long getRecordCount() {
		return recordCount;
	}
	
	public void setRecordCount(Long recordCount) {
		this.recordCount = recordCount;
	}

}
