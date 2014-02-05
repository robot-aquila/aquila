package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import java.util.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Хранилище портфелей.
 */
public class Portfolios {
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
	public Portfolios(PortfoliosEventDispatcher dispatcher,
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
	public Portfolios(PortfoliosEventDispatcher dispatcher) {
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

	public boolean isPortfolioAvailable(Account account) {
		return map.containsKey(account);
	}

	public EventType OnPortfolioAvailable() {
		return dispatcher.OnPortfolioAvailable();
	}

	public List<Portfolio> getPortfolios() {
		return new LinkedList<Portfolio>(map.values());
	}

	public Portfolio getPortfolio(Account account) throws PortfolioException {
		Portfolio portfolio = map.get(account);
		if ( portfolio == null ) {
			throw new PortfolioNotExistsException(account);
		}
		return portfolio;
	}

	public Portfolio getDefaultPortfolio() throws PortfolioException {
		if ( defaultPortfolio == null ) {
			throw new PortfolioNotExistsException();
		}
		return defaultPortfolio;
	}

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

	public EditablePortfolio
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

	public void setDefaultPortfolio(EditablePortfolio portfolio) {
		defaultPortfolio = portfolio;
	}

	public EventType OnPortfolioChanged() {
		return dispatcher.OnPortfolioChanged();
	}

	public EventType OnPositionAvailable() {
		return dispatcher.OnPositionAvailable();
	}

	public EventType OnPositionChanged() {
		return dispatcher.OnPositionChanged();
	}

	public int getPortfoliosCount() {
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
	protected void setPortfolio(Account account, EditablePortfolio p) {
		map.put(account, p);
	}

}
