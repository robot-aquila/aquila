package ru.prolib.aquila.ChaosTheory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortfolioDriverEmergCloseShort implements
		PortfolioDriverEmergClosePosition
{
	private final static Logger logger =
		LoggerFactory.getLogger(PortfolioDriverEmergCloseShort.class);
	
	private final Portfolio portfolio;
	private final long timeout;
	private final Asset asset;
	
	public PortfolioDriverEmergCloseShort(Portfolio portfolio,
										  Asset asset, long timeout)
	{
		super();
		this.portfolio = portfolio;
		this.asset = asset;
		this.timeout = timeout;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	public long getTimeout() {
		return timeout;
	}

	@Override
	public boolean tryClose(int priceShift, String comment)
		throws PortfolioException, InterruptedException
	{
		int pos = portfolio.getPosition();
		if ( pos >= 0 ) {
			return true;
		}
		portfolio.killAll(Order.SELL);
		double price = getPrice(priceShift);
		logger.debug("Try to close short {} pcs. by {} pts.", -pos, price);		
		Order order = portfolio.limitBuy(-pos, price, comment);
		try {
			portfolio.waitForComplete(order, timeout);
			try {
				portfolio.waitForNeutralPosition(timeout);
			} catch ( PortfolioTimeoutException e ) {
				logger.debug("Can't wait anymore. Position still not neutral");
			}
		} catch ( PortfolioTimeoutException e ) {
			logger.debug("Timeout, kill order {}", order);
			portfolio.kill(order);			
		}
		return portfolio.getPosition() >= 0;
	}
	
	private double getPrice(int priceShift) throws PortfolioException {
		try {
			return asset.getPrice() + (asset.getPriceStep() * priceShift);
		} catch ( AssetException e ) {
			throw new PortfolioException("Error calculate price", e);
		}
	}

}