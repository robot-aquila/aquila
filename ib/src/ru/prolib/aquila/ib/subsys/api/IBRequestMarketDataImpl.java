package ru.prolib.aquila.ib.subsys.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.Contract;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.event.IBEventTick;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

/**
 * Запрос тиковых данных.
 * <p>
 * 2012-12-23<br>
 * $Id: IBRequestMarketDataImpl.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRequestMarketDataImpl
	implements IBRequestMarketData, EventListener
{
	private static final Logger logger;
	private final IBServiceLocator locator;
	private final EventDispatcher dispatcher;
	private final EventType onError;
	private final EventType onTick;
	private final int reqId;
	private final Contract contract;
	
	static {
		logger = LoggerFactory.getLogger(IBRequestMarketDataImpl.class);
	}

	/**
	 * Конструктор.
	 * <p>
	 * @param locator сервис-локатор
	 * @param dispatcher диспетчер событий
	 * @param onError тип события при ошибке 
	 * @param onTick тип события при поступлении тикоых данных
	 * @param reqId номер запроса
	 * @param contract контракт, по которому нужно получать данные
	 */
	public IBRequestMarketDataImpl(IBServiceLocator locator,
			EventDispatcher dispatcher,
			EventType onError, EventType onTick,
			int reqId, Contract contract)
	{
		super();
		this.locator = locator;
		this.dispatcher = dispatcher;
		this.onError = onError;
		this.onTick = onTick;
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
	 * Получить контракт, по которому необходимо получать данные.
	 * <p>
	 * @return контракт
	 */
	public Contract getContract() {
		return contract;
	}
	
	@Override
	public void start() {
		IBClient client = locator.getApiClient();
		client.OnError().addListener(this);
		client.OnTick().addListener(this);
		try {
			client.reqMktData(reqId, contract, null, false);
		} catch ( IBException e ) {
			if ( locator.getApiClient().isConnected() ) {
				logger.error("API connected but request failed: ", e);
				locator.getTerminal().firePanicEvent(1,
						"IBRequestMarketDataImpl#start");
			} else {
				logger.debug("Ignore exception cuz API not connected: ", e);
			}
		}
	}

	@Override
	public void stop() {
		locator.getApiClient().cancelMktData(reqId);
	}

	@Override
	public EventType OnError() {
		return onError;
	}

	@Override
	public EventType OnTick() {
		return onTick;
	}

	@Override
	public void onEvent(Event event) {
		IBClient client = locator.getApiClient();
		if ( event.isType(client.OnError())
				&& ((IBEventError) event).getReqId() == reqId )
		{
			dispatcher.dispatch(new IBEventError(onError, (IBEventError)event));
		} else if ( event.isType(client.OnTick())
				&& ((IBEventTick) event).getReqId() == reqId )
		{
			dispatcher.dispatch(new IBEventTick(onTick, (IBEventTick) event));
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBRequestMarketDataImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRequestMarketDataImpl o = (IBRequestMarketDataImpl) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(dispatcher, o.dispatcher)
			.append(onError, o.onError)
			.append(onTick, o.onTick)
			.append(reqId, o.reqId)
			.append(contract, o.contract)
			.isEquals();
	}

}
