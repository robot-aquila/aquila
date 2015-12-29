package ru.prolib.aquila.datatools.storage;

import java.time.LocalDateTime;
import java.util.Currency;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Constant properties which exists all of the security lifetime. 
 */
public interface SecurityProperties {

	/**
	 * Get symbol of the security.
	 * <p>
	 * @return symbol info
	 */
	public Symbol getSymbolInfo();
	
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
	public LocalDateTime getExpirationTime();
	
	/**
	 * Get starting time of the security.
	 * <p>
	 * @return
	 */
	public LocalDateTime getStartingTime();

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
