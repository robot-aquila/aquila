package ru.prolib.aquila.datatools.storage;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * The trading session properties of a security.
 */
public interface SecuritySessionProperties {
	
	/**
	 * Get symbol info of the security.
	 * <p>
	 * @return symbol info
	 */
	public Symbol getSymbolInfo();
	
	/**
	 * Get scale of the price values.
	 * <p>
	 * @return scale
	 */
	public Integer getScale();

	/**
	 * Get cost of one price tick.
	 * <p>
	 * @return cost of the one tick in the currency specified in appropriate
	 * {@link SecurityProperties} instance.
	 */
	public Double getTickCost();
	
	/**
	 * Get initial margin per contract.
	 * <p>
	 * @return the initial margin per contract in the currency specified in
	 * appropriate {@link SecurityProperties} instance. 
	 * The result may be null if the security don't use an initial margin.    
	 */
	public Double getInitialMarginCost();
	
	/**
	 * Get initial price.
	 * <p>
	 * Initial price is some basic price for the session.
	 * <p>
	 * @return initial price
	 */
	public Double getInitialPrice();
	
	/**
	 * Get lower price limit.
	 * <p>
	 * @return the lower price which can be accepted starting this snapshot. 
	 */
	public Double getLowerPriceLimit();
	
	/**
	 * Get upper price limit.
	 * <p>
	 * @return the upper price which can be accepted starting this snapshot.
	 */
	public Double getUpperPriceLimit();
	
	/**
	 * Get lot size.
	 * <p>
	 * @return the lot size.
	 */
	public Integer getLotSize();
	
	/**
	 * Get size of tick.
	 * <p>
	 * @return the tick size in units of price.
	 */
	public Double getTickSize();
	
	/**
	 * Get snapshot time.
	 * <p>
	 * @return the starting time of the property values.
	 */
	public LocalDateTime getSnapshotTime();
	
	/**
	 * Get clearing time.
	 * <p>
	 * @return the time of the next clearing.
	 */
	public LocalDateTime getClearingTime();

}
