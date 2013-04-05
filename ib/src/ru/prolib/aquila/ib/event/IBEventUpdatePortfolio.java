package ru.prolib.aquila.ib.event;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.ib.client.Contract;

import ru.prolib.aquila.core.EventType;

/**
 * Событие: обновление портфеля.
 * <p>
 * http://www.interactivebrokers.com/en/software/api/apiguide/java/updateportfolio.htm
 * <p>
 * 2012-11-27<br>
 * $Id: IBEventUpdatePortfolio.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventUpdatePortfolio extends IBEvent {
	private final Contract contract;
	private final int position;
	private final double marketPrice;
	private final double marketValue;
	private final double averageCost;
	private final double unrealizedPNL;
	private final double realizedPNL;
	private final String accountName;

	/**
	 * Конструктор.
	 * <p>  
	 * @param type тип события
	 * @param contract 
	 * @param position
	 * @param marketPrice
	 * @param marketValue
	 * @param averageCost
	 * @param unrealizedPNL
	 * @param realizedPNL
	 * @param accountName
	 */
	public IBEventUpdatePortfolio(EventType type, Contract contract,
			int position, double marketPrice, double marketValue,
			double averageCost, double unrealizedPNL, double realizedPNL,
			String accountName)
	{
		super(type);
		this.contract = contract;
		this.position = position;
		this.marketPrice = marketPrice;
		this.marketValue = marketValue;
		this.averageCost = averageCost;
		this.unrealizedPNL = unrealizedPNL;
		this.realizedPNL = realizedPNL;
		this.accountName = accountName;
	}
	
	/**
	 * Конструктор на основании другого события.
	 * <p>
	 * @param type тип нового события
	 * @param e событие-основание
	 */
	public IBEventUpdatePortfolio(EventType type, IBEventUpdatePortfolio e) {
		this(type, e.contract, e.position, e.marketPrice, e.marketValue,
				e.averageCost, e.unrealizedPNL, e.realizedPNL, e.accountName);
	}
	
	/**
	 * Получить идентификатор контракта.
	 * <p>
	 * @return идентификатор контракта
	 */
	public int getContractId() {
		return contract.m_conId;
	}
	
	/**
	 * Получить дескриптор контракта.
	 * <p>
	 * @return контракт
	 */
	public Contract getContract() {
		return contract;
	}
	
	/**
	 * Получить размер позиции.
	 * <p>
	 * @return позиция
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Получить рыночную цену.
	 * <p>
	 * @return цена
	 */
	public double getMarketPrice() {
		return marketPrice;
	}
	
	/**
	 * Получить объем позиции.
	 * <p>
	 * @return объем
	 */
	public double getMarketValue() {
		return marketValue;
	}
	
	/**
	 * Получить среднюю цену.
	 * <p>
	 * @return средняя цена (execution price + commission) / qty
	 */
	public double getAverageCost() {
		return averageCost;
	}
	
	/**
	 * The difference between the current market value of your open positions
	 * and the average cost, or Value - Average Cost.
	 * <p>
	 * @return value
	 */
	public double getUnrealizedPNL() {
		return unrealizedPNL;
	}
	
	/**
	 * Shows your profit on closed positions, which is the difference between
	 * your entry execution cost (execution price + commissions to open the
	 * position) and exit execution cost ((execution price + commissions to
	 * close the position)
	 * <p>
	 * @return value
	 */
	public double getRealizedPNL() {
		return realizedPNL;
	}
	
	/**
	 * Получить код торгового счета.
	 * <p>
	 * @return код счета
	 */
	public String getAccount() {
		return accountName;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof IBEventUpdatePortfolio ) {
			IBEventUpdatePortfolio o = (IBEventUpdatePortfolio) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(accountName, o.accountName)
				.append(averageCost, o.averageCost)
				.append(contract, o.contract)
				.append(marketPrice, o.marketPrice)
				.append(marketValue, o.marketValue)
				.append(position, o.position)
				.append(realizedPNL, o.realizedPNL)
				.append(unrealizedPNL, o.unrealizedPNL)
				.isEquals();
		} else {
			return false;
		}
	}

}
