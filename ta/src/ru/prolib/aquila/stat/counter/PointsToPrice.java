package ru.prolib.aquila.stat.counter;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;

/**
 * Конвертирует значение счетчика в стоимость, выраженную в единицах измерения
 * стоимости портфеля.
 * 
 * 2012-02-05
 * $Id: PointsToPrice.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public class PointsToPrice extends Observable
	implements Counter<Double>, Observer
{
	private static final Logger logger = LoggerFactory.getLogger(PointsToPrice.class);
	private final Counter<Double> points;
	private Asset asset;
	private Double value;
	
	public PointsToPrice(Counter<Double> points) {
		super();
		this.points = points;
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			value = asset.priceToMoney(points.getValue());
			setChanged();
			notifyObservers();
		} catch ( AssetException e ) {
			error(e);
			value = null;
		}
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		if ( asset != null ) {
			throw new CounterServiceAlreadyStartedException();
		}
		try {
			asset = locator.getPortfolio().getAsset();
		} catch ( ServiceLocatorException e ) {
			throw new CounterException(e);
		}
		points.addObserver(this);
	}

	@Override
	public void stopService() throws CounterException {
		asset = null;
		points.deleteObserver(this);
	}
	
	private void error(Exception e) {
		if ( logger.isDebugEnabled() ) {
			logger.error("Could not obtain counter", e);
		} else {
			logger.error("Could not obtain counter: {}", e.getMessage());
		}
	}

}
