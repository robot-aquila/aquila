package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.Direction;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.Tick;

/**
 * Набор вспомогательных функций для работы с объектами бизнес-модели.
 */
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
		t.setPrice(tick.getValue());
		t.setQty(tick.getOptionalValueAsLong());
		t.setSymbol(security.getSymbol());
		t.setTime(tick.getTime());
		t.setVolume(security.getMostAccurateVolume(t.getPrice(), t.getQty()));
		return t;
	}

}
