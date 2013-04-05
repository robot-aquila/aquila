package ru.prolib.aquila.experiment;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ta.Signal;
import ru.prolib.aquila.ta.SignalList;
import ru.prolib.aquila.ta.SignalSourceList;
import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.SignalSource.SignalSourceBuilder;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.math.Alligator;

/**
 * Базовая стратегия Chaos Theory.
 * 
 * Открываемся по фракталу. Фракталы фильтруются сигнальной линией. Добираем в
 * случае получения сигналов с любого измерения: фрактал, AO, AC, Williams Zone.
 * Закрываемся по стоп-лоссу по пересечению цены закрытия с зубами аллигатора.
 * В случае, если стоп-лосс проскочили, то экстренное закрытие позиции по цене
 * закрытия.
 */
public class CT0 extends CommonStrategy {
	protected final SignalSourceList sigsrc;
	protected Alligator gator;
	protected MarketData data;
	protected Asset asset;
	protected PortfolioState state;
	protected Value<Double> signalLine;
	protected int fractal;

	public CT0(ServiceLocator locator, PortfolioDriver driver) {
		super(locator, driver);
		sigsrc = new SignalSourceList();
	}

	@Override
	public void inLongPosition() throws Exception {
		if ( data.getClose().get() <= signalLine.get() ) {
			driver.closeLongImmediately("Emergency close long");
			return;
		}
		Signal signal = sigsrc.getCurrentSignals().findOne(null, Signal.BUY);
		if ( signal != null ) {
			driver.addLong(signal.getPrice(), signal.getComment());
		}
		
		Order order = driver.getSell();
		double price = asset.roundPrice(signalLine.get());
		if ( order == null || order.getQty() != state.getPosition()
			|| order.getPrice() != price )
		{
			driver.closeLong(price, "Stop-Loss (protect LONG)");
		}
	}

	@Override
	public void inShortPosition() throws Exception {
		if ( data.getClose().get() >= signalLine.get() ) {
			driver.closeShortImmediately("Emergency close short");
			return;
		}
		Signal signal = sigsrc.getCurrentSignals().findOne(null, Signal.SELL);
		if ( signal != null ) {
			driver.addShort(signal.getPrice(), signal.getComment());
		}
		
		Order order = driver.getBuy();
		double price = asset.roundPrice(signalLine.get());
		if ( order == null || order.getQty() != -state.getPosition()
			|| order.getPrice() != price )
		{
			driver.closeShort(price, "Stop-Loss (protect SHORT)");
		}

	}

	@Override
	public void inNeutralPosition() throws Exception {
		SignalList sigs = sigsrc.getCurrentSignals();
		
		Signal signal = sigs.findOne(fractal, Signal.BUY);
		if ( signal != null ) {
			driver.addLong(signal.getPrice(), signal.getComment());
		}
		
		signal = sigs.findOne(fractal, Signal.SELL);
		if ( signal != null ) {
			driver.addShort(signal.getPrice(), signal.getComment());
		}
	}

	@Override
	public void prepare() throws Exception {
		asset = driver.getAsset();
		state = locator.getPortfolioState();
		data = locator.getMarketData();
		gator = data.addAlligator("gator");
		signalLine = getSignalLine();
		SignalSourceBuilder ssb = new SignalSourceBuilder(driver.getAsset());
		fractal = sigsrc.addSignalSource(ssb.fromFractal(data.getHigh(),
				data.getLow(), signalLine));
		sigsrc.addSignalSource(ssb.fromAwesomeOscillator(
				data.addAwesomeOscillator("ao"),
				data.getHigh(), data.getLow()));
		sigsrc.addSignalSource(ssb.fromAccelOscillator(
				data.addAccelerationOscillator("ao", "ac"),
				data.getHigh(), data.getLow()));
		sigsrc.addSignalSource(ssb.fromWilliamsZones(
				data.addWilliamsZones("ao", "ac", "wz"),
				data.getClose(), data.getHigh(), data.getLow()));
	}

	@Override
	public void clean() {

	}
	
	/**
	 * Получить сигнальную линию.
	 * 
	 * Сигнальная линия используется для фильтрации фракталов и определения
	 * момента выхода из позиции при пересечении с ценой закрытия. В качестве
	 * сигнальной линии по-умолчанию используются зубы аллигатора.  
	 * @return
	 */
	protected Value<Double> getSignalLine() {
		return gator.teeth;
	}

}
