package ru.prolib.aquila.ui;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;

public class CurrentPortfolioImpl implements CurrentPortfolio {
	/**
	 * $Id: CurrentPortfolioImpl.java 525 2013-02-12 19:51:39Z huan.kaktus $
	 */
	private final EventType portfolioChanged;
	private final EventDispatcher dispatcher;
	private Portfolio portfolio;
	
	public CurrentPortfolioImpl(EventType portfolioChanged, EventDispatcher dispatcher) 
	{
		this.portfolioChanged = portfolioChanged;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public Portfolio getCurrentPortfolio() {		
		return portfolio;
	}

	@Override
	public void setCurrentPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
		fireCurrentPortfolioChangedEvent();
	}

	@Override
	public EventType OnCurrentPortfolioChanged() {		
		return portfolioChanged;
	}
	
	private void fireCurrentPortfolioChangedEvent() {
		dispatcher.dispatch(new EventImpl(portfolioChanged));
	}

}
