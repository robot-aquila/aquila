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
	 * Get position open price.
	 * <p>
	 * @return open price of position or null if data not available
	 */
	public Double getOpenPrice();
	
	/**
	 * Get margin.
	 * <p>
	 * @return margin or null if data not available
	 */
	public Double getUsedMargin();
	
	/**
	 * Get profit and loss.
	 * <p>
	 * @return profit and loss or null if data not available.
	 */
	public Double getProfitAndLoss();
	
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
