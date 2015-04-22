package ru.prolib.aquila.probe.storage;

import java.util.Currency;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

/**
 * Constant properties which exists all of the security lifetime. 
 */
public interface ConstantSecurityProperties {

	/**
	 * Get descriptor of the security.
	 * <p>
	 * @return security descriptor
	 */
	public SecurityDescriptor getSecurityDescriptor();
	
	/**
	 * Get display name of the security.
	 * <p>
	 * @return display name
	 */
	public String getDisplayName();
	
	/**
	 * Get expiration time of the security.
	 * <p>
	 * @return expiration time or null if the security doesn't expire
	 */
	public DateTime getExpirationTime();

	/**
	 * Get the currency of cost values of {@link TradingSessionProperties}.
	 * <p>
	 * Some securities may have quotes in different units than the portfolio's
	 * currency. To combine securities with a different price units in one
	 * portfolio we should convert all values to one currency - the portfolio's
	 * currency. 
	 * <p>
	 * @return the currency
	 */
	public Currency getCurrencyOfCost();

}
