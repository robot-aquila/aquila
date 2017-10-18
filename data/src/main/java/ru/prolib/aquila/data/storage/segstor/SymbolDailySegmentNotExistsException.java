package ru.prolib.aquila.data.storage.segstor;

import ru.prolib.aquila.data.storage.DataStorageException;

public class SymbolDailySegmentNotExistsException extends DataStorageException {
	private static final long serialVersionUID = 1L;
	private final SymbolDaily segment;
	
	public SymbolDailySegmentNotExistsException(SymbolDaily segment, Throwable t) {
		super("Segment not exists: " + segment.toString(), t);
		this.segment = segment;
	}
	
	public SymbolDailySegmentNotExistsException(SymbolDaily segment) {
		this(segment, null);
	}
	
	public SymbolSegment getSegment() {
		return segment;
	}

}
