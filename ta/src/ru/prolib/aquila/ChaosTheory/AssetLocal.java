package ru.prolib.aquila.ChaosTheory;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Реализация локального актива.
 * После старта сервиса начинает обрабатывать события, поступающие от указанного
 * экземпляра {@link MarketData}.
 * 
 * При каждом полученном уведомлении выставляет цену актива в соответствии
 * с ценой закрытия текущего бара, после чего уведомляет своих наблюдателей
 * событие типа {@link Asset#EVENT_PRICE}.
 * 
 * При получении первого уведомления а так же при переходе метки данных на
 * другую дату выполняет пересчет клиринговых параметров:
 * 1. EstimatedPrice - устанавливается цена открытия текущего бара. 
 * 2. InitialMarginMoney - расчитывается как
 * 
 * 		EstimatedPrice * initialMarginFactor * priceStepMoney / priceStep
 * 
 * Где initialMarginFactor значение от 0 до 1 (устанавливается в конструкторе).
 * Полученное значение округляется до двух знаков после запятой.
 * 
 * После пересчета параметров независимо от того, были ли параметры фактически
 * изменены, уведомляет наблюдателей событием типа {@link Asset#EVENT_CLEARING}.
 *  
 * PriceStepMoney устанавливается в конструкторе и остается неизменным все
 * время.
 */
public class AssetLocal extends AssetImpl implements Observer {
	private static final Logger logger = LoggerFactory.getLogger(AssetLocal.class);
	private final double initialMarginFactor;
	private Date lastClearing = null;
	protected MarketData data = null;

	public AssetLocal(String assetCode, String classCode,
			double priceStep, int priceScale,
			double initialMarginFactor, double priceStepMoney)
	{
		super(assetCode, classCode, priceStep, priceScale);
		this.initialMarginFactor = initialMarginFactor;
		updatePriceStepMoney(priceStepMoney);
		super.clearChanged();
	}
	
	public double getInitialMarginFactor() {
		return initialMarginFactor;
	}
	
	public void startService(MarketData data) throws AssetException {
		if ( this.data != null ) {
			throw new AssetException("Service already started");
		}
		this.data = data;
		data.addObserver(this);
	}
	
	public void stopService() throws AssetException {
		if ( data != null ) {
			data.deleteObserver(this);
			data = null;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( o == data ) {
			updateClearing();
			updatePrice();
		}
	}
	
	private void updatePrice() {
		try {
			updatePrice(data.getClose().get());
			notifyObservers(Asset.EVENT_PRICE);
		} catch ( ValueException e ) {
			error("price update failed", e);
		}
	}
	
	private void updateClearing() {
		String msg = "failed recalculate variables for the new clearing period";
		try {
			Date current = getDayStart(data.getTime().get());
			if ( lastClearing != null && lastClearing.equals(current) ) {
				return;
			}
			double estimated = data.getOpen().get();
			double initialMargin = estimated * initialMarginFactor
				* getPriceStepMoney() / getPriceStep();
			initialMargin = Math.round(initialMargin * 100d) / 100d;
			logger.info("New clearing period {} for asset {} opened", current, getAssetCode());
			logger.info("Estimated price={} pts., initial margin in money={}", estimated, initialMargin);
			lastClearing = current;
			updateEstimatedPrice(estimated);
			updateInitialMarginMoney(initialMargin);
			setChanged();
			notifyObservers(Asset.EVENT_CLEARING);
		} catch ( ValueException e ) {
			error(msg, e);
		} catch ( AssetException e ) {
			error(msg, e);
		}
	}
	
	private Date getDayStart(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	private void error(String msg, Exception e) {
		Object params[] = null;
		if ( logger.isDebugEnabled() ) {
			params = new Object[] { getAssetCode(), msg, e.getMessage(), e };
		} else {
			params = new Object[] { getAssetCode(), msg, e.getMessage() };
		}
		logger.error("Asset {} {} : {}", params);
	}

}
