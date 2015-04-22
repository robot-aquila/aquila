package ru.prolib.aquila.probe.storage;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

/**
 * The properties of trading session of one security.
 */
public interface TradingSessionProperties {
	
	/**
	 * Get descriptor of the security.
	 * <p>
	 * @return security descriptor
	 */
	public SecurityDescriptor getSecurityDescriptor();
	
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
	 * {@link ConstantSecurityProperties} instance.
	 */
	public BigDecimal getTickCost();
	
	/**
	 * Get initial margin per contract.
	 * <p>
	 * @return the initial margin per contract in the currency specified in
	 * appropriate {@link ConstantSecurityProperties} instance. 
	 * The result may be null if the security don't use an initial margin.    
	 */
	public Double getInitialMarginCost();
		
	public Double getInitialPrice();
	
	public Double getLowerPriceLimit();
	
	public Double getUpperPriceLimit();
	
	public Integer getLotSize();
	
	public BigDecimal getTickSize();
	
	public DateTime getSnapshotTime();
	
	public DateTime getClearingTime();

}
