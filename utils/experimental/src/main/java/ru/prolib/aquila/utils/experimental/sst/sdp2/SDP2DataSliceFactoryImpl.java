package ru.prolib.aquila.utils.experimental.sst.sdp2;

import ru.prolib.aquila.core.EventQueue;

public class SDP2DataSliceFactoryImpl<T extends SDP2Key> implements SDP2DataSliceFactory<T> {
	private final EventQueue queue;
	
	public SDP2DataSliceFactoryImpl(EventQueue queue) {
		this.queue = queue;
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}

	@Override
	public SDP2DataSlice<T> produce(T key) {
		return new SDP2DataSliceImpl<>(key, queue);
	}

}
