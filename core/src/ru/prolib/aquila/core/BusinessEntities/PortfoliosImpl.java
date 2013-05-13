package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;

/**
 * Реализация набора портфелей.
 * <p>
 * 2012-08-04<br>
 * $Id: PortfoliosImpl.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class PortfoliosImpl implements EditablePortfolios, EventListener {
	private final EventDispatcher dispatcher;
	private final EventType onAvailable,onChanged,onPosAvailable,onPosChanged;
	private final Map<Account, EditablePortfolio> map;
	private Portfolio defaultPortfolio;

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
			EventType onChanged, EventType onPosAvailable,
			EventType onPosChanged)
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
		return getEditablePortfolio(account);
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
	public void firePortfolioAvailableEvent(Portfolio portfolio) {
		dispatcher.dispatch(new PortfolioEvent(onAvailable, portfolio));
	}

	@Override
	public synchronized EditablePortfolio getEditablePortfolio(Account acc)
			throws PortfolioException
	{
		EditablePortfolio port = map.get(acc);
		if ( port == null ) {
			throw new PortfolioNotExistsException(acc);
		}
		return port;
	}

	@Override
	public synchronized EditablePortfolio
		createPortfolio(EditableTerminal terminal, Account account)
			throws PortfolioException
	{
		if ( map.containsKey(account) ) {
			throw new PortfolioAlreadyExistsException(account);
		}
		EventSystem es = terminal.getEventSystem();
		EventDispatcher d = es.createEventDispatcher("Portfolio["+account+"]");
		PortfolioImpl p = new PortfolioImpl(terminal, account, d,
				d.createType("OnChanged"));
		EventDispatcher pd = es.createEventDispatcher("Portfolio["+account+"]");
		p.setPositionsInstance(new PositionsImpl(p, pd,
				pd.createType("OnPosAvailable"),
				pd.createType("OnPosChanged")));
		map.put(account, p);
		p.OnChanged().addListener(this);
		p.OnPositionAvailable().addListener(this);
		p.OnPositionChanged().addListener(this);
		if ( defaultPortfolio == null ) {
			defaultPortfolio = p;
		}
		return p;
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
	 * Перенаправить событие позициию
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
			.isEquals();
	}

}
