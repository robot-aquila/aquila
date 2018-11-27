package ru.prolib.aquila.data.storage.ohlcv.segstor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;

public class InterdayMDStorageOverSMSS implements MDStorage<TFSymbol, Candle> {
	private final SymbolMonthlySegmentStorage<Candle> smss;
	private final ZTFrame tframe;
	
	public InterdayMDStorageOverSMSS(SymbolMonthlySegmentStorage<Candle> smss,
			ZTFrame tframe)
	{
		this.smss = smss;
		this.tframe = tframe;
	}
	
	public SymbolMonthlySegmentStorage<Candle> getSMSS() {
		return smss;
	}
	
	public ZTFrame getTimeFrame() {
		return tframe;
	}

	@Override
	public Set<TFSymbol> getKeys() throws DataStorageException {
		Set<TFSymbol> result = new HashSet<>();
		for ( Symbol symbol : smss.listSymbols() ) {
			result.add(new TFSymbol(symbol, tframe));
		}
		return result;
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key)
			throws DataStorageException
	{
		validateTimeFrame(key);
		List<SymbolMonthly> segments = smss.listMonthlySegments(key.getSymbol());
		return segments.size() > 0 ?
			new MonthlySegmentsCombiner(smss, segments) :
			new CloseableIteratorStub<>();
	}

	@Override
	public CloseableIterator<Candle> createReaderFrom(TFSymbol key, Instant from)
			throws DataStorageException
	{
		validateTimeFrame(key);
		from = tframe.getInterval(from).getStart();
		LocalDate dateFrom = from.atZone(smss.getZoneID()).toLocalDate();
		List<SymbolMonthly> segments = smss.listMonthlySegments(
				key.getSymbol(),
				new MonthPoint(dateFrom.getYear(), dateFrom.getMonth())
			);
		return segments.size() > 0 ?
			new PreciseTimeLimitsIterator(new MonthlySegmentsCombiner(smss, segments), from, null) : 
			new CloseableIteratorStub<>();
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, int count) 
			throws DataStorageException
	{
		validateTimeFrame(key);
		from = tframe.getInterval(from).getStart();
		LocalDate dateFrom = from.atZone(smss.getZoneID()).toLocalDate();
		List<SymbolMonthly> segments = smss.listMonthlySegments(
				key.getSymbol(),
				new MonthPoint(dateFrom.getYear(), dateFrom.getMonth())
			);
		if ( segments.size() == 0 ) {
			return new CloseableIteratorStub<>(); 
		}
		return new LimitedAmountIterator(new PreciseTimeLimitsIterator(
			new MonthlySegmentsCombiner(smss, segments), from, null), count);
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, Instant to)
			throws DataStorageException
	{
		validateTimeFrame(key);
		//System.out.println("from (before): " + from);
		//System.out.println("  to (before): " + to);
		from = tframe.getInterval(from).getStart();
		to = tframe.getInterval(to).getStart();
		//System.out.println("from ( after): " + from);
		//System.out.println("  to ( after): " + to);
		LocalDate dateFrom = from.atZone(smss.getZoneID()).toLocalDate();
		LocalDate dateTo = to.atZone(smss.getZoneID()).toLocalDate();
		List<SymbolMonthly> segments = smss.listMonthlySegments(
				key.getSymbol(),
				new MonthPoint(dateFrom.getYear(), dateFrom.getMonth()),
				new MonthPoint(dateTo.getYear(), dateTo.getMonth())
			);
		if ( segments.size() == 0 ) {
			return new CloseableIteratorStub<>(); 
		}
		return new PreciseTimeLimitsIterator(
				new MonthlySegmentsCombiner(smss, segments),
				from,
				to
			);
	}

	@Override
	public CloseableIterator<Candle> createReader(TFSymbol key, int count, Instant to)
			throws DataStorageException
	{
		validateTimeFrame(key);
		//System.out.println("to (before): " + to);
		to = tframe.getInterval(to).getStart();
		//System.out.println("to (after): " + to);
		LocalDate dateTo = to.atZone(smss.getZoneID()).toLocalDate();
		//System.out.println("dateTo: " + dateTo);
		LinkedList<SymbolMonthly> segments =
			new LinkedList<>(smss.listMonthlySegments(
				key.getSymbol(),
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(dateTo.getYear(), dateTo.getMonth())
			));
		if ( segments.size() == 0 ) {
			return new CloseableIteratorStub<>();
		}
		EditableTSeries<Candle> result = new TSeriesImpl<>(tframe);
		SymbolMonthly segment = segments.removeLast();
		try {
			CloseableIterator<Candle> iterator = smss.createReader(segment);
			while ( iterator.next() ) {
				Candle x = iterator.item();
				Instant xTime = x.getStartTime();
				//System.out.println("candle time: " + xTime);
				if ( xTime.compareTo(to) >= 0 ) {
					//System.out.println("last (exclude)");
					break;
				}
				result.set(xTime, x);
			}
			iterator.close();
			while ( result.getLength() < count && segments.size() > 0 ) {
				segment = segments.removeLast();
				iterator = smss.createReader(segment);
				while ( iterator.next() ) {
					Candle x = iterator.item();
					result.set(x.getStartTime(), x);
				}
				iterator.close();
			}
			CloseableIterator<Candle> rit = new CloseableIteratorOfSeries<Candle>(result);
			int rlen = result.getLength();
			if ( rlen > count ) {
				Instant startTime = result.get(rlen - count).getStartTime();
				rit = new PreciseTimeLimitsIterator(rit, startTime, null);
			}
			return rit;
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
		LocalDate dateTo = to.atZone(smss.getZoneID()).toLocalDate();
		List<SymbolMonthly> segments = smss.listMonthlySegments(
				key.getSymbol(),
				new MonthPoint(0, Month.JANUARY),
				new MonthPoint(dateTo.getYear(), dateTo.getMonth())
			);
		if ( segments.size() == 0 ) {
			return new CloseableIteratorStub<>(); 
		}
		return new PreciseTimeLimitsIterator(
				new MonthlySegmentsCombiner(smss, segments),
				null,
				to
			);
	}
	
	private void validateTimeFrame(TFSymbol key) throws DataStorageException {
		ZTFrame t = key.getTimeFrame();
		if ( ! tframe.equals(t) ) {
			throw new DataStorageException("Unexpected timeframe: " + t);
		}
	}

}
