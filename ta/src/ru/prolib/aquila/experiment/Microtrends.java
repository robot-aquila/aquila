package ru.prolib.aquila.experiment;

import java.util.Date;

import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.tasks.CloseImmediately;
import ru.prolib.aquila.tasks.TargetedLongWithFixedStopLoss;
import ru.prolib.aquila.tasks.TargetedShortWithFixedStopLoss;
import ru.prolib.aquila.tasks.Task;

public class Microtrends extends CommonStrategy {
	private MarketData data;
	private Value<Double> med,atr;
	private Task task;

	public Microtrends(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}

	@Override
	public void inLongPosition() throws Exception {
		if ( task.cancelled() ) {
			new CloseImmediately(locator).start();
		}
	}

	@Override
	public void inShortPosition() throws Exception {
		if ( task.cancelled() ) {
			new CloseImmediately(locator).start();
		}
	}

	@Override
	public void inNeutralPosition() throws Exception {
		if ( data.getLength() < 10 ) {
			return;
		}
		double m0 = med.get(-2),m1 = med.get(-1), m2 = med.get();
		double atr05 = atr.get() * 0.5;
		Candle now = data.getBar();
		if ( m0 > m1 && m1 > m2 ) {
			// нисходящий
			double delta = m0 - m2; 
			//if ( delta > atr05 && atr05 > 50 ) {
				double price = now.getCandleCenterOrCloseIfLower();
				task = new TargetedShortWithFixedStopLoss(locator)
					.openNotLongerThan(1)
					.openPrice(price)
					.closeNotLongerThan(3)
					.closePrice(price - delta * 0.25)
					.stopPrice(price + 50);
				task.start();
			//}
			
		} else if ( m0 < m1 && m1 < m2 ) {
			// восходящий
			double delta = m2 - m0;
			//if ( delta > atr05 && atr05 > 50 ) {
				double price = now.getCandleCenterOrCloseIfLower();
				task = new TargetedLongWithFixedStopLoss(locator)
					.openNotLongerThan(1)
					.openPrice(price)
					.closeNotLongerThan(3)
					.closePrice(price + delta * 0.25)
					.stopPrice(price - 50);
				task.start();
			//}
		}
	}

	@Override
	public void prepare() throws Exception {
		data = locator.getMarketData();
		data.addSub(MarketData.HIGH, MarketData.LOW, "bar.height");
		atr = data.addSma("bar.height", 5, "atr");
		med = data.getMedian();
	}

	@Override
	public void clean() {

	}

}
