package ru.prolib.aquila.ChaosTheory;

import java.util.List;

import ru.prolib.aquila.ta.Signal;

/**
 * Модификация стратегии #2.
 * 
 * В состоянии набора позиции отлавливает противоположные сигналы и экстренно
 * закрывает позицию, в случае поступления противоположного сигнала, кроме
 * фракталов.
 */
public class ChaosTheoryImpl2 extends ChaosTheoryImpl {

	public ChaosTheoryImpl2(ServiceLocator locator,
							PortfolioDriver driver)
	{
		super(locator, driver);
	}
	
	protected void statePosition() throws Exception {
		if ( ! driver.isNeutral() ) {
			int rev = driver.isLong() ? Signal.SELL : Signal.BUY;
			List<Signal> slist = signalGeneratorList.getCurrentSignals()
				.find(null, rev);
			for ( int i = 0; i < slist.size(); i ++ ) {
				if ( isReversedSignalAreImportant(slist.get(i)) ) {
					// имеется противоположный сигнал и это не фрактал
					//changeState(STATE_EMERG_CLOSE);
					closeNow("Reversed signal: " + slist.get(i).getComment());
					return;
				}
			}
		}
		super.statePosition();
	}
	
	protected boolean isReversedSignalAreImportant(Signal signal) {
		return signal.getSourceId() != 1;
	}

}
