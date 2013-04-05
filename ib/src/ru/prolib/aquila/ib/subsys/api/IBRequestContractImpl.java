package ru.prolib.aquila.ib.subsys.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.Contract;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventContract;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.event.IBEventRequest;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

/**
 * Запрос деталей контракта.
 * <p>
 * 2012-11-19<br>
 * $Id: IBRequestContractImpl.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRequestContractImpl implements IBRequestContract, EventListener {
	private static final Logger logger;
	private final EventDispatcher dispatcher;
	private final EventType onError;
	private final EventType onResponse;
	private final IBServiceLocator locator;
	private final Contract contract;
	private final int reqId;
	
	static {
		logger = LoggerFactory.getLogger(IBRequestContractImpl.class);
	}
	
	/**
	 * Создать запрос.
	 * <p>
	 * @param locator сервис-локатор
	 * @param dispatcher диспетчер событий
	 * @param onError тип события: при ошибке
	 * @param onResponse тип события: при ответе
	 * @param reqId номер запроса
	 * @param contract дескриптор контракта
	 */
	public IBRequestContractImpl(IBServiceLocator locator,
			EventDispatcher dispatcher,
			EventType onError, EventType onResponse,
			int reqId, Contract contract)
	{
		super();
		this.locator = locator;
		this.dispatcher = dispatcher;
		this.onError = onError;
		this.onResponse = onResponse;
		this.reqId = reqId;
		this.contract = contract;
	}
	
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
	 * Получить номер запроса.
	 * <p>
	 * @return номер запроса
	 */
	public int getReqId() {
		return reqId;
	}
	
	/**
	 * Получить дескриптор контракта.
	 * <p>
	 * @return контракт
	 */
	public Contract getContract() {
		return contract;
	}
	
	@Override
	public void start() {
		IBClient client = locator.getApiClient();
		client.OnContractDetails().addListener(this);
		client.OnError().addListener(this);
		try {
			client.reqContractDetails(reqId, contract);
		} catch ( IBException e ) {
			if ( client.isConnected() ) {
				logger.error("API connected but request failed: ", e);
				locator.getTerminal().firePanicEvent(1,
						"IBRequestContractImpl#start");
			} else {
				logger.debug("Ignore failed request cuz API disconnected", e);
			}
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public EventType OnError() {
		return onError;
	}

	@Override
	public EventType OnResponse() {
		return onResponse;
	}

	@Override
	public void onEvent(Event event) {
		IBClient client = locator.getApiClient();
		if ( event instanceof IBEventRequest
		  && ((IBEventRequest) event).getReqId() == reqId )
		{
			if ( event.isType(client.OnError()) ) {
				IBEventError e = (IBEventError) event;
				dispatcher.dispatch(new IBEventError(onError, e));
			} else if ( event.isType(client.OnContractDetails()) ) {
				IBEventContract e = (IBEventContract) event;
				dispatcher.dispatch(new IBEventContract(onResponse, e));
			}
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121207, 222521)
			.append(locator)
			.append(dispatcher)
			.append(onError)
			.append(onResponse)
			.append(reqId)
			.append(contract)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other != null && other.getClass() == IBRequestContractImpl.class) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRequestContractImpl o = (IBRequestContractImpl) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(dispatcher, o.dispatcher)
			.append(onError, o.onError)
			.append(onResponse, o.onResponse)
			.append(reqId, o.reqId)
			.append(contract, o.contract)
			.isEquals();
	}

}
