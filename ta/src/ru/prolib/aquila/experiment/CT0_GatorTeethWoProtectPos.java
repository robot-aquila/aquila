package ru.prolib.aquila.experiment;

import ru.prolib.aquila.ChaosTheory.PortfolioDriver;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ta.Signal;

/**
 * Базовая стратегия, сигнальная линия на зубах, не выставляет защитные стопы,
 * закрывает только по close бара.
 */
public class CT0_GatorTeethWoProtectPos extends CT0 {

	public CT0_GatorTeethWoProtectPos(ServiceLocator locator,
									  PortfolioDriver driver)
	{
		super(locator, driver);
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
	}

}
