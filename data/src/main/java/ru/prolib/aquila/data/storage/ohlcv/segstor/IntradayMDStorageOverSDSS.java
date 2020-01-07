package ru.prolib.aquila.data.storage.ohlcv.segstor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CloseableIteratorOfSeries;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.utils.LimitedAmountIterator;
import ru.prolib.aquila.data.storage.ohlcv.utils.PreciseTimeLimitsIterator;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

/**
 * The intraday OHLCV data storage of specified timeframe.
 */
public class IntradayMDStorageOverSDSS implements MDStorage<TFSymbol, Candle> {
	private final SymbolDailySegmentStorage<Candle> segstor;
	private final ZTFrame tframe;
	
	/**
	 * Constructor.
	 * <p>
	 * @param segstor - OHLCV data segment storage
	 * @param tframe - timeframe of OHLCV data
	 */
	public IntradayMDStorageOverSDSS(SymbolDailySegmentStorage<Candle> segstor,
			ZTFrame tframe)
	{
		if ( ! tframe.isIntraday() ) {
			throw new IllegalArgumentException("Expected timeframe must be intraday but: " + tframe);
		}
		this.segstor = segstor;
		this.tframe = tframe;
	}
	
	public SymbolDailySegmentStorage<Candle> getSegmentStorage() {
		return segstor;
	}
	
	public ZTFrame getTimeFrame() {
		return tframe;
	}
	
	@Override
	public Set<TFSymbol> getKeys() throws DataStorageException {
		Set<TFSymbol> result = new HashSet<>();
		for ( Symbol symbol : segstor.listSymbols() ) {
			result.add(new TFSymbol(symbol, tframe));
		}
		return result;
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key) throws DataStorageException {
		validateTimeFrame(key);
		List<SymbolDaily> segments = segstor.listDailySegments(key.getSymbol());
		return segments.size() > 0 ?
				new DailySegmentsCombiner(segstor, segments) : new CloseableIteratorStub<>();
	}

	@Override
	public CloseableIterator<Candle> createReaderFrom(TFSymbol key, Instant from)
			throws DataStorageException
	{
		validateTimeFrame(key);
		from = tframe.getInterval(from).getStart();
		LocalDate dateFrom = from.atZone(segstor.getZoneID()).toLocalDate();
		List<SymbolDaily> segments = segstor.listDailySegments(key.getSymbol(),
				new DatePoint(dateFrom.getYear(), dateFrom.getMonthValue(), dateFrom.getDayOfMonth()));
		return segments.size() > 0 ?
			new PreciseTimeLimitsIterator(new DailySegmentsCombiner(segstor, segments), from, null) : 
			new CloseableIteratorStub<>();
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, int count)
			throws DataStorageException
	{
		validateTimeFrame(key);
		from = tframe.getInterval(from).getStart();
		LocalDate dateFrom = from.atZone(segstor.getZoneID()).toLocalDate();
		List<SymbolDaily> segments = segstor.listDailySegments(key.getSymbol(),
				new DatePoint(dateFrom.getYear(), dateFrom.getMonthValue(), dateFrom.getDayOfMonth()));
		return segments.size() > 0 ? new LimitedAmountIterator(new PreciseTimeLimitsIterator(
				new DailySegmentsCombiner(segstor, segments), from, null), count) :
			new CloseableIteratorStub<>();
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, Instant to)
			throws DataStorageException
	{
		validateTimeFrame(key);
		from = tframe.getInterval(from).getStart();
		to = tframe.getInterval(to).getStart();
		LocalDate dateFrom = from.atZone(segstor.getZoneID()).toLocalDate();
		LocalDate dateTo = to.atZone(segstor.getZoneID()).toLocalDate();
		List<SymbolDaily> segments = segstor.listDailySegments(key.getSymbol(),
				new DatePoint(dateFrom.getYear(), dateFrom.getMonthValue(), dateFrom.getDayOfMonth()),
				new DatePoint(dateTo.getYear(), dateTo.getMonthValue(), dateTo.getDayOfMonth()));
		return segments.size() > 0 ?
			new PreciseTimeLimitsIterator(new DailySegmentsCombiner(segstor, segments), from, to) : 
			new CloseableIteratorStub<>();
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, int count, Instant to)
			throws DataStorageException
	{
		validateTimeFrame(key);
		to = tframe.getInterval(to).getStart();
		LocalDate dateTo = to.atZone(segstor.getZoneID()).toLocalDate();
		SymbolDaily lastSegment = new SymbolDaily(key.getSymbol(), dateTo.getYear(),
				dateTo.getMonthValue(), dateTo.getDayOfMonth());
		LinkedList<SymbolDaily> segments = new LinkedList<>();
		List<SymbolDaily> allSegments = segstor.listDailySegments(key.getSymbol());
		if ( allSegments.size() == 0 ) {
			return new CloseableIteratorStub<>();
		}
		for ( SymbolDaily segment : allSegments ) {
			if ( segment.compareTo(lastSegment) <= 0 ) {
				segments.add(segment);
			}
		}
		EditableTSeries<Candle> result = new TSeriesImpl<>(tframe);
		try {
			while ( segments.size() > 0 ) {
				SymbolDaily segment = segments.removeLast();
				CloseableIterator<Candle> iterator =
						new PreciseTimeLimitsIterator(segstor.createReader(segment), null, to);
				while ( iterator.next() ) {
					Candle x = iterator.item();
					result.set(x.getStartTime(), x);
				}
				iterator.close();
				if ( result.getLength() > count ) {
					break;
				}
			}
			
			int rlen = result.getLength();
			if ( rlen > count ) {
				EditableTSeries<Candle> temp = new TSeriesImpl<Candle>(tframe);
				for ( int i = rlen - count; i < rlen; i ++ ) {
					Candle x = result.get(i);
					temp.set(x.getStartTime(), x);
				}
				result = temp;
			}
			return new CloseableIteratorOfSeries<Candle>(result);
		} catch ( Exception e ) {
			throw new DataStorageException("Unexpected exception: ", e);
		}
	}

	@Override
	public CloseableIterator<Candle> createReaderTo(TFSymbol key, Instant to)
			throws DataStorageException
	{
		validateTimeFrame(key);
		to = tframe.getInterval(to).getStart();
		List<SymbolDaily> segments = segstor.listDailySegments(key.getSymbol());
		return segments.size() > 0 ?
			new PreciseTimeLimitsIterator(new DailySegmentsCombiner(segstor, segments), null, to) :
			new CloseableIteratorStub<>();
	}
	
	private void validateTimeFrame(TFSymbol key) throws DataStorageException {
		ZTFrame t = key.getTimeFrame();
		if ( ! tframe.equals(t) ) {
			throw new DataStorageException("Unexpected timeframe: " + t);
		}
	}

	@Override
	public void warmingUpReader(TFSymbol key, int count, Instant to) {
		
	}

}
