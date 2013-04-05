package ru.prolib.aquila.ib.subsys.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

import com.ib.client.Contract;

/**
 * Фабрика запросов.
 * <p>
 * 2012-11-19<br>
 * $Id: IBRequestFactoryImpl.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBRequestFactoryImpl implements IBRequestFactory {
	private final Counter reqId;
	private final EventSystem eSys;
	private final IBServiceLocator locator;
	
	public IBRequestFactoryImpl(EventSystem eSys, IBServiceLocator locator,
			Counter requestNumerator)
	{
		super();
		this.eSys = eSys;
		this.locator = locator;
		this.reqId = requestNumerator;
	}
	
	public IBRequestFactoryImpl(EventSystem eSys, IBServiceLocator locator) {
		this(eSys, locator, new SimpleCounter());
	}
	
	@Override
	public Counter getRequestNumerator() {
		return reqId;
	}
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return система событий
	 */
	public EventSystem getEventSystem() {
		return eSys;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return подключение
	 */
	public IBServiceLocator getServiceLocator() {
		return locator;
	}

	@Override
	public synchronized IBRequestContract requestContract(Contract contract) {
		EventDispatcher dispatcher = eSys.createEventDispatcher();
		return new IBRequestContractImpl(locator, dispatcher,
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				reqId.incrementAndGet(), contract);
	}

	@Override
	public IBRequestAccountUpdates requestAccountUpdates(String account) {
		EventDispatcher dispatcher = eSys.createEventDispatcher();
		return new IBRequestAccountUpdatesImpl(locator, dispatcher,
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				account);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121207, 231519)
			.append(eSys)
			.append(locator)
			.append(reqId)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == IBRequestFactoryImpl.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRequestFactoryImpl o = (IBRequestFactoryImpl) other;
		return new EqualsBuilder()
			.append(eSys, o.eSys)
			.append(locator, o.locator)
			.append(reqId, o.reqId)
			.isEquals();
	}

	@Override
	public synchronized
			IBRequestMarketData requestMarketData(Contract contract)
	{
		EventDispatcher dispatcher = eSys.createEventDispatcher();
		return new IBRequestMarketDataImpl(locator, dispatcher, 
				eSys.createGenericType(dispatcher),
				eSys.createGenericType(dispatcher),
				reqId.incrementAndGet(), contract);
	}

}
