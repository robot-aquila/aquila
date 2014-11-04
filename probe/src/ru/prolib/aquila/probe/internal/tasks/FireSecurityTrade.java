package ru.prolib.aquila.probe.internal.tasks;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

public class FireSecurityTrade implements Runnable {
	private final EditableSecurity security;
	private final Tick tick;
	
	public FireSecurityTrade(EditableSecurity security, Tick tick) {
		super();
		this.security = security;
		this.tick = tick;
	}

	@Override
	public void run() {
		Trade t = new Trade();
		t.setDirection(Direction.BUY);
		t.setPrice(tick.getValue());
		t.setQty(tick.getVolume().longValue());
		t.setSecurityDescriptor(security.getDescriptor());
		t.setTerminal(security.getTerminal());
		t.setTime(tick.getTime());
		// TODO: calculate & set volume?
		security.fireTradeEvent(t);
		
		
		// TODO Auto-generated method stub

	}

}
