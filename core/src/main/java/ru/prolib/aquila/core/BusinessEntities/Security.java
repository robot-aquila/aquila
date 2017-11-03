package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Security interface.
 * <p>
 * 2012-06-11<br>
 * $Id: Security.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public interface Security extends ObservableStateContainer, L1StreamContainer, MDStreamContainer, BusinessEntity {
	
	/**
	 * Get terminal.
	 * <p>
	 * @return security owner terminal
	 */
	public Terminal getTerminal();
		
	/**
	 * Get symbol.
	 * <p>
	 * @return security symbol
	 */
	public Symbol getSymbol();
	
	/**
	 * Get lot size.
	 * <p>
	 * @return lot size
	 */
	public CDecimal getLotSize();

	/**
	 * Get lower price limit.
	 * <p>
	 * @return lower price limit
	 */
	public CDecimal getLowerPriceLimit();
	
	/**
	 * Get upper price limit.
	 * <p>
	 * @return upper price limit
	 */
	public CDecimal getUpperPriceLimit();
	
	/**
	 * Get tick price.
	 * <p>
	 * The price of security expressed in a specific units (for example in
	 * dollars, roubles, points, etc...) which may be different of units used in
	 * portfolios. This value is how much it costs in a currency specified in
	 * symbol and can be used to calculate position value in portfolios.
	 * <p>
	 * @return calculated tick price
	 */
	public CDecimal getTickValue();
	
	/**
	 * Get tick size.
	 * <p>
	 * @return minimal price change
	 */
	public CDecimal getTickSize();
	
	/**
	 * Get price scale.
	 * <p>
	 * @return number of digits after a decimal point in price values.
	 * Equivalent of scale obtained from {@link #getTickSize()} value.
	 */
	public Integer getScale();
		
	/**
	 * Get session attributes update event type.
	 * <p>
	 * @return event type
	 */
	public EventType onSessionUpdate();
	
	/**
	 * Получить наименование инструмента для отображения в интерфейсе.
	 * <p>
	 * @return наименование инструмента
	 */
	public String getDisplayName();
		
	/**
	 * Получить цену открытия сессии.
	 * <p>
	 * @return цена открытия
	 */
	public CDecimal getOpenPrice();
	
	/**
	 * Получить цену закрытия предыдущей сессии.
	 * <p>
	 * @return цена закрытия
	 */
	public CDecimal getClosePrice();
	
	/**
	 * Получить максимальную цену за сессию.
	 * <p>
	 * @return максимальная цена
	 */
	public CDecimal getHighPrice();
	
	/**
	 * Получить минимальную цену за сессию.
	 * <p>
	 * @return минимальная цена
	 */
	public CDecimal getLowPrice();
	
	/**
	 * Get settlement price.
	 * <p>
	 * @return settlement price
	 */
	public CDecimal getSettlementPrice();
	
	/**
	 * Get initial margin.
	 * <p>
	 * Initial margin means the amount in the margin currency required for
	 * opening a position with the volume of one lot. It is used for checking a
	 * client's assets when he or she enters the market.
	 * <p>
	 * @return initial margin
	 */
	public CDecimal getInitialMargin();
	
}
