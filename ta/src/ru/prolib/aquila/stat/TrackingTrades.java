package ru.prolib.aquila.stat;

import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.util.Observable;

/**
 * 2012-02-02
 * $Id: TrackingTrades.java 196 2012-02-02 20:24:38Z whirlwind $
 * 
 * Интерфейс наблюдателя сделок.
 * Наблюдатель сделок формирует отчеты о проведенных сделках и уведомляет
 * наблюдателей об открытии новых, изменении и закрытии существующих сделок.
 * Каждое событие является экземпляром {@link TradeEvent}.
 */
public interface TrackingTrades extends Observable {
	
	public void startService(ServiceLocator locator) throws TrackingException;
	
	public void stopService() throws TrackingException;

}
