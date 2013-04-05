package ru.prolib.aquila.ib.getter;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Интерфейс фабрики геттеров.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetterFactory.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBGetterFactory {
	
	/**
	 * Создать геттер счета из события {@link IBEventOpenOrder}.
	 * <p>
	 * @return геттер
	 */
	public G<Account> openOrderAccount();
	
	/**
	 * Создать геттер дескриптора инструмента из события {@link IBEventOpenOrder}.
	 * <p>
	 * @return геттер
	 */
	public G<SecurityDescriptor> openOrderSecDescr();
	
	/**
	 * Создать геттер типа заявки из события {@link IBEventOpenOrder}.
	 * <p>
	 * @return геттер
	 */
	public G<OrderType> openOrderType();
	
	/**
	 * Создать геттер направления заявки из события {@link IBEventOpenOrder}.
	 * <p>
	 * @return геттер
	 */
	public G<OrderDirection> openOrderDir();
	
	/**
	 * Создать геттер кол-ва заявки из события {@link IBEventOpenOrder}.
	 * <p>
	 * @return геттер
	 */
	public G<Long> openOrderQty();
	
	/**
	 * Создать геттер состояния заявки из события {@link IBEventOpenOrder}.
	 * <p> 
	 * @return геттер
	 */
	public G<OrderStatus> openOrderStatus();
	
	/**
	 * Создать геттер статуса заявки из события {@link IBEventOrderStatus}.
	 * <p>
	 * @return геттер
	 */
	public G<OrderStatus> orderStatusStatus();
	
	/**
	 * Создать геттер неисполненного кол-ва заявки из события
	 * {@link IBEventOrderStatus}.
	 * <p>
	 * @return геттер
	 */
	public G<Long> orderStatusRemaining();
	
	/**
	 * Создать геттер исполненного объема заявки из события
	 * {@link IBEventOrderStatus}.
	 * <p> 
	 * @return геттер
	 */
	public G<Double> orderStatusExecutedVolume();
	
	/**
	 * Создать геттер доступных средств портфеля из события
	 * {@link IBEventUpdateAccount}.
	 * <p>
	 * @return геттер
	 */
	public G<Double> portCash();
	
	/**
	 * Создать геттер баланса портфеля из события
	 * {@link IBEventUpdateAccount}.
	 * <p>
	 * @return геттер
	 */
	public G<Double> portBalance();

	/**
	 * Создать геттер позиции из события {@link IBEventUpdatePortfolio}.
	 * <p>
	 * @return геттер
	 */
	public G<Long> posCurrValue();
	
	/**
	 * Создать геттер рыночной стоимости позиции из события
	 * {@link IBEventUpdatePortfolio}.
	 * <p>
	 * @return геттер
	 */
	public G<Double> posMarketValue();
	
	/**
	 * Создать геттер балансовой стоимости позиции из события
	 * {@link IBEventUpdatePortfolio}.
	 * <p>
	 * @return геттер
	 */
	public G<Double> posBalanceCost();
	
	/**
	 * Создать геттер PL позиции из события {@link IBEventUpdatePortfolio}.
	 * <p>
	 * @return геттер
	 */
	public G<Double> posPL();
	
}
