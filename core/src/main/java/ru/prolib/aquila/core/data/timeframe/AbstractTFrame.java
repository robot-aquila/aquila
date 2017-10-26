package ru.prolib.aquila.core.data.timeframe;

import java.time.temporal.ChronoUnit;

import ru.prolib.aquila.core.data.TFrame;

public abstract class AbstractTFrame implements TFrame {
	protected final int length;
	protected final ChronoUnit unit;
	
	public AbstractTFrame(int length, ChronoUnit unit) {
		this.length = length;
		this.unit = unit;
	}

	@Override
	public int getLength() {
		return length;
	}
	
	@Override
	public ChronoUnit getUnit() {
		return unit;
	}

}
