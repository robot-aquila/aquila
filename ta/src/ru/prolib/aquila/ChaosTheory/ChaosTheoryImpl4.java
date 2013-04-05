package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.ta.Signal;
import ru.prolib.aquila.ta.SignalList;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Теория Хаоса версия #4.
 * Отличия от первой версии касаются стоп-лоссов в состоянии POSITION.
 * Цена стоп-лосса выбирается по следующей схеме:
 * 1. Формируется сигнал для закрытия по зубам.
 * 2. Из двух сигналов: из противоположного сигнала (если такой есть) и сигнала
 * полученного на шаге 1 выбирается с ценой наименьших потерь.
 * 3. Из двух сигналов: текущий сигнал на закрытие (если такой есть) и сигнала
 * полученного на шаге 2 выбирается с ценой наименьших потерь.
 */
public class ChaosTheoryImpl4 extends ChaosTheoryImpl {

	public ChaosTheoryImpl4(ServiceLocator locator,
							PortfolioDriver driver)
	{
		super(locator, driver);
	}
	
	protected void statePosition() throws Exception {
		if ( driver.isNeutral() ) {
			changeState(STATE_OPENING);
			return;
		}
		MarketData data = locator.getMarketData();
		double step = driver.getAsset().getPriceStep();
		
		double teethPrice = Math.round(alligator.teeth.get() / step) * step;
		SignalList signals = signalGeneratorList.getCurrentSignals();
		Signal sig1 = null,sig2 = null;

		if ( driver.isLong() ) { // обработка лонга
			if ( data.getClose().get() <= alligator.teeth.get() ) {
				changeState(STATE_EMERG_CLOSE);
				return;
			}
			
			sig1 = signals.findOne(null, Signal.BUY);
			if ( sig1 != null ) {
				driver.addLong(sig1.getPrice(), sig1.getComment());
			}
			
			// Рассчет стоп-лосса
			// Первый уровень: или обратный сигнал или по зубам
			sig1 = new Signal(0, Signal.SELL, teethPrice,
					"Stop-Loss (protect long, by teeth)");
			sig2 = signals.findOne(null, Signal.SELL);
			if ( sig2 != null && sig2.getPrice() > sig1.getPrice() ) {
				sig1 = new Signal(0, Signal.SELL, sig2.getPrice(),
						"Stop-Loss (protect long, " + sig2.getComment() + ")");
			}
			// Второй уровень: или предыдущий или текущий
			if ( driver.getSell() != null ) {
				Order o = driver.getSell();
				sig2 = new Signal(0, Signal.SELL, o.getPrice(), o.getComment());
				if ( sig2.getPrice() > sig1.getPrice() ) {
					sig1 = sig2;
				}
			}
			driver.closeLong(sig1.getPrice(), sig1.getComment());
			
		} else { // обработка шорта
			if ( data.getClose().get() >= alligator.teeth.get() ) {
				changeState(STATE_EMERG_CLOSE);
				return;
			}
			
			sig1 = signals.findOne(null, Signal.SELL);
			if ( sig1 != null ) {
				driver.addShort(sig1.getPrice(), sig1.getComment());
			}
			
			// Рассчет стоп-лосса
			// Первый уровень: обратный сигнал или зубы
			sig1 = new Signal(0, Signal.BUY, teethPrice,
					"Stop-Loss (protect short, by teeth)");
			sig2 = signals.findOne(null, Signal.BUY);
			if ( sig2 != null && sig2.getPrice() < sig1.getPrice() ) {
				sig1 = new Signal(0, Signal.BUY, sig2.getPrice(),
						"Stop-Loss (protect short, " + sig2.getComment() + ")");
			}
			// Второй уровень: или предыдущий или текущий
			if ( driver.getBuy() != null ) {
				Order o = driver.getBuy();
				sig2 = new Signal(0, Signal.BUY, o.getPrice(), o.getComment());
				if ( sig2.getPrice() < sig1.getPrice() ) {
					sig1 = sig2;
				}
			}
			driver.closeShort(sig1.getPrice(), sig1.getComment());
			
		}
	}

}
