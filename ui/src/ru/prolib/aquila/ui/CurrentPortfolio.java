package ru.prolib.aquila.ui;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
/**
 * $Id: CurrentPortfolio.java 525 2013-02-12 19:51:39Z huan.kaktus $
 */
public interface CurrentPortfolio {
	
	public Portfolio getCurrentPortfolio();
	
	public void setCurrentPortfolio(Portfolio portfolio);
	
	public EventType OnCurrentPortfolioChanged();
}
