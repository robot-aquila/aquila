package ru.prolib.aquila.experiment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.*;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.math.*;

/**
 * Модификация пробойной стратегии с фильтром по STDEV и ATR.
 *
 * Позиция не открывается, если STDEV или ATR меньше указанного в конфигурации
 * значения. 
 * 
 * 2012-02-07
 * $Id: RuptureM2.java 200 2012-02-11 14:03:38Z whirlwind $
 */
public class RuptureM2 extends Rupture {
	private static final Logger logger = LoggerFactory.getLogger(RuptureM2.class);
	
	/**
	 * Идентификатор параметра конфигурации, определяющего количество баров
	 * для расчета ATR и STDEV. Значение по умолчанию 15.
	 */
	public static final String PROP_FILTER_PERIOD = "Rupture.FilterPeriod";
	
	/**
	 * Идентификатор параметра конфигурации, определяющего .
	 */
	public static final String PROP_FILTER_VALUE = "Rupture.FilterValue";


	private Sma atr;
	private Stdev stdev;
	private double filter;

	public RuptureM2(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		Props props = locator.getProperties();
		MarketData data = locator.getMarketData();
		
		int period = props.getInt(PROP_FILTER_PERIOD, 15);
		filter = props.getDouble(PROP_FILTER_VALUE, 100); 
		data.addSub(MarketData.HIGH, MarketData.LOW, "bar.height");
		atr = data.addSma("bar.height", period, "atr");
		stdev = new Stdev(data.getClose(), period, "stdev");
		data.addValue(stdev);
		
		logger.info("$Id: RuptureM2.java 200 2012-02-11 14:03:38Z whirlwind $");
		logger.info("Configured with:");
		logger.info("Filter period={}, filter value={}", period, filter);
	}
	
	@Override
	public void inNeutralPosition() throws Exception {
		if ( atr.get() != null && atr.get() >= filter
			&& stdev.get() != null && stdev.get() >= filter )
		{
			super.inNeutralPosition();
		} else {
			driver.killAll();
			logger.debug("Filtered by ATR={} STDEV={}", atr.get(), stdev.get());
		}
	}

}
