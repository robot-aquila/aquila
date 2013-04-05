package ru.prolib.aquila.ta;

public class Signal {
	public static final int BUY  =  1;
	public static final int SELL = -1;
	
	private final int sourceId;
	private final int type;
	private final double price;
	private final String comment;
	
	public Signal(int sourceId, int type, double price, String comment) {
		this.sourceId = sourceId;
		this.type = type;
		this.price = price;
		this.comment = comment;
	}
	
	public int getSourceId() {
		return sourceId;
	}
	
	public int getType() {
		return type;
	}

	public double getPrice() {
		return price;
	}
	
	public String getComment() {
		return comment; 
	}
	
}
