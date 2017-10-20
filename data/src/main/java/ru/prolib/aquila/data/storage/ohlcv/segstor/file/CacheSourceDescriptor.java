package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CacheSourceDescriptor {
	private final String path;
	private final String hash;
	
	public CacheSourceDescriptor(String hash, String path) {
		if ( StringUtils.containsWhitespace(hash) ) {
			throw new IllegalArgumentException("Incorrect hash code: " + hash);
		}
		this.hash = hash;
		this.path = path;
	}
	
	public CacheSourceDescriptor(String source) {
		String[] dummy = StringUtils.splitByWholeSeparator(source, " ", 2);
		if ( dummy.length != 2 ) {
			throw new IllegalArgumentException("Incorrect descriptor: " + source);
		}
		this.hash = dummy[0];
		this.path = dummy[1];
	}
	
	public String getPath() {
		return path;
	}
	
	public String getHashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return hash + " " + path;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1197, 1375).append(hash).append(path).hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CacheSourceDescriptor.class ) {
			return false;
		}
		CacheSourceDescriptor o = (CacheSourceDescriptor) other;
		return new EqualsBuilder()
				.append(o.hash, hash)
				.append(o.path, path)
				.isEquals();
	}

}
