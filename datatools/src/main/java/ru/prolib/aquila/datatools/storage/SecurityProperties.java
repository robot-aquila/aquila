package ru.prolib.aquila.datatools.storage;

import java.util.Currency;

import org.joda.time.*;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

/**
 * Constant properties which exists all of the security lifetime. 
 */
public interface SecurityProperties {

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
	 * @return expiration time or null if the security doesn't expire or
	 * expiration time is unknown.
	 */
	public DateTime getExpirationTime();
	
	/**
	 * Get starting time of the security.
	 * <p>
	 * @return
	 */
	public DateTime getStartingTime();

	/**
	 * Get the currency of cost values of {@link SecuritySessionProperties}.
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
