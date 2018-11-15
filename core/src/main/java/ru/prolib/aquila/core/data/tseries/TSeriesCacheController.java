package ru.prolib.aquila.core.data.tseries;

public interface TSeriesCacheController<T> {

	TSeriesCacheController<T> addCache(TSeriesCache cache);

	TSeriesCacheController<T> removeCache(TSeriesCache cache);

}