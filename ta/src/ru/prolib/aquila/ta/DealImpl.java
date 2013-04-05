package ru.prolib.aquila.ta;

import java.util.Date;


public class DealImpl implements Deal {
	private final double price;
	private final long qty;
	private final long time;
	
	public DealImpl(Date time, double price, long qty) {
		this.time = time.getTime();
		this.price = price;
		this.qty = qty;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public long getQuantity() {
		return qty;
	}

	@Override
	public Date getTime() {
		return new Date(time);
	}
	
	public boolean equals(Object obj) {
		if ( ! (obj instanceof DealImpl) ) {
			return false;
		}
		DealImpl deal = (DealImpl)obj; 
		return price == deal.price
			&& time == deal.time
			&& qty == deal.qty;
	}

}
