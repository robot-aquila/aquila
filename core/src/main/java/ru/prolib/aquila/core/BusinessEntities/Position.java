package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Interface of market position.
 * <p>
 * 2012-08-02<br>
 * $Id: Position.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public interface Position extends Container {
	
	/**
	 * Get account.
	 * <p>
	 * @return account code
	 */
	public Account getAccount();
	
	/**
	 * Get symbol.
	 * <p>
	 * @return symbol
	 */
	public Symbol getSymbol();
		
	/**
	 * Get terminal.
	 * <p>
	 * @return terminal
	 */
	public Terminal getTerminal();

	/**
	 * Get position volume.
	 * <p>
	 * @return position volume or null if data not available
	 */
	public Long getCurrentVolume();

	/**
	 * Get current price of position.
	 * <p>
	 * @return current price or null if data not available
	 */
	public Double getCurrentPrice();
	
	/**
	 * Get price of position at session start.
	 * <p>
	 * @return open price of position or null if data not available
	 */
	public Double getOpenPrice();
	
	/**
	 * Get volume of position at session start.
	 * <p>
	 * @return open volume or null if data not available
	 */
	public Long getOpenVolume();
	
	/**
	 * Get variation margin.
	 * <p>
	 * @return variation margin or null if data not available
	 */
	public Double getVariationMargin();
	
	/**
	 * When position volume changed.
	 * <p>
	 * This event type allow track only changes of position volume. 
	 * <p>
	 * @return event type
	 */
	public EventType onPositionChange();
	
	/**
	 * When opened position current price changed.
	 * <p>
	 * This event type allow track only changes of position current price.
	 * <p>
	 * @return event type
	 */
	public EventType onCurrentPriceChange();

}
