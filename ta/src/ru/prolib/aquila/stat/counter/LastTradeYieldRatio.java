package ru.prolib.aquila.stat.counter;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.stat.PositionChange;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.stat.TradeEvent;

/**
 * Расчитывает относительную прибыльность сделки.
 * 
 * Прибыльность расчитывается отношение стоимости продажи к стоимости покупки
 * минус единица * 100. Например:
 * 
 * Прибыльный лонг: стоимость покупки (сумма по сделкам на увеличение позиции)
 * 1x200 + 5x205 = 1225, стоимость продажи (сумма по сделкам на сокращение
 * позиции до нуля) 6x208 = 1248, прибыльность расчитывается как
 * 1248 / 1225 - 1 = 0.01877551 * 100, то есть ~1.87%.
 * 
 * Убыточный лонг: стоимость покупки 1x205, продажи 1x200, прибыльность
 * 200 / 205 - 1 = -0.024390244 * 100, то есть ~ -2.44%.
 * 
 * Прибыльный шорт: продажа 200, покупка 180, прибыльность 
 * (здесь суммы меняются местами, так как открытие короткой - это продажа)
 * 200 / 180 - 1 = 0.11111 * 100, что примерно ~11% 
 *
 * Убыточный шорт: продали 180, купили 200, прибыльность
 * 180 / 200 - 1 = -0.1 * 100, что означает потеряли -10%.
 * 
 * Работает на основании событий от объекта типа
 * {@link ru.prolib.aquila.stat.TrackingTrades}.
 * Уведомляет наблюдателей только в случае обработки соответствующего события.
 * 
 * 2012-02-06
 * $Id: LastTradeYieldRatio.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class LastTradeYieldRatio extends Observable
	implements Counter<Double>, Observer
{
	private TrackingTrades tracking;
	private Double value;
	
	public LastTradeYieldRatio() {
		super();
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( arg instanceof TradeEvent ) {
			TradeEvent event = (TradeEvent) arg;
			List<PositionChange> changes = event.getTradeReport().getChanges();
			if ( event.getEventId() == TradeEvent.TRADE_CLOSED ) {
				double bval = 0.0d, sval = 0.0d;
				for ( int i = 0; i < changes.size(); i ++ ) {
					PositionChange c = changes.get(i);
					double val = c.getPrice() * c.getQty();
					if ( val < 0 ) {
						sval += Math.abs(val);
					} else {
						bval += val;
					}
				}
				value = ((sval / bval) - 1d) * 100d;
				setChanged();
				notifyObservers();
			}
		}
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void startService(ServiceLocator locator) throws CounterException {
		if ( tracking != null ) {
			throw new CounterServiceAlreadyStartedException();
		}
		try {
			tracking = locator.getTrackingTrades();
		} catch ( ServiceLocatorException e ) {
			throw new CounterException(e);
		}
		tracking.addObserver(this);
	}

	@Override
	public void stopService() throws CounterException {
		if ( tracking != null ) {
			tracking.deleteObserver(this);
			tracking = null;
		}
	}

}
