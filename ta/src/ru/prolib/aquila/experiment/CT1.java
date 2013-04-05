package ru.prolib.aquila.experiment;

import java.util.List;

import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.stat.TrackingPosition;
import ru.prolib.aquila.stat.TrackingPositionChange;
import ru.prolib.aquila.ta.Signal;
import ru.prolib.aquila.ta.SignalList;
import ru.prolib.aquila.ta.Value;


/**
 * Расширенная стратегия Chaos Theory.
 * 
 * Экстренно закрывает позицию при пересечении ценой закрытия сигнальной линии.
 * Открывает по фракталу, отфильтрованному по зубам гатора. Добирает по
 * сигналам из любого измерения на следующих барах. Если нет сигнала на текущем
 * баре, то выставляет лимитную заявку на закрытие по цене
 * для лонга
 * 
 * 		average inc price * (1 + profit rate)
 * 
 * для шорта
 * 
 * 		average inc price * (1 - profit rate)
 * 
 * где average inc price - средняя цена лота при увеличении позиции 
 * profit rate - норма прибыли
 * Если в течение no signal bars не было сигналов на добор, позиция закрывается
 * по текущей цене. 
 */
public class CT1 extends CT0 {
	protected int noSignalBars = 1;
	protected double profitRate = 0.005d;
	protected double lossRate = 0.005d;
	protected int firstBuySigBar = -1;
	protected int firstSelSigBar = -1;
	protected int maxBars = 3;
	

	public CT1(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		noSignalBars = 5; // TODO: в конфиг
		profitRate = 0.001d; // TODO: в конфиг
		lossRate = 0.0025d; // TODO: в конфиг
		maxBars = 15;
	}

	@Override
	public void inLongPosition() throws Exception {
		firstBuySigBar = firstSelSigBar = -1;
		driver.killAll();
		if ( data.getClose().get() <= signalLine.get() ) {
			driver.closeLongImmediately("Close long (By stop-line)");
			return;
		} else if ( data.getLastBarIndex() - getLastTrackingPosition()
				.getFirstChange().getBarIndex() >= maxBars )
		{
			driver.closeLongImmediately("Close long (max bars)");
			return;
		}
		
		Signal signal = sigsrc.getCurrentSignals().findOne(null, Signal.BUY);
		if ( signal != null ) {
			driver.addLong(signal.getPrice(), signal.getComment());
		} else {
			if ( getNoSignalBars() >= noSignalBars ) {
				driver.closeLongImmediately("Close long (No signal bars)");
			//} else {
			//	double lossPrice = asset.roundPrice(averageIncPrice
			//		.calculate(getLastTrackingPosition())) * (1 - lossRate);
			//	driver.closeLong(lossPrice, "Protect long (By loss rate)");
			}
		}
	}

	@Override
	public void inShortPosition() throws Exception {
		firstBuySigBar = firstSelSigBar = -1;
		driver.killAll();
		if ( data.getClose().get() >= signalLine.get() ) {
			driver.closeShortImmediately("Close short (By stop-line)");
			return;
		} else if ( data.getLastBarIndex() - getLastTrackingPosition()
				.getFirstChange().getBarIndex() >= maxBars )
		{
			driver.closeLongImmediately("Close short (max bars)");
			return;
		} 

		Signal signal = sigsrc.getCurrentSignals().findOne(null, Signal.SELL);
		if ( signal != null ) {
			driver.addShort(signal.getPrice(), signal.getComment());
		} else {
			if ( getNoSignalBars() >= noSignalBars ) {
				driver.closeShortImmediately("Close short (No signal bars)");
			//} else {
			//	double lossPrice = asset.roundPrice(averageIncPrice
			//		.calculate(getLastTrackingPosition())) * (1 + lossRate);
			//	driver.closeShort(lossPrice, "Protect short (By loss rate)");
			}
		}
	}
	
	@Override
	public void inNeutralPosition() throws Exception {
		driver.killAll();
		SignalList sigs = sigsrc.getCurrentSignals();
		
		Signal signal = null;
		signal = sigs.findOne(null, Signal.BUY);
		if ( signal != null ) {
			// Сигнал должен быть не первым в последовательности сигналов,
			// цена сигнала должна быть выше стоп-линии,
			// должен быть в следующем, за баром предыдущего сигнала
			if ( firstBuySigBar > -1 && signal.getPrice() > signalLine.get()
				&& data.getLastBarIndex() - firstBuySigBar == 1 )
			{
				driver.addLong(signal.getPrice(), signal.getComment());
			} else if ( signal.getSourceId() == fractal ) {
				// Иначе считаем этот сигнал первым сигналом последовательности
				firstBuySigBar = data.getLastBarIndex();
			}
		}
		
		signal = sigs.findOne(null, Signal.SELL);
		if ( signal != null ) {
			// Сигнал должен быть не первым в последовательности сигналов,
			// цена сигнала должна быть ниже стоп-линии,
			// должен быть в следующем за баром предыдущего сигнала
			if ( firstSelSigBar > -1 && signal.getPrice() < signalLine.get()
					&& data.getLastBarIndex() - firstSelSigBar == 1)
			{
				driver.addShort(signal.getPrice(), signal.getComment());
			} else if ( signal.getSourceId() == fractal ) {
				firstSelSigBar = data.getLastBarIndex();
			}
		}
	}
	
	protected int getNoSignalBars() throws Exception {
		return data.getLength() - 1 - getLastPositionChange().getBarIndex();
	}
	
	protected TrackingPositionChange getLastPositionChange() throws Exception {
		throw new Exception("Temporarily disabled");
		//List<TrackingPositionChange> changes = getLastTrackingPosition()
		//	.getChanges();
		//return changes.get(changes.size() - 1);
	}
	
	protected TrackingPosition getLastTrackingPosition() throws Exception {
		throw new Exception("Temporarily disabled");
		//List<TrackingPosition> trades = locator.getTrackingPortfolio()
		//	.getTrades();
		//return trades.get(trades.size() - 1);
	}
	
	@Override
	protected Value<Double> getSignalLine() {
		return gator.teeth;
	}

}
