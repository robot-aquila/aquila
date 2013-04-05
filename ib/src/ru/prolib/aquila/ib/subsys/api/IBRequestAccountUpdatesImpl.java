package ru.prolib.aquila.ib.subsys.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

/**
 * Подписка на обновление счета.
 * <p>
 * 2012-11-28<br>
 * $Id: IBRequestAccountUpdatesImpl.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRequestAccountUpdatesImpl
	implements IBRequestAccountUpdates, EventListener
{
	private static final Logger logger;
	private final IBServiceLocator locator;
	private final EventDispatcher dispatcher;
	private final EventType onUpdateAccount;
	private final EventType onUpdatePortfolio;
	private final String account;
	
	static {
		logger = LoggerFactory.getLogger(IBRequestAccountUpdatesImpl.class);
	}
	
	/**
	 * Конструктор с фильтром по счету.
	 * <p>
	 * @param locator сервис-локатор
	 * @param dispatcher диспетчер событий
	 * @param onUpdateAccount событие обновление параметров счета
	 * @param onUpdatePortfolio событие обновление портфеля
	 * @param account код торгового счета
	 */
	public IBRequestAccountUpdatesImpl(IBServiceLocator locator,
			EventDispatcher dispatcher, EventType onUpdateAccount,
			EventType onUpdatePortfolio,
			String account)
	{
		super();
		if ( account == null ) {
			throw new NullPointerException("Account code cannot be null");
		}
		this.dispatcher = dispatcher;
		this.onUpdateAccount = onUpdateAccount;
		this.onUpdatePortfolio = onUpdatePortfolio;
		this.locator = locator;
		this.account = account;
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
	 * Получить код счета.
	 * <p>
	 * @return код счета
	 */
	public String getAccount() {
		return account;
	}

	@Override
	public EventType OnUpdateAccount() {
		return onUpdateAccount;
	}

	@Override
	public EventType OnUpdatePortfolio() {
		return onUpdatePortfolio;
	}

	@Override
	public void start() {
		IBClient client = locator.getApiClient();
		client.OnUpdateAccount().addListener(this);
		client.OnUpdatePortfolio().addListener(this);
		try {
			client.reqAccountUpdates(true, account);
		} catch ( IBException e ) {
			if ( client.isConnected() ) {
				logger.error("API connected but request failed: ", e);
				locator.getTerminal().firePanicEvent(1,
						"IBRequestAccountUpdatesImpl#start");
			} else {
				logger.debug("Ignore failed request cuz API disconnected", e);
			}
		}
	}

	@Override
	public void stop() {
		try {
			locator.getApiClient().reqAccountUpdates(false, account);
		} catch ( IBException e ) {
			logger.debug("Ignore failed request: ", e);
		}
	}

	@Override
	public void onEvent(Event event) {
		IBClient client = locator.getApiClient();
		if ( event.isType(client.OnUpdateAccount()) ) {
			IBEventUpdateAccount e = (IBEventUpdateAccount) event;
			if ( e.getAccount().equals(account) ) {
				dispatcher
					.dispatch(new IBEventUpdateAccount(onUpdateAccount, e));
			}
		} else if ( event.isType(client.OnUpdatePortfolio()) ) {
			IBEventUpdatePortfolio e = (IBEventUpdatePortfolio) event;
			if ( e.getAccount().equals(account) ) {
				dispatcher
					.dispatch(new IBEventUpdatePortfolio(onUpdatePortfolio, e));
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null
		  && other.getClass() == IBRequestAccountUpdatesImpl.class )
		{
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRequestAccountUpdatesImpl o = (IBRequestAccountUpdatesImpl) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(account, o.account)
			.append(dispatcher, o.dispatcher)
			.append(onUpdateAccount, o.onUpdateAccount)
			.append(onUpdatePortfolio, o.onUpdatePortfolio)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121207, 230947)
			.append(locator)
			.append(dispatcher)
			.append(onUpdateAccount)
			.append(onUpdatePortfolio)
			.append(account)
			.toHashCode();
	}

}
