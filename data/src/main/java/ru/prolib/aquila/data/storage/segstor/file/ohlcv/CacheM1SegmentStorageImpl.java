package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import java.io.BufferedReader;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

public class CacheM1SegmentStorageImpl implements SymbolDailySegmentStorage<Candle> {
	public static final String FILE_SUFFIX = "-OHLCV-M1.cache";
	private final CacheUtils utils;
	private final SymbolDailySegmentStorage<L1Update> sourceSegments;
	private final SegmentFileManager cacheManager;
	
	public CacheM1SegmentStorageImpl(SymbolDailySegmentStorage<L1Update> sourceSegments,
			SegmentFileManager cacheManager, CacheUtils utils)
	{
		this.sourceSegments = sourceSegments;
		this.cacheManager = cacheManager;
		this.utils = utils;
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
	public List<SymbolDaily> listDailySegments(Symbol symbol)
			throws DataStorageException
	{
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
	public List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, int maxCount)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, from, maxCount);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, int maxCount, DatePoint to)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, maxCount, to);
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, MonthPoint month)
			throws DataStorageException
	{
		return sourceSegments.listDailySegments(symbol, month);
	}

	@Override
	public SegmentMetaData getMetaData(SymbolDaily segment)
			throws DataStorageException
	{
		SegmentMetaData sourceMeta = sourceSegments.getMetaData(segment);
		SegmentFileInfo cacheInfo = cacheManager.getFileInfo(segment, FILE_SUFFIX);
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
	public CloseableIterator<Candle> createReader(SymbolDaily segment)
			throws DataStorageException
	{
		SegmentMetaData sourceMeta = sourceSegments.getMetaData(segment);
		SegmentFileInfo cacheInfo = cacheManager.getFileInfo(segment, FILE_SUFFIX);
		try {
			if ( cacheInfo.getFullPath().exists() ) {
				BufferedReader reader = utils.createReader(cacheInfo.getFullPath());
				CacheHeader header = utils.readHeader(reader);
				if ( header.getNumberOfSourceDescriptors() == 1
				  && sourceMeta.getHashCode().equals(header.getSourceDescriptor(0).getHashCode()) )
				{
					return utils.createIterator(reader, TimeFrame.M1);
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
		try ( CloseableIterator<L1Update> source = sourceSegments.createReader(segment) ) {
			return utils.buildUsingSourceData(source, TimeFrame.M1);
		}
	}
	
	private CacheHeader createHeader(SegmentMetaData sourceMeta, EditableTSeries<Candle> series) {
		 return new CacheHeaderImpl()
			.addSourceDescriptor(sourceMeta.getHashCode(), sourceMeta.getPath())
			.setNumberOfElements(series.getLength());
	}

}
