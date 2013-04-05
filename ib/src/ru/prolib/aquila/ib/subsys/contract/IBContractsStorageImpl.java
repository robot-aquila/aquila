package ru.prolib.aquila.ib.subsys.contract;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventContract;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

/**
 * Хранилище контрактов.
 * <p>
 * 2013-01-04<br>
 * $Id: IBContractsStorageImpl.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBContractsStorageImpl
	implements IBContractsStorage, EventListener
{
	private final IBServiceLocator locator;
	private final EventDispatcher dispatcher;
	
	/**
	 * Карта идентификатора контракта на запрос. Используется
	 * для отправки запросов на получение деталей контракта (что бы не
	 * плодить запросы без особой необходимости).
	 */
	private final Map<Integer, IBRequestContract> conId2Req;
	
	/**
	 * Карта идентификатор контракта на тип события единовременного получения
	 * деталей контракта.
	 */
	private final Map<Integer, EventType> conId2onLoadedOnce;
	
	/**
	 * Карта идентификатор контракта на детали контракта. Наличие пары в этой
	 * карте свидетельствует о факте получения информации о контракте, то есть
	 * используется как индикатор загрузки контракта.
	 */
	private final Map<Integer, ContractDetails> conId2Contract;
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public IBServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator сервис-локатор
	 * @param dispatcher диспетчер событий
	 */
	public IBContractsStorageImpl(IBServiceLocator locator,
			EventDispatcher dispatcher)
	{
		super();
		this.locator = locator;
		this.dispatcher = dispatcher;
		conId2Req = new Hashtable<Integer, IBRequestContract>();
		conId2onLoadedOnce = new Hashtable<Integer, EventType>();
		conId2Contract = new Hashtable<Integer, ContractDetails>();
	}

	@Override
	public synchronized void start() {
		locator.getApiClient().OnContractDetails().addListener(this);
	}

	@Override
	public synchronized ContractDetails getContract(int conId)
			throws IBException
	{
		ContractDetails contract = conId2Contract.get(conId);
		if ( contract == null ) {
			throw new IBContractUnavailableException(conId);
		}
		return contract;
	}

	@Override
	public synchronized boolean isContractAvailable(int conId) {
		return conId2Contract.containsKey(conId);
	}

	@Override
	public synchronized void loadContract(int conId) {
		IBRequestContract req = conId2Req.get(conId); 
		if ( req == null ) {
			Contract contract = new Contract();
			contract.m_conId = conId;
			req = locator.getRequestFactory().requestContract(contract);
			conId2Req.put(conId, req);
		}
		req.start();
	}

	@Override
	public synchronized EventType OnContractLoadedOnce(int conId) {
		EventType type = conId2onLoadedOnce.get(conId);
		if ( type == null ) {
			type = locator.getEventSystem().createGenericType(dispatcher);
			conId2onLoadedOnce.put(conId, type);
		}
		return type;
	}

	@Override
	public synchronized void onEvent(Event event) {
		if ( event.isType(locator.getApiClient().OnContractDetails()) ) {
			switch ( ((IBEventContract) event).getSubType() ) {
			case IBEventContract.SUBTYPE_BOND:
			case IBEventContract.SUBTYPE_NORM:
				onContractEvent((IBEventContract) event);
				break;
			}
		}
	}
	
	/**
	 * Обработать событие с деталями контракта.
	 * <p>
	 * @param event событие
	 */
	private void onContractEvent(IBEventContract event) {
		ContractDetails contract = event.getContractDetails();
		int conId = contract.m_summary.m_conId;
		conId2Contract.put(conId, contract);
		EventType type = OnContractLoadedOnce(conId);
		dispatcher.dispatchForCurrentList(new IBEventContract(type, event));
		dispatcher.removeListeners(type);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBContractsStorageImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		if ( other == this ) {
			return true;
		}
		IBContractsStorageImpl o = (IBContractsStorageImpl) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(dispatcher, o.dispatcher)
			.append(conId2Req, o.conId2Req)
			.append(conId2onLoadedOnce, o.conId2onLoadedOnce)
			.append(conId2Contract, o.conId2Contract)
			.isEquals();
	}

}
