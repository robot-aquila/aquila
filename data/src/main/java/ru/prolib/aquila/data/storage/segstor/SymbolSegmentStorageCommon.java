package ru.prolib.aquila.data.storage.segstor;

import java.time.ZoneId;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface SymbolSegmentStorageCommon {

	/**
	 * Get time zone of the storage.
	 * <p>
	 * Knowledge of the time zone is important to build storages hierarchy.
	 * Every segment storage which wraps this one must use the same time zone
	 * to keep coherence of segment identification. 
	 * <p>
	 * @return time zone ID
	 */
	ZoneId getZoneID();

	/**
	 * Get available symbols.
	 * <p>
	 * @return symbols
	 */
	Set<Symbol> listSymbols();

}