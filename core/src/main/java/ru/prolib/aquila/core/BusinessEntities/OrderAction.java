package ru.prolib.aquila.core.BusinessEntities;

public enum OrderAction {
	BUY,
	COVER,
	SELL,
	SELL_SHORT;
	
	public boolean isBuy() {
		switch ( this ) {
		case BUY:		 return true;
		case COVER:		 return true;
		case SELL:		 return false;
		case SELL_SHORT: return false;
		default:
			throw new IllegalStateException("Unsupported case: " + this);
		}
	}
}
