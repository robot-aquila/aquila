package ru.prolib.aquila.core.data.timeframe;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import ru.prolib.aquila.core.data.ZTFrame;

public abstract class AbstractZTFrame implements ZTFrame {
	protected final int length;
	protected final ChronoUnit unit;
	protected final ZoneId zoneID;
	
	public AbstractZTFrame(int length, ChronoUnit unit, ZoneId zoneID) {
		this.length = length;
		this.unit = unit;
		this.zoneID = zoneID;
	}
	
	@Override
	public int getLength() {
		return length;
	}

	@Override
	public ZoneId getZoneID() {
		return zoneID;
	}
	
	@Override
	public ChronoUnit getUnit() {
		return unit;
	}

	@Override
	public boolean isCompatibleWith(ZTFrame tframe) {
		return zoneID.equals(tframe.getZoneID());
	}

}
