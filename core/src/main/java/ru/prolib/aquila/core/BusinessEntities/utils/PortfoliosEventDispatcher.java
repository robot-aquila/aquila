package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий набора портфелей.
 * <p>
 * Диспетчер абстрагирует набор от набора задействованных типов событий.
 * Имеет фиксированную внутреннюю структуру (создается при инстанцировании), что
 * позволяет избегать комплексных операций проверки элементов событийной системы
 * в рамках набора. Так же предоставляет интерфейс для генерации конкретных
 * событий и выполняет ретрансляцию событий подчиненных портфелей.
 * <p>
 * См. примечания {@link OrdersEventDispatcher}.
 */
public class PortfoliosEventDispatcher implements EventListener {
	private final EventDispatcher dispatcher;
	private final EventTypeSI onAvailable, onChanged, onPosAvailable,
		onPosChanged;
	
	public PortfoliosEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Portfolios");
		onAvailable = dispatcher.createType("Available");
		onChanged = dispatcher.createType("Changed");
		onPosAvailable = dispatcher.createType("PositionAvailable");
		onPosChanged = dispatcher.createType("PositionChanged");
	}
	
	/**
	 * Получить подчиненный диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить тип события: доступен новый портфель.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPortfolioAvailable() {
		return onAvailable;
	}
	
	/**
	 * Получить тип события: изменение атрибутов портфеля.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPortfolioChanged() {
		return onChanged;
	}
	
	/**
	 * Получить тип события: доступна новая позиция.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionAvailable() {
		return onPosAvailable;
	}
	
	/**
	 * Получить тип события: изменение атрибутов позиции.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionChanged() {
		return onPosChanged;
	}
	
	/**
	 * Генератор события: доступен новый портфель.
	 * <p>
	 * @param portfolio портфель
	 */
	public void fireAvailable(Portfolio portfolio) {
		dispatcher.dispatch(new PortfolioEvent(onAvailable, portfolio));
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof PositionEvent ) {
			PositionEvent e = (PositionEvent) event;
			Position position = e.getPosition();
			Portfolio portfolio = position.getPortfolio();
			if ( event.isType(portfolio.OnPositionChanged()) ) {
				dispatcher.dispatch(new PositionEvent(onPosChanged, position));
			} else if ( event.isType(portfolio.OnPositionAvailable())) {
				dispatcher.dispatch(new PositionEvent(onPosAvailable, position));
			}
			
		} else if ( event instanceof PortfolioEvent ) {
			PortfolioEvent e = (PortfolioEvent) event;
			Portfolio portfolio = e.getPortfolio();
			if ( event.isType(portfolio.OnChanged()) ) {
				dispatcher.dispatch(new PortfolioEvent(onChanged, portfolio));
			}
		}
	}
	
	/**
	 * Начать ретрансляцию событий портфеля.
	 * <p>
	 * @param portfolio портфель
	 */
	public void startRelayFor(Portfolio portfolio) {
		portfolio.OnChanged().addSyncListener(this);
		portfolio.OnPositionAvailable().addSyncListener(this);
		portfolio.OnPositionChanged().addSyncListener(this);
	}

}
