package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfolioFactory;

/**
 * Хранилище портфелей.
 */
public class PortfoliosImpl implements EditablePortfolios, EventListener {
	private final EventDispatcher dispatcher;
	private final EventType onAvailable,onChanged,onPosAvailable,onPosChanged;
	private final Map<Account, EditablePortfolio> map;
	private Portfolio defaultPortfolio;
	private final PortfolioFactory factory;

	/**
	 * Конструктор набора.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param onAvailable тип события при появлении нового портфеля
	 * @param onChanged тип события при изменении портфеля
	 * @param onPosAvailable тип события при появлении новой позиции
	 * @param onPosChanged тип события при изменении позиции
	 * @param factory фабрика портфелей
	 */
	public PortfoliosImpl(EventDispatcher dispatcher, EventType onAvailable,
			EventType onChanged, EventType onPosAvailable,
			EventType onPosChanged, PortfolioFactory factory)
	{
		super();
		if ( dispatcher == null ) {
			throw new NullPointerException("Dispatcher cannot be null");
		}
		if ( onAvailable == null || onChanged == null
				|| onPosAvailable == null || onPosChanged == null )
		{
			throw new NullPointerException("Event type cannot be null");
		}
		this.dispatcher = dispatcher;
		this.onAvailable = onAvailable;
		this.onChanged = onChanged;
		this.onPosAvailable = onPosAvailable;
		this.onPosChanged = onPosChanged;
		map = new LinkedHashMap<Account, EditablePortfolio>();
		this.factory = factory;
	}
	
	/**
	 * Конструктор набора.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param onAvailable тип события при появлении нового портфеля
	 * @param onChanged тип события при изменении портфеля
	 * @param onPosAvailable тип события при появлении новой позиции
	 * @param onPosChanged тип события при изменении позиции
	 */
	public PortfoliosImpl(EventDispatcher dispatcher, EventType onAvailable,
		EventType onChanged, EventType onPosAvailable, EventType onPosChanged)
	{
		this(dispatcher, onAvailable, onChanged, onPosAvailable, onPosChanged,
				new PortfolioFactory());
	}
	
	public PortfolioFactory getFactory() {
		return factory;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public synchronized boolean isPortfolioAvailable(Account account) {
		return map.containsKey(account);
	}

	@Override
	public EventType OnPortfolioAvailable() {
		return onAvailable;
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
				dispatcher.dispatch(new PortfolioEvent(onAvailable, portfolio));
			}
			portfolio.resetChanges();
		}
	}

	@Override
	public synchronized EditablePortfolio
		getEditablePortfolio(EditableTerminal terminal, Account account)
	{
		EditablePortfolio port = map.get(account);
		if ( port == null ) {
			port = factory.createInstance(terminal, account);
			map.put(account, port);
			port.OnChanged().addListener(this);
			port.OnPositionAvailable().addListener(this);
			port.OnPositionChanged().addListener(this);
			if ( defaultPortfolio == null ) {
				defaultPortfolio = port;
			}
		}
		return port;
	}

	@Override
	public synchronized void setDefaultPortfolio(EditablePortfolio portfolio) {
		defaultPortfolio = portfolio;
	}

	@Override
	public EventType OnPortfolioChanged() {
		return onChanged;
	}

	@Override
	public EventType OnPositionAvailable() {
		return onPosAvailable;
	}

	@Override
	public EventType OnPositionChanged() {
		return onPosChanged;
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof PortfolioEvent ) {
			Portfolio port = ((PortfolioEvent) event).getPortfolio();
			EventType map[][] = {
					{ port.OnChanged(), onChanged },
			};
			for ( int i = 0; i < map.length; i ++ ) {
				if ( event.isType(map[i][0]) ) {
					dispatcher.dispatch(new PortfolioEvent(map[i][1], port));
					break;
				}
			}
		} else if ( event instanceof PositionEvent ) {
			translatePositionEvent((PositionEvent) event);
		}
	}
	
	/**
	 * Перенаправить событие позициии
	 * <p>
	 * @param event исходное событие
	 */
	private void translatePositionEvent(PositionEvent event) {
		Position pos = ((PositionEvent) event).getPosition();
		Portfolio port = pos.getPortfolio();
		EventType map[][] = {
				{ port.OnPositionAvailable(), onPosAvailable },
				{ port.OnPositionChanged(), onPosChanged },
		};
		for ( int i = 0; i < map.length; i ++ ) {
			if ( event.isType(map[i][0]) ) {
				dispatcher.dispatch(new PositionEvent(map[i][1], pos));
				break;
			}
		}
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
			.append(o.defaultPortfolio, defaultPortfolio)
			.append(o.dispatcher, dispatcher)
			.append(o.map, map)
			.append(o.onAvailable, onAvailable)
			.append(o.onChanged, onChanged)
			.append(o.onPosAvailable, onPosAvailable)
			.append(o.onPosChanged, onPosChanged)
			.append(o.factory, factory)
			.isEquals();
	}

}
