package ru.prolib.aquila.core.data.tseries;

/**
 * TSeries cache control interface allow to control cached data series. Caching is intended
 * to improve performance. In most cases cached data is based on another series data. This
 * means that the cache should be properly invalidated when data in base series is changed.
 * To split caching mechanism with synchronization mechanism there are cache control
 * interface and cache controller. Each series which uses caching should implement this
 * interface to make possible cache invalidation by controller. Controller is a mechanism
 * which knows how to detect when base data is changed. Controller uses cache control for
 * range invalidation to force recalculation on further access.
 */
public interface TSeriesCache {

	/**
	 * Invalidate cache starting from index.
	 * <p>
	 * @param indexFrom - all values from this index (inclusive) will be marked as invalid.
	 */
	void invalidate(int indexFrom);

}
