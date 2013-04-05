package ru.prolib.aquila.ta.ds.quik;

import java.util.Date;

public interface DealDispatcher {
	
	public void dispatch(long number, Date dealTime,
			String asset, double price, long qty);
	
	public void flushAll();
	
}