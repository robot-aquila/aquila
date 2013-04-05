package ru.prolib.aquila.ChaosTheory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Класс экстренного закрытия длинной позиции.
 */
public class PortfolioDriverEmergCloseLong
	implements PortfolioDriverEmergClosePosition
{
	private final static Logger logger = LoggerFactory.getLogger(PortfolioDriverEmergCloseLong.class);
	private final Portfolio portfolio;
	private final long timeout;
	private final Asset asset;
	
	/**
	 * Конструктор.
	 * 
	 * @param portfolio портфель, через который будут выполняться операции
	 * @param timeout время ожидания исполнения заявок и обнуления позы
	 */
	public PortfolioDriverEmergCloseLong(Portfolio portfolio,
										 Asset asset, long timeout)
	{
		super();
		this.portfolio = portfolio;
		this.asset = asset;
		this.timeout = timeout;
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public long getTimeout() {
		return timeout;
	}

	@Override
	public boolean tryClose(int priceShift, String comment)
		throws PortfolioException, InterruptedException
	{
		int pos = portfolio.getPosition();
		if ( pos <= 0 ) {
			return true;
		}
		portfolio.killAll(Order.BUY);
		double price = getPrice(priceShift);
		logger.debug("Try to close long {} pcs. by {} pts.", pos, price);
		Order order = portfolio.limitSell(pos, price, comment);
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
		return portfolio.getPosition() <= 0;
	}
	
	private double getPrice(int priceShift) throws PortfolioException {
		try {
			return asset.getPrice() - (asset.getPriceStep() * priceShift);
		} catch ( AssetException e ) {
			throw new PortfolioException("Error calculate price", e);
		}
	}

}