package ru.prolib.aquila.core.utils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * In some cases we need to know a price scale on lowest-levels of the system.
 * This interface is to provide such information when it needed.
 */
public interface PriceScaleDB {
	
	/**
	 * Get price scale of symbol.
	 * <p>
	 * @param symbol the symbol
	 * @return price scale
	 */
	int getScale(Symbol symbol);

}
