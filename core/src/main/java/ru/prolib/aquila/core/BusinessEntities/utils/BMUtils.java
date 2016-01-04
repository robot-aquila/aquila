package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.Direction;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * Набор вспомогательных функций для работы с объектами бизнес-модели.
 */
@Deprecated
public class BMUtils {
	
	public BMUtils() {
		super();
	}
	
	/**
	 * Создать сделку на базе тика.
	 * <p>
	 * @param tick тик-основание
	 * @param security инструмент сделки
	 * @return сделка
	 */
	public Trade tradeFromTick(Tick tick, Security security) {
		Trade t = new Trade(security.getTerminal());
		t.setDirection(Direction.BUY);
		t.setPrice(tick.getPrice());
		t.setQty(tick.getSize());
		t.setSymbol(security.getSymbol());
		t.setTime(tick.getTime());
		t.setVolume(tick.getValue());
		return t;
	}

}
