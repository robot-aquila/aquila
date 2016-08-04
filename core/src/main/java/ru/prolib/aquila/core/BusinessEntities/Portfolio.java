package ru.prolib.aquila.core.BusinessEntities;

import java.util.Set;

import ru.prolib.aquila.core.EventType;

/**
 * Interface of portfolio.
 * <p>
 * 2012-05-30<br>
 * $Id: Portfolio.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public interface Portfolio extends ObservableStateContainer {
	
	/**
	 * Get terminal instance.
	 * <p>
	 * @return terminal
	 */
	public Terminal getTerminal();
	
	/**
	 * Get account code.
	 * <p>
	 * @return account code
	 */
	public Account getAccount();
	
	/**
	 * Get account balance.
	 * <p>
	 * @return account balance or null if data not available
	 */
	public Double getBalance();
	
	/**
	 * Get account equity.
	 * <p>
	 * @return account equity or null if data not available
	 */
	public Double getEquity();
	
	/**
	 * Get current profit of an account.
	 * <p>
	 * @return current profit or null if data not available
	 */
	public Double getProfitAndLoss();
	
	/**
	 * Get the margin is currently used.
	 * <p>
	 * @return margin used or null if data not available
	 */
	public Double getUsedMargin();
	
	/**
	 * Get the free margin.
	 * <p>
	 * @return free margin  or null if data not available
	 */
	public Double getFreeMargin();
	
	/**
	 * Get the margin call level.
	 * <p>
	 * @return margin call level or null if data not available
	 */
	public Double getMarginCallLevel();
	
	/**
	 * Get the margin stop out level.
	 * <p>
	 * @return stop out level or null if data not available
	 */
	public Double getMarginStopOutLevel();
	
	/**
	 * Get the current assets of an account.
	 * <p>
	 * @return the current assets or null if data not available
	 */
	public Double getAssets();
	
	/**
	 * Get the current liabilities of an account.
	 * <p>
	 * @return the current liabilities or null if data not available
	 */
	public Double getLiabilities();
	
	/**
	 * Account leverage.
	 * <p>
	 * @return account leverage
	 */
	public Double getLeverage();
	
	/**
	 * Get account currency.
	 * <p>
	 * @return ISO 4217 currency code
	 */
	public String getCurrency();
	
	/**
	 * Get position count.
	 * <p>
	 * @return count of positions
	 */
	public int getPositionCount();
	
	/**
	 * Get existing positions.
	 * <p>
	 * @return set of positions. Position order is not a constant and may differ
	 * for two consecutive calls.
	 */
	public Set<Position> getPositions();
	
	/**
	 * Get position.
	 * <p>
	 * @param symbol - the symbol of position
	 * @return position instance
	 */
	public Position getPosition(Symbol symbol);
	
	/**
	 * When new position got minimum required data to be useful.
	 * <p>
	 * @return event type
	 */
	public EventType onPositionAvailable();
	
	/**
	 * When opened position current price changed.
	 * <p>
	 * @return event type
	 */
	public EventType onPositionCurrentPriceChange();
	
	/**
	 * When position volume changed.
	 * <p>
	 * @return event type
	 */
	public EventType onPositionChange();

	/**
	 * When one or more attributes of position were changed.
	 * <p>
	 * @return event type
	 */
	public EventType onPositionUpdate();
	
}
