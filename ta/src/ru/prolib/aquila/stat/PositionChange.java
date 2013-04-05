package ru.prolib.aquila.stat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.text.StrBuilder;

/**
 * 2012-02-02
 * $Id: PositionChange.java 196 2012-02-02 20:24:38Z whirlwind $
 * 
 * Информации об изменении позиции.
 */
public class PositionChange {
	private final int bar;
	private final int qty;
	private final double price;
	private final String comment;
	
	public PositionChange(int bar, int qty, double price, String comment) {
		super();
		this.bar = bar;
		this.qty = qty;
		this.price = price;
		this.comment = comment;
	}
	
	public PositionChange(int bar, int qty, double price) {
		this(bar, qty, price, null);
	}
	
	/**
	 * Получить индекс бара, на котором зафиксировано изменение позиции.
	 * 
	 * @return
	 */
	public int getBar() {
		return bar;
	}
	
	/**
	 * Получить величину изменения позиции.
	 *  
	 * @return
	 */
	public int getQty() {
		return qty;
	}
	
	/**
	 * Получить цену изменения позиции.
	 * 
	 * @return
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Получить комментарий к изменению позиции.
	 * 
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == null ) {
			return false;
		}
		if ( o == this ) {
			return true;
		}
		if ( o.getClass() != getClass() ) {
			return false;
		}
		PositionChange other = (PositionChange)o;
		return new EqualsBuilder()
			.append(bar, other.bar)
			.append(qty, other.qty)
			.append(price, other.price)
			.append(comment, other.comment)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return new StrBuilder()
			.append("B:").append(bar).append(",")
			.append("Q:").append(qty).append(",")
			.append("P:").append(price).append(",")
			.append("C:").append(comment)
			.toString(); 
	}

}
