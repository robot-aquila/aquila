package ru.prolib.aquila.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.PortfolioException;

public class TestPortfolioStateControllerImpl implements
		TestPortfolioStateController
{
	private static final Logger logger = LoggerFactory.getLogger(TestPortfolioStateControllerImpl.class);
	/**
	 * Актив, для которого открыт период.
	 * Нулевое значение является индикатором того, что период не открыт.
	 */
	protected Asset asset = null;
	
	protected double cacheInitialMarginMoney = 0.0d;
	protected double cachePriceStepMoney = 0.0d;
	protected double cacheEstimatedPrice = 0.0d;
	
	protected double initialMarginMoney = 0.0d;
	protected double variationMarginPoints = 0.0d;
	protected double money = 0.0d;
	protected int position = 0;
	
	public TestPortfolioStateControllerImpl() {
		super();
	}

	@Override
	public void setMoney(double money)
			throws TestPortfolioStatePeriodOpenedException
	{
		if ( asset != null ) {
			throw new TestPortfolioStatePeriodOpenedException();
		}
		this.money = money;
		logger.info("Set money: {}", money);
	}

	@Override
	public void setPosition(int position)
			throws TestPortfolioStatePeriodOpenedException
	{
		if ( asset != null ) {
			throw new TestPortfolioStatePeriodOpenedException();
		}
		this.position = position;
		logger.info("Set position: {}", position);
	}

	@Override
	public void closePeriod() throws PortfolioException {
		if ( asset != null ) {
			// TODO: log message
			money += getInitialMargin() + getVariationMargin();
			initialMarginMoney = 0.0d;
			variationMarginPoints = 0.0d;
			asset = null;
		}
	}

	@Override
	public double getMoney() {
		return money;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public double getVariationMargin() throws PortfolioException {
		if ( asset == null ) {
			return 0.0d;
		}
		try {
			double pts = variationMarginPoints + (asset.getPrice() * position);
			return asset.priceToMoney(pts);
		} catch ( AssetException e ) {
			throw new PortfolioException(e);
		}
	}

	@Override
	public double getInitialMargin() {
		if ( asset == null ) {
			return 0.0d;
		}
		return Math.abs(initialMarginMoney);
	}

	@Override
	public void openPeriod(Asset asset)
			throws TestPortfolioStatePeriodOpenedException,
				   PortfolioException
	{
		if ( this.asset != null ) {
			throw new TestPortfolioStatePeriodOpenedException();
		}
		this.asset = asset;
		try {
			cacheInitialMarginMoney = asset.getInitialMarginMoney();
			cachePriceStepMoney = asset.getPriceStepMoney();
			cacheEstimatedPrice = asset.getEstimatedPrice();
		} catch ( AssetException e ) {
			throw new PortfolioException(e);
		}
		initialMarginMoney = cacheInitialMarginMoney * position;
		variationMarginPoints = cacheEstimatedPrice * -position;
		money -= getInitialMargin();
		// TODO: log message
	}
	
	@Override
	public void changePosition(int delta, double price)
			throws TestPortfolioStatePeriodNotOpenedException
	{
		if ( asset == null ) {
			throw new TestPortfolioStatePeriodNotOpenedException();
		}
		money += getInitialMargin();
		variationMarginPoints += (price * -delta);
		position += delta;
		initialMarginMoney = cacheInitialMarginMoney * position;
		money -= getInitialMargin();
		// TODO: log message
	}

	@Override
	public void changePosition(int delta)
			throws TestPortfolioStatePeriodNotOpenedException,
				   PortfolioException
	{
		if ( asset == null ) {
			throw new TestPortfolioStatePeriodNotOpenedException();
		}
		try {
			changePosition(delta, asset.getPrice());
		} catch ( AssetException e ) {
			throw new PortfolioException(e);
		}
	}
	


}
