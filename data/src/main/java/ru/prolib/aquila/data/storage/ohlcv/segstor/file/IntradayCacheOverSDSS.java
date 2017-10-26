package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import java.io.BufferedReader;
import java.io.Writer;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesAggregator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

/**
 * Intraday OHLCV cache based on underlying daily segment storage.
 * <p>
 * This storage builds OHLCV data of any specified intraday timeframe using
 * underlying data of any type (for example OHLCV of lower timeframe or L1
 * updates). All OHLCV bars will be cached to a daily-segment file to speed-up
 * further operations. Intraday cache may be used to build OHLCV caches of
 * higher intervals (for example - days).
 * <p>
 * @param <RecordType> - data type of records in source segments
 */
public class IntradayCacheOverSDSS<RecordType> implements SymbolDailySegmentStorage<Candle> {
	private final String fileSuffix;
	private final ZTFrame tframe;
	private final SymbolDailySegmentStorage<RecordType> sourceSegments;
	private final SegmentFileManager cacheManager;
	private final CacheUtils utils;
	private final CandleSeriesAggregator<RecordType> aggregator;

	/**
	 * Constructor.
	 * <p>
	 * @param tframe - time frame of this OHLCV data storage
	 * @param sourceSegments - source data segments (M1 for example)
	 * @param cacheManager - manager of cache files
	 * @param utils - cache utilities
	 */
	public IntradayCacheOverSDSS(TFrame tframe,
			SymbolDailySegmentStorage<RecordType> sourceSegments,
			CandleSeriesAggregator<RecordType> aggregator,
			SegmentFileManager cacheManager, CacheUtils utils)
	{
		this.fileSuffix = "-OHLCV-" + tframe + ".cache";
		this.tframe = tframe.toZTFrame(sourceSegments.getZoneID());
		this.aggregator = aggregator;
		this.sourceSegments = sourceSegments;
		this.cacheManager = cacheManager;
		this.utils = utils;
	}
	
	public IntradayCacheOverSDSS(TFrame tframe,
			SymbolDailySegmentStorage<RecordType> sourceSegments,
			CandleSeriesAggregator<RecordType> aggregator,
			SegmentFileManager cacheManager)
	{
		this(tframe, sourceSegments, aggregator, cacheManager, CacheUtils.getInstance());
	}
	
	public SymbolDailySegmentStorage<RecordType> getUnderlyingStorage() {
		return sourceSegments;
	}
	
	public CandleSeriesAggregator<RecordType> getAggregator() {
		return aggregator;
	}
	
	public SegmentFileManager getCacheManager() {
		return cacheManager;
	}
	
	/**
	 * Get time frame of the cache.
	 * <p>
	 * @return time frame
	 */
	public ZTFrame getTimeFrame() {
		return tframe;
	}
	
	@Override
	public ZoneId getZoneID() {
		return sourceSegments.getZoneID();
	}
	
	@Override
	public Set<Symbol> listSymbols() {
		return sourceSegments.listSymbols();
	}

	@Override
	public boolean isExists(SymbolDaily segment) {
		return sourceSegments.isExists(segment);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol) throws DataStorageException {
		return sourceSegments.listDailySegments(symbol);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, DatePoint to)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, from, to);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, from);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, int maxNumSegments)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, from, maxNumSegments);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, int maxNumSegments, DatePoint to)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, maxNumSegments, to);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, MonthPoint month)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, month);
	}

	@Override
	public SegmentMetaData getMetaData(SymbolDaily segment) throws DataStorageException {
		SegmentMetaData sourceMeta = sourceSegments.getMetaData(segment);
		SegmentFileInfo cacheInfo = cacheManager.getFileInfo(segment, fileSuffix);
		try {
			if ( cacheInfo.getFullPath().exists() ) {
				CacheHeader header = utils.readHeader(cacheInfo.getFullPath());
				if ( header.getNumberOfSourceDescriptors() == 1
				  && sourceMeta.getHashCode().equals(header.getSourceDescriptor(0).getHashCode()) )
				{
					return utils.getMetaData(cacheInfo.getFullPath(), header.getNumberOfElements());
				}
			}
			EditableTSeries<Candle> series = buildUsingSourceData(segment);
			CacheHeader header = createHeader(sourceMeta, series);
			Writer writer = utils.createWriter(cacheInfo.getFullPath());
			utils.writeHeader(writer, header);
			utils.writeSeries(writer, series);
			series.clear();
			writer.close(); // The writer MUST be closed!
			return utils.getMetaData(cacheInfo.getFullPath(), header.getNumberOfElements());
		} catch ( Exception e ) {
			throw new DataStorageException("Unexpected exception: ", e);
		}
	}

	@Override
	public CloseableIterator<Candle> createReader(SymbolDaily segment) throws DataStorageException {
		SegmentMetaData sourceMeta = sourceSegments.getMetaData(segment);
		SegmentFileInfo cacheInfo = cacheManager.getFileInfo(segment, fileSuffix);
		try {
			if ( cacheInfo.getFullPath().exists() ) {
				BufferedReader reader = utils.createReader(cacheInfo.getFullPath());
				CacheHeader header = utils.readHeader(reader);
				if ( header.getNumberOfSourceDescriptors() == 1
				  && sourceMeta.getHashCode().equals(header.getSourceDescriptor(0).getHashCode()) )
				{
					return utils.createIterator(reader, tframe);
				}
				// This cache is outdated.
				reader.close();
			}
			EditableTSeries<Candle> series = buildUsingSourceData(segment);
			CacheHeader header = createHeader(sourceMeta, series);
			Writer writer = utils.createWriter(cacheInfo.getFullPath());
			utils.writeHeader(writer, header);
			utils.writeSeries(writer, series);
			writer.close(); // The writer MUST be closed!
			return utils.createIterator(series);
		} catch ( Exception e ) {
			throw new DataStorageException("Unexpected exception: ", e);
		}
	}
	
	private EditableTSeries<Candle> buildUsingSourceData(SymbolDaily segment)
			throws Exception
	{
		try ( CloseableIterator<RecordType> source = sourceSegments.createReader(segment) ) {
			return utils.buildUsingSourceData(source, tframe, aggregator);
		}
	}
	
	private CacheHeader createHeader(SegmentMetaData sourceMeta, EditableTSeries<Candle> series) {
		 return new CacheHeaderImpl()
			.addSourceDescriptor(sourceMeta.getHashCode(), sourceMeta.getPath())
			.setNumberOfElements(series.getLength());
	}

}
