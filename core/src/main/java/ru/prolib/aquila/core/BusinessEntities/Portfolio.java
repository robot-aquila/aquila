package ru.prolib.aquila.core.BusinessEntities;

import java.util.Set;

import ru.prolib.aquila.core.EventType;

/**
 * Interface of portfolio.
 * <p>
 * 2012-05-30<br>
 * $Id: Portfolio.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public interface Portfolio extends ObservableStateContainer, BusinessEntity {
	
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
	public CDecimal getBalance();
	
	/**
	 * Get account equity.
	 * <p>
	 * @return account equity or null if data not available
	 */
	public CDecimal getEquity();
	
	/**
	 * Get current profit of an account.
	 * <p>
	 * @return current profit or null if data not available
	 */
	public CDecimal getProfitAndLoss();
	
	/**
	 * Get the margin is currently used.
	 * <p>
	 * @return margin used or null if data not available
	 */
	public CDecimal getUsedMargin();
	
	/**
	 * Get the free margin.
	 * <p>
	 * @return free margin  or null if data not available
	 */
	public CDecimal getFreeMargin();
	
	/**
	 * Get the margin call level.
	 * <p>
	 * @return margin call level or null if data not available
	 */
	public CDecimal getMarginCallLevel();
	
	/**
	 * Get the margin stop out level.
	 * <p>
	 * @return stop out level or null if data not available
	 */
	public CDecimal getMarginStopOutLevel();
	
	/**
	 * Get the current assets of an account.
	 * <p>
	 * @return the current assets or null if data not available
	 */
	public CDecimal getAssets();
	
	/**
	 * Get the current liabilities of an account.
	 * <p>
	 * @return the current liabilities or null if data not available
	 */
	public CDecimal getLiabilities();
	
	/**
	 * Account leverage.
	 * <p>
	 * @return account leverage
	 */
	public CDecimal getLeverage();
	
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
	
	/**
	 * When position object is closed.
	 * <p>
	 * Note: That event type does not indicate when the position is closed (i.e.
	 * when position volume is zero). It indicates when the position instance
	 * is closed and cannot be used in the future (it will not get updates
	 * anymore and cannot be obtained via portfolio interface).
	 * <p>
	 * @return event type
	 */
	public EventType onPositionClose();
	
	public boolean isPositionExists(Symbol symbol);
	
}
