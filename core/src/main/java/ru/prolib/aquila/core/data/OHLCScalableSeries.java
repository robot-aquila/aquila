package ru.prolib.aquila.core.data;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.timeframe.ZTFDays;
import ru.prolib.aquila.core.data.timeframe.ZTFHours;
import ru.prolib.aquila.core.data.timeframe.ZTFMinutes;
import ru.prolib.aquila.core.data.tseries.TSeriesUpdateEventFactory;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesAggregator;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesCandleAggregator;

public class OHLCScalableSeries implements ObservableTSeries<Candle> {
	private static final CDecimal ONE = CDecimalBD.of(1L);
	
	public static List<ZTFrame> stdScaleDefList(ZoneId zone_id) {
		List<ZTFrame> list = new ArrayList<>();
		list.add(new ZTFMinutes( 1, zone_id));
		list.add(new ZTFMinutes( 5, zone_id));
		list.add(new ZTFMinutes(15, zone_id));
		list.add(new ZTFMinutes(30, zone_id));
		list.add(new ZTFHours( 1, zone_id));
		list.add(new ZTFHours( 3, zone_id));
		list.add(new ZTFHours( 6, zone_id));
		list.add(new ZTFHours(12, zone_id));
		list.add(new ZTFDays(1, zone_id));
		return list;
	}

	private final EventQueue queue;
	private final String id;
	private final LID lid;
	private final Lock lock;
	private final EventType onUpdate, onLengthUpdate;
	private final List<ZTFrame> scaleDefList;
	private final int maxCount;
	private TSeriesImpl<Candle> currSeries;
	private ZTFrame currTF;
	private int currScaleIndex = 0;
	private int currScaleMultiplier = 1;
	
	public OHLCScalableSeries(EventQueue queue,
							  String id,
							  int max_count,
							  List<ZTFrame> scale_def_list)
	{
		this.queue = queue;
		this.id = id;
		this.lid = LID.createInstance();
		this.lock = new ReentrantLock();
		this.onUpdate = new EventTypeImpl(id + ".UPDATE");
		this.onLengthUpdate = new EventTypeImpl(id + ".LENGTH_UPDATE");
		this.maxCount = max_count;
		this.scaleDefList = scale_def_list;
		this.currSeries = new TSeriesImpl<Candle>(currTF = scaleDefList.get(currScaleIndex));
	}
	
	public OHLCScalableSeries(EventQueue queue,
							  String id,
							  int max_count,
							  ZoneId zone_id)
	{
		this(queue, id, max_count, stdScaleDefList(zone_id));
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}
	
	public int getMaxCount() {
		return maxCount;
	}
	
	/**
	 * Combine OHLC entries using bigger time frame.
	 */
	private void compact() {
		currScaleIndex ++;
		if ( currScaleIndex >= scaleDefList.size() ) {
			currScaleMultiplier *= 2;
			currTF = new ZTFDays(currScaleMultiplier, currTF.getZoneID());
		} else {
			currTF = scaleDefList.get(currScaleIndex);
		}
		TSeriesImpl<Candle> prev_series = currSeries;
		currSeries = new TSeriesImpl<>(currTF);
		CandleSeriesAggregator<Candle> csca = CandleSeriesCandleAggregator.getInstance();
		prev_series.lock();
		try {
			int prev_length = prev_series.getLength();
			for ( int i = 0; i < prev_length; i ++ ) {
				csca.aggregate(currSeries, prev_series.get(i));
			}
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		} finally {
			prev_series.unlock();
		}
	}
	
	public void append(CDecimal value, Instant at_time) {
		lock();
		try {
			Candle c = currSeries.get(at_time);
			if ( c == null ) {
				c = new CandleBuilder()
						.withTimeFrame(currTF)
						.withTime(at_time)
						.withOpenPrice(value)
						.withHighPrice(value)
						.withLowPrice(value)
						.withClosePrice(value)
						.withVolume(1L)
						.buildCandle();
			} else {
				c = c.addDeal(value, ONE);
			}
			int prev_length = currSeries.getLength();
			TSeriesUpdate update = currSeries.set(at_time, c);
			int curr_length = currSeries.getLength();
			if ( curr_length > maxCount ) {
				compact();
				curr_length = currSeries.getLength();
				queue.enqueue(onLengthUpdate, new LengthUpdateEventFactory(prev_length, curr_length));
			} else {
				queue.enqueue(onUpdate, new TSeriesUpdateEventFactory(update));
				if ( prev_length != curr_length ) {
					queue.enqueue(onLengthUpdate, new LengthUpdateEventFactory(prev_length, curr_length));
				}
			}
		} finally {
			unlock();
		}
	}

	@Override
	public ZTFrame getTimeFrame() {
		lock();
		try {
			return currTF;
		} finally {
			unlock();
		}
	}

	@Override
	public Candle get(Instant key) {
		lock();
		try {
			return currSeries.get(key);
		} finally {
			unlock();
		}
	}

	@Override
	public int toIndex(Instant key) {
		lock();
		try {
			return currSeries.toIndex(key);
		} finally {
			unlock();
		}
	}

	@Override
	public Instant toKey(int index) throws ValueException {
		lock();
		try {
			return currSeries.toKey(index);
		} finally {
			unlock();
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Candle get() throws ValueException {
		lock();
		try {
			return currSeries.get();
		} finally {
			unlock();
		}
	}

	@Override
	public Candle get(int index) throws ValueException {
		lock();
		try {
			return currSeries.get(index);
		} finally {
			unlock();
		}
	}

	@Override
	public int getLength() {
		lock();
		try {
			return currSeries.getLength();
		} finally {
			unlock();
		}
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public EventType onUpdate() {
		return onUpdate;
	}

	@Override
	public EventType onLengthUpdate() {
		return onLengthUpdate;
	}
	
	@Override
	public Candle getFirstBefore(Instant time) {
		return currSeries.getFirstBefore(time);
	}

	@Override
	public int getFirstIndexBefore(Instant time) {
		return currSeries.getFirstIndexBefore(time);
	}

}
