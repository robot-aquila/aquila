package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import java.util.List;

public interface CacheHeader {
	
	long getNumberOfSourceDescriptors();
	long getNumberOfElements();
	List<CacheSourceDescriptor> getSourceDescriptors();
	CacheSourceDescriptor getSourceDescriptor(int index);

}
