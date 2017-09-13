package ru.prolib.aquila.data.storage.segstor.file.ohlcv;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class CacheHeaderImpl implements CacheHeader {
	private final List<CacheSourceDescriptor> descriptors;
	private long numberOfElements;
	
	CacheHeaderImpl(List<CacheSourceDescriptor> descriptors) {
		this.descriptors = descriptors;
	}
	
	public CacheHeaderImpl() {
		this(new ArrayList<>());
	}

	@Override
	public long getNumberOfSourceDescriptors() {
		return descriptors.size();
	}

	@Override
	public long getNumberOfElements() {
		return numberOfElements;
	}

	@Override
	public List<CacheSourceDescriptor> getSourceDescriptors() {
		return descriptors;
	}
	
	@Override
	public CacheSourceDescriptor getSourceDescriptor(int index) {
		return descriptors.get(index);
	}
	
	public CacheHeaderImpl addSourceDescriptor(String line) {
		addSourceDescriptor(new CacheSourceDescriptor(line));
		return this;
	}
	
	public CacheHeaderImpl addSourceDescriptor(String hash, String path) {
		addSourceDescriptor(new CacheSourceDescriptor(hash, path));
		return this;		
	}
	
	public CacheHeaderImpl addSourceDescriptor(CacheSourceDescriptor descr) {
		descriptors.add(descr);
		return this;
	}
	
	public CacheHeaderImpl setNumberOfElements(long number) {
		this.numberOfElements = number;
		return this;
	}
	
	@Override
	public String toString() {
		return getNumberOfSourceDescriptors() + " " + getNumberOfElements() + " " + descriptors;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CacheHeaderImpl.class ) {
			return false;
		}
		CacheHeaderImpl o = (CacheHeaderImpl) other;
		return new EqualsBuilder()
				.append(o.numberOfElements, numberOfElements)
				.append(o.descriptors, descriptors)
				.isEquals();
	}

}
