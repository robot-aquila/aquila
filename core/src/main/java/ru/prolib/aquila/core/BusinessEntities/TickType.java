package ru.prolib.aquila.core.BusinessEntities;

public class TickType {
	public static final TickType TICK = new TickType("Tick");
	public static final TickType ASK = new TickType("Ask");
	public static final TickType BID = new TickType("Bid");
	public static final TickType TRADE = new TickType("Trade");
	
	private final String code;
	
	private TickType(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}
