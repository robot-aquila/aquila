package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Хранилище портфелей.
 */
public class PortfoliosImpl implements EditablePortfolios {
	private final PortfoliosEventDispatcher dispatcher;
	private final Map<Account, EditablePortfolio> map;
	private Portfolio defaultPortfolio;
	private final PortfolioFactory factory;

	/**
	 * Конструктор набора.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param factory фабрика портфелей
	 */
	public PortfoliosImpl(PortfoliosEventDispatcher dispatcher,
			PortfolioFactory factory)
	{
		super();
		this.dispatcher = dispatcher;
		this.factory = factory;
		map = new LinkedHashMap<Account, EditablePortfolio>();
	}
	
	/**
	 * Конструктор набора.
	 * <p>
	 * @param dispatcher диспетчер событий
	 */
	public PortfoliosImpl(PortfoliosEventDispatcher dispatcher) {
		this(dispatcher, new PortfolioFactory());
	}
	
	public PortfolioFactory getFactory() {
		return factory;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер
	 */
	public PortfoliosEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public synchronized boolean isPortfolioAvailable(Account account) {
		return map.containsKey(account);
	}

	@Override
	public EventType OnPortfolioAvailable() {
		return dispatcher.OnPortfolioAvailable();
	}

	@Override
	public synchronized List<Portfolio> getPortfolios() {
		return new LinkedList<Portfolio>(map.values());
	}

	@Override
	public synchronized Portfolio getPortfolio(Account account)
			throws PortfolioException
	{
		Portfolio portfolio = map.get(account);
		if ( portfolio == null ) {
			throw new PortfolioNotExistsException(account);
		}
		return portfolio;
	}

	@Override
	public synchronized Portfolio getDefaultPortfolio()
			throws PortfolioException
	{
		if ( defaultPortfolio == null ) {
			throw new PortfolioNotExistsException();
		}
		return defaultPortfolio;
	}

	@Override
	public void fireEvents(EditablePortfolio portfolio) {
		synchronized ( portfolio ) {
			if ( portfolio.isAvailable() ) {
				portfolio.fireChangedEvent();
			} else {
				portfolio.setAvailable(true);
				dispatcher.fireAvailable(portfolio);
			}
			portfolio.resetChanges();
		}
	}

	@Override
	public synchronized EditablePortfolio
		getEditablePortfolio(EditableTerminal terminal, Account account)
	{
		EditablePortfolio portfolio = map.get(account);
		if ( portfolio == null ) {
			portfolio = factory.createInstance(terminal, account);
			map.put(account, portfolio);
			dispatcher.startRelayFor(portfolio);
			if ( defaultPortfolio == null ) {
				defaultPortfolio = portfolio;
			}
		}
		return portfolio;
	}

	@Override
	public synchronized void setDefaultPortfolio(EditablePortfolio portfolio) {
		defaultPortfolio = portfolio;
	}

	@Override
	public EventType OnPortfolioChanged() {
		return dispatcher.OnPortfolioChanged();
	}

	@Override
	public EventType OnPositionAvailable() {
		return dispatcher.OnPositionAvailable();
	}

	@Override
	public EventType OnPositionChanged() {
		return dispatcher.OnPositionChanged();
	}

	@Override
	public synchronized int getPortfoliosCount() {
		return map.size();
	}
	
	/**
	 * Установить экземпляр портфеля.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param account торговый счет
	 * @param p экземпляр портфеля
	 */
	protected synchronized void
		setPortfolio(Account account, EditablePortfolio p)
	{
		map.put(account, p);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfoliosImpl.class ) {
			return false;
		}
		PortfoliosImpl o = (PortfoliosImpl) other;
		return new EqualsBuilder()
			.appendSuper(o.defaultPortfolio == defaultPortfolio)
			.append(o.map, map)
			.isEquals();
	}

}
