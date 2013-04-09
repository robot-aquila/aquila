package ru.prolib.aquila.ui;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioEvent;
import ru.prolib.aquila.core.BusinessEntities.Portfolios;

public class CurrentPortfolioImpl implements CurrentPortfolio, Starter, EventListener {
	/**
	 * $Id: CurrentPortfolioImpl.java 525 2013-02-12 19:51:39Z huan.kaktus $
	 */
	private final EventType portfolioChanged;
	private final EventDispatcher dispatcher;
	private Portfolios portfolios;
	private Portfolio portfolio;
	
	public CurrentPortfolioImpl(
			Portfolios portfolios, EventType portfolioChanged, EventDispatcher dispatcher) 
	{
		this.portfolioChanged = portfolioChanged;
		this.dispatcher = dispatcher;
		this.portfolios = portfolios;
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
		dispatcher.dispatch(new PortfolioEvent(portfolioChanged, portfolio));
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#start()
	 */
	@Override
	public void start() throws StarterException {
		portfolios.OnPortfolioAvailable().addListener(this);
		
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#stop()
	 */
	@Override
	public void stop() throws StarterException {
		portfolios.OnPortfolioAvailable().removeListener(this);
		
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if(event.isType(portfolios.OnPortfolioAvailable())) {
			PortfolioEvent e = (PortfolioEvent) event;
			if(portfolio == null) {
				setCurrentPortfolio(e.getPortfolio());
			}
		}		
	}
	
	public Portfolios getPortfolios() {
		return portfolios;
	}
	
	public EventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public void setPortfolio(Portfolio prt) {
		portfolio = prt;
	}

}
