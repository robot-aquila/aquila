package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesAggregator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.YearPoint;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

public class D1CacheOverCandleSDSS implements SymbolMonthlySegmentStorage<Candle> {
	
	static class ActualSegmentInfo {
		private final SegmentFileInfo tgtFileInfo;
		private final List<SymbolDaily> srcSegmentList;
		private final List<SegmentMetaData> srcMetaDataList;
		
		public ActualSegmentInfo(SegmentFileInfo tgtFileInfo,
				List<SymbolDaily> srcSegmentList,
				List<SegmentMetaData> srcMetaDataList)
		{
			this.tgtFileInfo = tgtFileInfo;
			this.srcSegmentList = srcSegmentList;
			this.srcMetaDataList = srcMetaDataList;
		}
		
		public SegmentFileInfo getTargetFileInfo() {
			return tgtFileInfo;
		}
		
		public List<SymbolDaily> getSourceSegmentList() {
			return srcSegmentList;
		}
		
		public List<SegmentMetaData> getSourceMetaDataList() {
			return srcMetaDataList;
		}
		
		public File getFullPath() {
			return tgtFileInfo.getFullPath();
		}
		
		public CacheHeader toHeader(long numberOfElements) {
			CacheHeaderImpl hdr = new CacheHeaderImpl()
					.setNumberOfElements(numberOfElements);
			for ( SegmentMetaData smd : srcMetaDataList ) {
				hdr.addSourceDescriptor(smd.getHashCode(), smd.getPath());
			}
			return hdr;
		}
		
	}
	
	static class SegmentData {
		private final CacheHeader header;
		private final EditableTSeries<Candle> candles;
		private final File path;
		
		public SegmentData(CacheHeader header,
				EditableTSeries<Candle> candles,
				File path)
		{
			this.header = header;
			this.candles = candles;
			this.path = path;
		}
		
		public CacheHeader getHeader() {
			return header;
		}
		
		public EditableTSeries<Candle> getCandles() {
			return candles;
		}
		
		public long getNumberOfElements() {
			return header.getNumberOfElements();
		}
		
		public File getFullPath() {
			return path;
		}
		
	}
	
	static class TestHelper {
		private final String fileSuffix;
		private final ZTFrame tframe;
		private final SegmentFileManager cm;
		private final SymbolDailySegmentStorage<Candle> sdss;
		private final CandleSeriesAggregator<Candle> aggr;
		private final CacheUtils utils;
		
		public TestHelper(ZTFrame tframe,
				SymbolDailySegmentStorage<Candle> sourceSegments,
				CandleSeriesAggregator<Candle> aggregator,
				SegmentFileManager cacheManager,
				CacheUtils utils)
		{
			this.fileSuffix = "-OHLCV-" + tframe.toTFrame() + ".cache";
			this.tframe = tframe;
			this.cm = cacheManager;
			this.sdss = sourceSegments;
			this.aggr = aggregator;
			this.utils = utils;
		}
		
		public ZTFrame getTFrame() {
			return tframe;
		}
		
		public SegmentFileManager getCacheManager() {
			return cm;
		}
		
		public SymbolDailySegmentStorage<Candle> getSDSS() {
			return sdss;
		}
		
		public CandleSeriesAggregator<Candle> getAggregator() {
			return aggr;
		}
		
		public CacheUtils getCacheUtils() {
			return utils;
		}
		
		public ActualSegmentInfo loadCurrentInfo(SymbolMonthly m_seg)
			throws DataStorageException
		{
			SegmentFileInfo cacheInfo = cm.getFileInfo(m_seg, fileSuffix);
			List<SymbolDaily> srcSegs = sdss.listDailySegments(m_seg.getSymbol(), m_seg.getPoint());
			List<SegmentMetaData> srcMDs = new ArrayList<>();
			for ( SymbolDaily d_seg : srcSegs  ) {
				srcMDs.add(sdss.getMetaData(d_seg));
			}
			return new ActualSegmentInfo(cacheInfo, srcSegs, srcMDs);
		}
		
		public boolean sameSources(ActualSegmentInfo cacheInfo, CacheHeader header) {
			List<SegmentMetaData> smds = cacheInfo.getSourceMetaDataList();
			long h_num = header.getNumberOfSourceDescriptors();
			int c_num = smds.size();
			if ( h_num != c_num ) {
				return false;
			}
			for ( int i = 0; i < c_num; i ++ ) {
				SegmentMetaData smd = smds.get(i);
				CacheSourceDescriptor csd = header.getSourceDescriptor(i);
				if ( ! smd.getHashCode().equals(csd.getHashCode()) 
				  || ! smd.getPath().equals(csd.getPath()) )
				{
					return false;
				}
			}
			return true;
		}
		
		public SegmentData buildSegment(ActualSegmentInfo info)
			throws Exception
		{
			List<SymbolDaily> ss = info.getSourceSegmentList();
			int count = ss.size();
			if ( count == 0 ) {
				return new SegmentData(info.toHeader(0),
						new TSeriesImpl<>(tframe),
						info.getFullPath());
			}
			EditableTSeries<Candle> target = buildUsingSourceData(ss.get(0));			
			for ( int i = 1; i < count; i ++ ) {
				buildUsingSourceData(ss.get(i), target);
			}
			return new SegmentData(info.toHeader(target.getLength()),
					target,
					info.getFullPath());
		}
		
		public void saveSegment(SegmentData data) throws Exception {
			Writer writer = utils.createWriter(data.getFullPath());
			utils.writeHeader(writer, data.getHeader());
			utils.writeSeries(writer, data.getCandles());
			writer.close();
		}
		
		public List<SymbolMonthly> compact(List<SymbolDaily> dailySegments) {
			List<SymbolMonthly> result = new ArrayList<>();
			SymbolMonthly prev = null, curr;
			for ( SymbolDaily sd : dailySegments ) {
				curr = sd.toMonthly();
				if ( prev == null || ! prev.equals(curr) ) {
					result.add(curr);
					prev = curr;
				}
			}
			return result;
		}
		
		private EditableTSeries<Candle> buildUsingSourceData(SymbolDaily segment)
				throws Exception
		{
			try ( CloseableIterator<Candle> source = sdss.createReader(segment) ) {
				return utils.buildUsingSourceData(source, tframe, aggr);
			}
		}
		
		private void buildUsingSourceData(SymbolDaily segment, EditableTSeries<Candle> target)
				throws Exception
		{
			try ( CloseableIterator<Candle> source = sdss.createReader(segment) ) {
				utils.buildUsingSourceData(source, target, aggr);
			}
		}

	}
	
	private final ZTFrame tframe;
	private final SymbolDailySegmentStorage<Candle> sdss;
	private final CacheUtils utils;
	private final TestHelper helper;

	public D1CacheOverCandleSDSS(ZTFrame tframe,
			SymbolDailySegmentStorage<Candle> sdss,
			CacheUtils utils,
			TestHelper helper)
	{
		this.tframe = tframe;
		this.sdss = sdss;
		this.utils = utils;
		this.helper = helper;
	}
	
	public D1CacheOverCandleSDSS(ZTFrame tframe,
			SymbolDailySegmentStorage<Candle> sdss,
			CandleSeriesAggregator<Candle> aggregator,
			SegmentFileManager cacheManager,
			CacheUtils utils)
	{
		this(tframe,
			sdss,
			utils,
			new TestHelper(
				tframe,
				sdss,
				aggregator,
				cacheManager,
				utils
			)
		);
	}
	
	public D1CacheOverCandleSDSS(TFrame tframe,
			SymbolDailySegmentStorage<Candle> sdss,
			CandleSeriesAggregator<Candle> aggregator,
			SegmentFileManager cacheManager,
			CacheUtils utils)
	{
		this(tframe.toZTFrame(sdss.getZoneID()),
			sdss,
			aggregator,
			cacheManager,
			utils
		);
	}
	
	public D1CacheOverCandleSDSS(TFrame tframe,
			SymbolDailySegmentStorage<Candle> sdss,
			CandleSeriesAggregator<Candle> aggregator,
			SegmentFileManager cacheManager)
	{
		this(tframe,
			sdss,
			aggregator,
			cacheManager,
			CacheUtils.getInstance()
		);
	}
	
	public SymbolDailySegmentStorage<Candle> getSDSS() {
		return sdss;
	}
	
	public CacheUtils getCacheUtils() {
		return utils;
	}
	
	public TestHelper getTestHelper() {
		return helper;
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
		return sdss.getZoneID();
	}

	@Override
	public Set<Symbol> listSymbols() {
		return sdss.listSymbols();
	}

	@Override
	public boolean isExists(SymbolMonthly seg) throws DataStorageException {
		return sdss.listDailySegments(seg.getSymbol(), seg.getPoint()).size() > 0;
	}

	@Override
	public List<SymbolMonthly> listMonthlySegments(Symbol symbol)
			throws DataStorageException
	{
		return helper.compact(sdss.listDailySegments(symbol));
	}

	@Override
	public List<SymbolMonthly>
		listMonthlySegments(Symbol symbol, MonthPoint from, MonthPoint to)
			throws DataStorageException
	{
		return helper.compact(sdss.listDailySegments(symbol,
				from.getFirstDatePoint(), to.getLastDatePoint()));
	}

	@Override
	public List<SymbolMonthly>
		listMonthlySegments(Symbol symbol, MonthPoint from)
			throws DataStorageException
	{
		return helper.compact(sdss.listDailySegments(symbol,
				from.getFirstDatePoint()));
	}

	@Override
	public List<SymbolMonthly>
		listMonthlySegments(Symbol symbol, MonthPoint from, int maxCount)
			throws DataStorageException
	{
		List<SymbolMonthly> list = helper.compact(sdss.listDailySegments(symbol,
				from.getFirstDatePoint()));
		if ( list.size() > maxCount ) {
			list.subList(maxCount, list.size()).clear();
		}
		return list;
	}

	@Override
	public List<SymbolMonthly>
		listMonthlySegments(Symbol symbol, int maxCount, MonthPoint to)
			throws DataStorageException
	{
		List<SymbolMonthly> list = helper.compact(sdss.listDailySegments(symbol,
				new DatePoint(0, 1, 1), to.getLastDatePoint()));
		if ( list.size() > maxCount ) {
			list.subList(0, list.size() - maxCount).clear();
		}
		return list;
	}

	@Override
	public List<SymbolMonthly>
		listMonthlySegments(Symbol symbol, YearPoint year)
			throws DataStorageException
	{
		return helper.compact(sdss.listDailySegments(symbol,
			new MonthPoint(year.getYear(), Month.JANUARY).getFirstDatePoint(),
			new MonthPoint(year.getYear(), Month.DECEMBER).getLastDatePoint()
		));
	}

	@Override
	public SegmentMetaData getMetaData(SymbolMonthly segment)
			throws DataStorageException
	{
		try {
			ActualSegmentInfo asi = helper.loadCurrentInfo(segment);
			if ( asi.getFullPath().exists() ) {
				CacheHeader header = utils.readHeader(asi.getFullPath());
				if ( helper.sameSources(asi, header) ) {
					return utils.getMetaData(
							asi.getFullPath(),
							header.getNumberOfElements()
						);
				}
			}
			SegmentData sd = helper.buildSegment(asi);
			helper.saveSegment(sd);
			return utils.getMetaData(
					asi.getFullPath(),
					sd.getNumberOfElements()
				);
		} catch ( Exception e ) {
			throw new DataStorageException("Unexpected exception: ", e);
		}
	}

	@Override
	public CloseableIterator<Candle> createReader(SymbolMonthly segment)
			throws DataStorageException
	{
		try {
			ActualSegmentInfo asi = helper.loadCurrentInfo(segment);
			if ( asi.getFullPath().exists() ) {
				BufferedReader reader = utils.createReader(asi.getFullPath());
				CacheHeader header = utils.readHeader(reader);
				if ( helper.sameSources(asi, header) ) {
					return utils.createIterator(reader, tframe);
				}
				reader.close();
			}
			SegmentData sd = helper.buildSegment(asi);
			helper.saveSegment(sd);
			return utils.createIterator(sd.getCandles());
		} catch ( Exception e ) {
			throw new DataStorageException("Unexpected exception: ", e);
		}
	}

}
