package ru.prolib.aquila.util;


public class SequenceLong implements Sequence<Long> {
	private long lastId = 0;
	
	public SequenceLong() {
		super();
	}

	@Override
	public Long next() {
		return ++lastId;
	}

}
