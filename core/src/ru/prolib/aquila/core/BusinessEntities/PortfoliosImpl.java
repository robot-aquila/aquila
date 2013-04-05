package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;

/**
 * Реализация набора портфелей.
 * <p>
 * 2012-08-04<br>
 * $Id: PortfoliosImpl.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class PortfoliosImpl implements EditablePortfolios,EventListener {
	private static final Logger logger;
	private final EventDispatcher dispatcher;
	private final EventType onAvailable,onChanged,onPosAvailable,onPosChanged;
	private final Map<Account, EditablePortfolio> map;
	private Portfolio defaultPortfolio;
	
	static {
		logger = LoggerFactory.getLogger(PortfoliosImpl.class);
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
			throw new PortfolioNotExistsException(acc.toString());
		}
		return port;
	}

	@Override
	public synchronized void registerPortfolio(EditablePortfolio portfolio)
			throws PortfolioException
	{
		Account account = portfolio.getAccount();
		if ( map.containsKey(account) ) {
			throw new PortfolioException("Portfolio already exists: "+account);
		}
		map.put(account, portfolio);
		if ( map.size() == 1 ) {
			defaultPortfolio = portfolio;
		}
		portfolio.OnChanged().addListener(this);
		portfolio.OnPositionAvailable().addListener(this);
		portfolio.OnPositionChanged().addListener(this);
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
		Portfolio port = null;
		try {
			port = pos.getPortfolio();
		} catch ( PortfolioException e ) {
			logger.error("Couldn't translate position event: ", e);
			// TODO: This is PANIC state
			return;
		}
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

}
