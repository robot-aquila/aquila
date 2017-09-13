package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import java.util.List;

public interface CacheHeader {
	
	long getNumberOfSourceDescriptors();
	long getNumberOfElements();
	List<CacheSourceDescriptor> getSourceDescriptors();
	CacheSourceDescriptor getSourceDescriptor(int index);

}
