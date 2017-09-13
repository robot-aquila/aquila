package ru.prolib.aquila.data.storage.segstor;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class SegmentMetaDataImpl implements SegmentMetaData {
	private String path;
	private String hashCode;
	private Instant updateTime;
	private long numberOfElements;

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getHashCode() {
		return hashCode;
	}

	@Override
	public Instant getUpdateTime() {
		return updateTime;
	}

	@Override
	public long getNumberOfElements() {
		return numberOfElements;
	}
	
	public SegmentMetaDataImpl setPath(String path) {
		this.path = path;
		return this;
	}
	
	public SegmentMetaDataImpl setHashCode(String hashCode) {
		this.hashCode = hashCode;
		return this;
	}
	
	public SegmentMetaDataImpl setUpdateTime(Instant updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	
	public SegmentMetaDataImpl setNumberOfElements(long number) {
		this.numberOfElements = number;
		return this;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[path=" + path
				+ " hash=" + hashCode
				+ " numberOfElements=" + numberOfElements
				+ " updateTime=" + updateTime + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SegmentMetaDataImpl.class ) {
			return false;
		}
		SegmentMetaDataImpl o = (SegmentMetaDataImpl) other;
		return new EqualsBuilder()
				.append(o.path, path)
				.append(o.hashCode, hashCode)
				.append(o.numberOfElements, numberOfElements)
				.append(o.updateTime, updateTime)
				.isEquals();
	}

}
