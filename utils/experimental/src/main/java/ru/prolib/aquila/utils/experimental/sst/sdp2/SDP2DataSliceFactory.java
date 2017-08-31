package ru.prolib.aquila.utils.experimental.sst.sdp2;

public interface SDP2DataSliceFactory<T extends SDP2Key> {

	SDP2DataSlice<T> produce(T key);
	
}
