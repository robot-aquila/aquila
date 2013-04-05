package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс портфеля.
 * <p>
 * 2012-05-30<br>
 * $Id: Portfolio.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public interface Portfolio extends Positions {
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal();
	
	/**
	 * Получить торговый счет портфеля.
	 * <p>
	 * @return идентификатор счета
	 */
	public Account getAccount();
	
	/**
	 * Получить величину вариационной маржи.
	 * <p>
	 * @return вариационная маржа или null, если значение недоступно
	 */
	public Double getVariationMargin();
	
	/**
	 * Получить величину доступных денежных средств.
	 * <p>
	 * @return размер кэша или null, если значение недоступно
	 */
	public Double getCash();
	
	/**
	 * Получить баланс портфеля.
	 * <p>
	 * @return баланс или null, если баланс недоступен
	 */
	public Double getBalance();
	
	/**
	 * Получить тип события: при изменении атрибутов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged();
	
}
