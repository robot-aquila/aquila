package ru.prolib.aquila.core.utils;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

/**
 * The price scale database based on terminal data. It will work only when security is available.
 * Otherwise will throw an exception. Useful for example for L1/L2 data provides when start providing
 * the data make sense only if security attributes already initialized. Works for single terminal.
 */
public class PriceScaleDBTB implements PriceScaleDB {
	private final Terminal terminal;
	
	public PriceScaleDBTB(Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public int getScale(Symbol symbol) {
		Security security = null;
		try {
			security = terminal.getSecurity(symbol);
		} catch ( SecurityException e ) {
			throw new IllegalStateException("Security not exists: " + symbol, e);
		}
		if ( ! security.isAvailable() ) {
			throw new IllegalStateException("Security is not available: " + symbol);
		}
		return security.getScale();
	}

}
