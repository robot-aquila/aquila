package ru.prolib.aquila.core.report;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Помощник отчета по трейдам портфеля.
 */
class PortfolioTradesHelper {
	
	PortfolioTradesHelper() {
		super();
	}
	
	/**
	 * Создать инициирующую трейд сделку по позиции.
	 * <p>
	 * @param position позиция
	 * @return сделка или null, если нулевая позиция
	 */
	public Trade createInitialTrade(Position position) {
		Long qty = position.getCurrQty();
		if ( qty == 0L ) {
			return null;
		}
		Security security = position.getSecurity();
		Double price = security.getMostAccuratePrice();
		if ( price == null ) {
			price = 0.0d;
		}
		Trade t = new Trade(position.getTerminal());
		t.setSecurityDescriptor(security.getDescriptor());
		t.setPrice(price);
		if ( qty > 0 ) {
			t.setQty(qty);
			t.setDirection(OrderDirection.BUY);
			t.setVolume(security.getMostAccurateVolume(price, qty));
		} else {
			t.setQty(-qty);
			t.setDirection(OrderDirection.SELL);
			t.setVolume(security.getMostAccurateVolume(price, -qty));
		}
		return t;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == PortfolioTradesHelper.class;
	}

}
