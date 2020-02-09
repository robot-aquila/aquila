package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueFactory;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.core.SimpleEventFactory;
import ru.prolib.aquila.core.data.timeframe.ZTFDays;
import ru.prolib.aquila.core.data.timeframe.ZTFHours;
import ru.prolib.aquila.core.data.timeframe.ZTFMinutes;
import ru.prolib.aquila.core.data.tseries.TSeriesEventImpl;
import ru.prolib.aquila.core.data.tseries.TSeriesUpdateImpl;

public class OHLCScalableSeriesTest {
	private static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	
	interface EventExpectation extends EventListener {
		void await() throws InterruptedException, TimeoutException;
		void await(long millis) throws InterruptedException, TimeoutException;
		void close();
	}
	
	static class ExpectEvent implements EventExpectation {
		private final Event expected;
		private final CountDownLatch finished = new CountDownLatch(1);
		
		public ExpectEvent(Event expected) {
			this.expected = expected;
		}

		@Override
		public void onEvent(Event event) {
			if ( expected.equals(event) ) {
				finished.countDown();
				close();
			}
		}
		
		@Override
		public void await(long millis) throws InterruptedException, TimeoutException {
			if ( ! finished.await(millis, TimeUnit.MILLISECONDS) ) {
				throw new TimeoutException();
			}
		}
		
		@Override
		public void await() throws InterruptedException, TimeoutException {
			await(1000L);
		}
		
		@Override
		public void close() {
			expected.getType().removeListener(this);
		}
		
	}
	
	static class ExpectNoEvents implements EventExpectation {
		private final EventQueue queue;
		private final EventType type, myType;
		private final CountDownLatch finished;
		private Event unexpected_event;
		
		public ExpectNoEvents(EventQueue queue, EventType no_events_type) {
			this.queue = queue;
			this.type = no_events_type;
			this.myType = new EventTypeImpl();
			this.finished = new CountDownLatch(1);
		}
		
		@Override
		public void onEvent(Event event) {
			if ( event.isType(type) ) {
				synchronized ( this ) {
					unexpected_event = event;
				}
			} else if ( event.isType(myType) ) {
				finished.countDown();
			}
			close();
		}
		
		private void checkEvents() {
			synchronized ( this ) {
				if ( unexpected_event != null ) {
					throw new IllegalStateException("Unexpected event: " + unexpected_event);
				}
			}			
		}

		@Override
		public void await(long millis) throws InterruptedException, TimeoutException {
			checkEvents();
			myType.addListener(this);
			queue.enqueue(myType, SimpleEventFactory.getInstance());
			boolean no_timeout = finished.await(millis, TimeUnit.MILLISECONDS);
			checkEvents();
			if ( ! no_timeout ) {
				throw new TimeoutException();
			}
		}

		@Override
		public void await() throws InterruptedException, TimeoutException {
			await(1000L);
		}

		@Override
		public void close() {
			type.removeListener(this);
			myType.removeListener(this);
		}

	}
	
	static Instant T(String timeString) {
		return LocalDateTime.parse(timeString).atZone(ZONE_ID).toInstant();
	}
	
	static Candle OHLCV(ZTFrame tf,
						String timeString,
						String open,
						String high,
						String low,
						String close,
						int volume)
	{
		return new CandleBuilder()
				.withTimeFrame(tf)
				.withTime(T(timeString))
				.withOpenPrice(open)
				.withHighPrice(high)
				.withLowPrice(low)
				.withClosePrice(close)
				.withVolume(volume)
				.buildCandle();
	}
	
	

	
	static Candle OHLCV(ZTFrame tf,
						String timeString,
						String price_all,
						int volume)
	{
		return OHLCV(tf, timeString, price_all, price_all, price_all, price_all, volume);
	}
	
	static Candle OHLCV(ZTFrame tf,
			String timeString,
			String price_all)
	{
		return OHLCV(tf, timeString, price_all, 1);
	}
	
	private EventQueue queue;
	private OHLCScalableSeries service;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueFactory().createDefault();
		service = new OHLCScalableSeries(queue, "foobar", 5, ZONE_ID);
	}
	
	@After
	public void tearDown() throws Exception {
		queue.shutdown();
	}
	
	private EventExpectation expectUpdate(Instant start_time,
			   ZTFrame tf,
			   int node_index,
			   Object old_value,
			   Object new_value,
			   boolean is_new_node)
	{
		TSeriesUpdate update = new TSeriesUpdateImpl(tf.getInterval(start_time))
			.setNodeIndex(node_index)
			.setOldValue(old_value)
			.setNewValue(new_value)
			.setNewNode(is_new_node);
		return new ExpectEvent(new TSeriesEventImpl<Candle>(service.onUpdate(), update));
	}
	
	private EventExpectation expectLengthUpdate(int prev_length, int curr_length) {
		return new ExpectEvent(new LengthUpdateEvent(service.onLengthUpdate(), prev_length, curr_length));
	}
	
	private EventExpectation expectNoLengthUpdateEvents() {
		return new ExpectNoEvents(queue, service.onLengthUpdate());
	}
	
	private EventExpectation expectNoUpdateEvents() {
		return new ExpectNoEvents(queue, service.onUpdate());
	}
	
	private void assertSeriesEquals(String msg_prefix, List<Candle> expected_list) throws Exception {
		if ( msg_prefix == null ) {
			msg_prefix = "";
		} else {
			msg_prefix = msg_prefix + " ";
		}
		assertEquals(msg_prefix + "series length mismatch", expected_list.size(), service.getLength());
		for ( int i = 0; i < expected_list.size(); i ++ ) {
			Candle expected = expected_list.get(i), actual = service.get(i);
			assertEquals(msg_prefix + "At #" + i, expected, actual);
		}
	}
	
	private void fillDataSet1() {
		service.append(of("12340.00"), T("2019-03-25T18:58:00"));
		service.append(of("11420.00"), T("2019-03-25T18:58:05"));
		service.append(of("13250.13"), T("2019-03-25T18:58:10"));
		service.append(of("10012.00"), T("2019-03-25T18:58:15")); // OHLCV: 12340.00 13250.13 10012.00 10012.00 4
		service.append(of("24930.07"), T("2019-03-25T18:59:00"));
		service.append(of("19508.34"), T("2019-03-25T18:59:10"));
		service.append(of("23750.98"), T("2019-03-25T18:59:15")); // OHLCV: 24930.07 24930.07 19508.34 23750.98 3
		service.append(of("13486.05"), T("2019-03-25T19:00:00"));
		service.append(of("11202.12"), T("2019-03-25T19:00:01"));
		service.append(of("10908.71"), T("2019-03-25T19:00:02"));
		service.append(of("10107.18"), T("2019-03-25T19:00:03"));
		service.append(of("12400.47"), T("2019-03-25T19:00:04")); // OHLCV: 13486.05 13486.05 10107.18 12400.47 5
	}
	
	@Test
	public void testCtor4_ZoneId() {
		assertSame(queue, service.getEventQueue());
		assertEquals("foobar", service.getId());
		assertEquals(5, service.getMaxCount());
		assertEquals(new ZTFMinutes(1, ZONE_ID), service.getTimeFrame());
		// This does not work cuz the child series created its own LID
		//assertTrue(LID.isLastCreatedLID(service.getLID())); 
		assertEquals("foobar.UPDATE", service.onUpdate().getId());
		assertEquals("foobar.LENGTH_UPDATE", service.onLengthUpdate().getId());
	}
	
	@Test
	public void testAppend() throws Exception {
		EventExpectation expect_event1, expect_event2;
		EventType on_update = service.onUpdate(), on_length_update = service.onLengthUpdate();
		ZTFrame tf;
		Candle curr, prev;
		List<Candle>  expected_list = new ArrayList<>();
		tf = new ZTFMinutes(1, ZONE_ID);
		
		// --------------------------------------------------------------------
		// Some tests of aggregation at lower level - inside 1 minute time frame
		
		// new tick
		curr = OHLCV(tf, "2019-03-25T18:58:00", "12340.00");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T18:58:00"), tf, 0, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(0, 1));
		
		service.append(of("12340.00"), T("2019-03-25T18:58:00"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[0.01] first tick to first candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T18:58:00", "12340.00", "12340.00", "11420.00", "11420.00", 2);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T18:58:00"), tf, 0, prev, curr, false));
		on_length_update.addListener(expect_event2 = expectNoLengthUpdateEvents());
		
		service.append(of("11420.00"), T("2019-03-25T18:58:05"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(0, curr);
		assertSeriesEquals("[0.02] second tick to first candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T18:58:00", "12340.00", "13250.13", "11420.00", "13250.13", 3);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T18:58:00"), tf, 0, prev, curr, false));
		on_length_update.addListener(expect_event2 = expectNoLengthUpdateEvents());

		service.append(of("13250.13"), T("2019-03-25T18:58:10"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(0, curr);
		assertSeriesEquals("[0.03] third tick to first candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T18:58:00", "12340.00", "13250.13", "10012.00", "10012.00", 4);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T18:58:00"), tf, 0, prev, curr, false));
		on_length_update.addListener(expect_event2 = expectNoLengthUpdateEvents());
		
		service.append(of("10012.00"), T("2019-03-25T18:58:15"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(0, curr);
		assertSeriesEquals("[0.04] fourth tick to first candle", expected_list);
		
		// new tick
		prev = null;
		curr = OHLCV(tf, "2019-03-25T18:59:00", "24930.07");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T18:59:00"), tf, 1, null, curr, true));
		on_length_update.addListener(expectLengthUpdate(1, 2));
		
		service.append(of("24930.07"), T("2019-03-25T18:59:00"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[0.05] fifth tick and NEW candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T18:59:00", "24930.07", "24930.07", "19508.34", "19508.34", 2);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T18:59:00"), tf, 1, prev, curr, false));
		on_length_update.addListener(expect_event2 = expectNoLengthUpdateEvents());
		
		service.append(of("19508.34"), T("2019-03-25T18:59:10"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(1, curr);
		assertSeriesEquals("[0.06] sixth tick to second candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T18:59:00", "24930.07", "24930.07", "19508.34", "23750.98", 3);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T18:59:00"), tf, 1, prev, curr, false));
		on_length_update.addListener(expect_event2 = expectNoLengthUpdateEvents());
		
		service.append(of("23750.98"), T("2019-03-25T18:59:15"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(1, curr);
		assertSeriesEquals("[0.07] seventh tick to second candle", expected_list);
		
		// new tick
		prev = null;
		curr = OHLCV(tf, "2019-03-25T19:00:00", "13486.05");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:00:00"), tf, 2, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(2, 3));
		
		service.append(of("13486.05"), T("2019-03-25T19:00:00"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[0.08] eighth tick and NEW third candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "13486.05", "11202.12", "11202.12", 2);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:00:00"), tf, 2, prev, curr, false));
		on_length_update.addListener(expectNoLengthUpdateEvents());
		
		service.append(of("11202.12"), T("2019-03-25T19:00:01"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(2, curr);
		assertSeriesEquals("[0.09] ninth tick to third candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "13486.05", "10908.71", "10908.71", 3);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:00:00"), tf, 2, prev, curr, false));
		on_length_update.addListener(expectNoLengthUpdateEvents());
		
		service.append(of("10908.71"), T("2019-03-25T19:00:02"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(2, curr);
		assertSeriesEquals("[0.10] tenth tick to third candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "13486.05", "10107.18", "10107.18", 4);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:00:00"), tf, 2, prev, curr, false));
		on_length_update.addListener(expectNoLengthUpdateEvents());
		
		service.append(of("10107.18"), T("2019-03-25T19:00:03"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(2, curr);
		assertSeriesEquals("[0.11] eleventh tick to third candle", expected_list);
		
		// new tick
		prev = curr;
		curr = OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "13486.05", "10107.18", "12400.47", 5);
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:00:00"), tf, 2, prev, curr, false));
		on_length_update.addListener(expectNoLengthUpdateEvents());

		service.append(of("12400.47"), T("2019-03-25T19:00:04"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.set(2, curr);
		assertSeriesEquals("[0.12] twelfth tick to third candle", expected_list);
		
		// --------------------------------------------------------------------
		// Now let's test how it will go up to next level - the 5 minutes time frame
		
		// new tick, M1 candle #3
		prev = null;
		curr = OHLCV(tf, "2019-03-25T19:02:00", "28441.15");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:02:00"), tf, 3, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(3, 4));
		
		service.append(of("28441.15"), T("2019-03-25T19:02:00"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[1.01] to grow up M1 series up to 4 elements", expected_list);
		
		// new tick, M1 candle #4. Next candle should cause series compaction
		prev = null;
		curr = OHLCV(tf, "2019-03-25T19:07:00", "17220.02");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:07:00"), tf, 4, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(4, 5));
		
		service.append(of("17220.02"), T("2019-03-25T19:07:12.051"));

		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[1.02] to grow up M1 series up to 5 elements", expected_list);
		
		// new tick, M1 candle #5. Must cause series compaction
		tf = new ZTFMinutes(5, ZONE_ID);
		prev = curr = null;
		//curr = OHLCV(tf, "2019-03-25T19:05:00", "12519.28");
		on_update.addListener(expect_event1 = expectNoUpdateEvents());
		on_length_update.addListener(expect_event2 = expectLengthUpdate(5, 3));

		service.append(of("12519.28"), T("2019-03-25T19:08:52"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T18:55:00", "12340.00", "24930.07", "10012.00", "23750.98", 7));
		expected_list.add(OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "28441.15", "10107.18", "28441.15", 6));
		expected_list.add(OHLCV(tf, "2019-03-25T19:05:00", "17220.02", "17220.02", "12519.28", "12519.28", 2));
		assertSeriesEquals("[1.03] to cause M1 series compact up to M5", expected_list);
		
		// --------------------------------------------------------------------
		// Let's go up to M15. Need two more entries.
		
		// new tick
		prev = null;
		curr = OHLCV(tf, "2019-03-25T19:15:00", "16505.44");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:15:00"), tf, 3, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(3, 4));
		
		service.append(of("16505.44"), T("2019-03-25T19:18:47"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[2.01] to grow M5 series up to 4 elements", expected_list);
		
		// new tick
		prev = null;
		curr = OHLCV(tf, "2019-03-25T19:30:00", "21013.00");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T19:30:00"), tf, 4, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(4, 5));
		
		service.append(of("21013.00"), T("2019-03-25T19:32:11.402"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[2.02] to grow M5 series up to 5 elements", expected_list);
		
		// New tick. Must cause series compacting.
		// BTW. This is special case: time frame was changed but length is not.
		// Yes. That's look strange but OK.
		// Next candlestick will cause switch to next time frame.
		tf = new ZTFMinutes(15, ZONE_ID);
		prev = curr = null;
		on_update.addListener(expect_event1 = expectNoUpdateEvents());
		on_length_update.addListener(expect_event2 = expectLengthUpdate(5, 5)); 
		
		service.append(of("17000.99"), T("2019-03-25T19:48:02.212"));

		expect_event1.await();
		expect_event2.await();
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T18:45:00", "12340.00", "24930.07", "10012.00", "23750.98", 7));
		expected_list.add(OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "28441.15", "10107.18", "12519.28", 8));
		expected_list.add(OHLCV(tf, "2019-03-25T19:15:00", "16505.44"));
		expected_list.add(OHLCV(tf, "2019-03-25T19:30:00", "21013.00"));
		expected_list.add(OHLCV(tf, "2019-03-25T19:45:00", "17000.99"));
		assertSeriesEquals("[2.03] to cause M5 series compact up to M15", expected_list);
	
		// --------------------------------------------------------------------
		// Go to M30
		
		// New tick
		tf = new ZTFMinutes(30, ZONE_ID);
		prev = curr = null;
		on_update.addListener(expect_event1 = expectNoUpdateEvents());
		on_length_update.addListener(expect_event2 = expectLengthUpdate(5, 4));
		
		service.append(of("13426.19"), T("2019-03-25T20:01:12.407"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T18:30:00", "12340.00", "24930.07", "10012.00", "23750.98", 7));
		expected_list.add(OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "28441.15", "10107.18", "16505.44", 9));
		expected_list.add(OHLCV(tf, "2019-03-25T19:30:00", "21013.00", "21013.00", "17000.99", "17000.99", 2));
		expected_list.add(OHLCV(tf, "2019-03-25T20:00:00", "13426.19"));
		assertSeriesEquals("[3.01] to cause M15 series compact up to M30", expected_list);
		
		// --------------------------------------------------------------------
		// Go to H1
		
		// New tick
		prev = null;
		curr = OHLCV(tf, "2019-03-25T20:30:00", "15409.26");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T20:30:00"), tf, 4, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(4, 5));
		
		service.append(of("15409.26"), T("2019-03-25T20:36:19.150"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[4.01] to grow M30 series up to 5 elements", expected_list);

		// New tick
		tf = new ZTFHours(1, ZONE_ID);
		prev = curr = null;
		on_update.addListener(expect_event1 = expectNoUpdateEvents());
		on_length_update.addListener(expect_event2 = expectLengthUpdate(5, 4));
		
		service.append(of("13002.93"), T("2019-03-25T21:01:12.504"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T18:00:00", "12340.00", "24930.07", "10012.00", "23750.98",  7));
		expected_list.add(OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "28441.15", "10107.18", "17000.99", 11));
		expected_list.add(OHLCV(tf, "2019-03-25T20:00:00", "13426.19", "15409.26", "13426.19", "15409.26",  2));
		expected_list.add(OHLCV(tf, "2019-03-25T21:00:00", "13002.93"));
		assertSeriesEquals("[4.02] to cause M30 series compact up to H1", expected_list);
		
		// --------------------------------------------------------------------
		// Go to H3
		
		// New tick
		prev = null;
		curr = OHLCV(tf, "2019-03-25T23:00:00", "12209.15");
		on_update.addListener(expect_event1 = expectUpdate(T("2019-03-25T23:00:00"), tf, 4, null, curr, true));
		on_length_update.addListener(expect_event2 = expectLengthUpdate(4, 5));
		
		service.append(of("12209.15"), T("2019-03-25T23:19:26.771"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.add(curr);
		assertSeriesEquals("[5.01] to grow H1 up to 5 elements", expected_list);
		
		// New tick
		tf = new ZTFHours(3, ZONE_ID);
		prev = curr = null;
		on_update.addListener(expect_event1 = expectNoUpdateEvents());
		on_length_update.addListener(expect_event2 = expectLengthUpdate(5, 3));
		
		service.append(of("14662.99"), T("2019-03-26T12:15:26"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T18:00:00", "12340.00", "28441.15", "10012.00", "15409.26", 20));
		expected_list.add(OHLCV(tf, "2019-03-25T21:00:00", "13002.93", "13002.93", "12209.15", "12209.15",  2));
		expected_list.add(OHLCV(tf, "2019-03-26T12:00:00", "14662.99"));
		assertSeriesEquals("[5.02] to cause H1 series compact up to H3", expected_list);
		
		// --------------------------------------------------------------------
		// Go to H6
		
		FlushIndicator fi = queue.newFlushIndicator();
		fi.start();
		service.append(of("17205.00"), T("2019-03-26T15:28:36"));
		service.append(of("18557.97"), T("2019-03-26T18:36:27"));
		fi.waitForFlushing(1, TimeUnit.SECONDS);
		
		tf = new ZTFHours(6, ZONE_ID);
		on_update.addListener(expect_event1 = expectNoUpdateEvents());
		on_length_update.addListener(expect_event2 = expectLengthUpdate(5, 3));
		
		service.append(of("16442.22"), T("2019-03-26T22:19:44"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T18:00:00", "12340.00", "28441.15", "10012.00", "12209.15", 22));
		expected_list.add(OHLCV(tf, "2019-03-26T12:00:00", "14662.99", "17205.00", "14662.99", "17205.00",  2));
		expected_list.add(OHLCV(tf, "2019-03-26T18:00:00", "18557.97", "18557.97", "16442.22", "16442.22",  2));
		assertSeriesEquals("[6.01] to cause H3 series compact up to H6", expected_list);
		
		// --------------------------------------------------------------------
		// Go to H12
		
		fi = queue.newFlushIndicator();
		fi.start();
		service.append(of("11404.00"), T("2019-03-27T14:26:24"));
		service.append(of("29190.00"), T("2019-03-27T21:19:03"));
		fi.waitForFlushing(1, TimeUnit.SECONDS);
		
		tf = new ZTFHours(12, ZONE_ID);
		
		service.append(of("19015.00"), T("2019-03-28T11:45:00"));
		
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T12:00:00", "12340.00", "28441.15", "10012.00", "12209.15", 22));
		expected_list.add(OHLCV(tf, "2019-03-26T12:00:00", "14662.99", "18557.97", "14662.99", "16442.22",  4));
		expected_list.add(OHLCV(tf, "2019-03-27T12:00:00", "11404.00", "29190.00", "11404.00", "29190.00",  2));
		expected_list.add(OHLCV(tf, "2019-03-28T00:00:00", "19015.00"));
		assertSeriesEquals("[7.01] to cause H6 series compact up to H12", expected_list);

		// --------------------------------------------------------------------
		// Go to D1
		
		fi = queue.newFlushIndicator();
		fi.start();
		service.append(of("22515.27"), T("2019-03-28T23:01:12"));
		fi.waitForFlushing(1, TimeUnit.SECONDS);
		
		tf = new ZTFDays(1, ZONE_ID);
		on_update.addListener(expect_event1 = expectNoUpdateEvents());
		on_length_update.addListener(expect_event2 = expectLengthUpdate(5, 5));
		
		service.append(of("25500.00"), T("2019-03-29T01:12:26"));
		
		expect_event1.await();
		expect_event2.await();
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-25T00:00:00", "12340.00", "28441.15", "10012.00", "12209.15", 22));
		expected_list.add(OHLCV(tf, "2019-03-26T00:00:00", "14662.99", "18557.97", "14662.99", "16442.22",  4));
		expected_list.add(OHLCV(tf, "2019-03-27T00:00:00", "11404.00", "29190.00", "11404.00", "29190.00",  2));
		expected_list.add(OHLCV(tf, "2019-03-28T00:00:00", "19015.00", "22515.27", "19015.00", "22515.27",  2));
		expected_list.add(OHLCV(tf, "2019-03-29T00:00:00", "25500.00"));
		assertSeriesEquals("[8.01] to cause H12 series compact up to D1", expected_list);
		
		// --------------------------------------------------------------------
		// Go to D2
		
		tf = new ZTFDays(2, ZONE_ID);
		
		fi = queue.newFlushIndicator();
		fi.start();
		service.append(of("18902.12"), T("2019-04-01T00:12:23"));
		fi.waitForFlushing(1, TimeUnit.SECONDS);
		
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-24T00:00:00", "12340.00", "28441.15", "10012.00", "12209.15", 22));
		expected_list.add(OHLCV(tf, "2019-03-26T00:00:00", "14662.99", "29190.00", "11404.00", "29190.00",  6));
		expected_list.add(OHLCV(tf, "2019-03-28T00:00:00", "19015.00", "25500.00", "19015.00", "25500.00",  3));
		expected_list.add(OHLCV(tf, "2019-04-01T00:00:00", "18902.12"));
		assertSeriesEquals("[9.01] to cause D1 series compact up to D2", expected_list);

		// --------------------------------------------------------------------
		// Go to D4
		
		tf = new ZTFDays(4, ZONE_ID);
		
		fi = queue.newFlushIndicator();
		fi.start();
		service.append(of("14803.00"), T("2019-04-03T12:17:26"));
		service.append(of("22551.00"), T("2019-04-05T05:25:00"));
		fi.waitForFlushing(1, TimeUnit.SECONDS);
		
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-22T00:00:00", "12340.00", "28441.15", "10012.00", "12209.15", 22));
		expected_list.add(OHLCV(tf, "2019-03-26T00:00:00", "14662.99", "29190.00", "11404.00", "25500.00",  9));
		expected_list.add(OHLCV(tf, "2019-03-30T00:00:00", "18902.12"));
		expected_list.add(OHLCV(tf, "2019-04-03T00:00:00", "14803.00", "22551.00", "14803.00", "22551.00",  2));
		assertSeriesEquals("[10.01] to cause D2 series compact up to D4", expected_list);
		
		// --------------------------------------------------------------------
		// Go to D8
		
		tf = new ZTFDays(8, ZONE_ID);
		
		fi = queue.newFlushIndicator();
		fi.start();
		service.append(of("17209.00"), T("2019-04-08T15:29:01"));
		service.append(of("11208.00"), T("2019-04-12T02:08:37"));
		fi.waitForFlushing(1, TimeUnit.SECONDS);
		
		expected_list.clear();
		expected_list.add(OHLCV(tf, "2019-03-22T00:00:00", "12340.00", "29190.00", "10012.00", "25500.00", 31));
		expected_list.add(OHLCV(tf, "2019-03-30T00:00:00", "18902.12", "22551.00", "14803.00", "22551.00",  3));
		expected_list.add(OHLCV(tf, "2019-04-07T00:00:00", "17209.00", "17209.00", "11208.00", "11208.00",  2));
		assertSeriesEquals("[11.01] to cause D4 series compact up to D8", expected_list);
	}
	
	@Test
	public void testToIndex() {
		fillDataSet1();
		
		assertEquals(-1, service.toIndex(T("1972-12-07T14:49:26.102")));
		assertEquals(0, service.toIndex(T("2019-03-25T18:58:00")));
		assertEquals(0, service.toIndex(T("2019-03-25T18:58:09")));
		assertEquals(0, service.toIndex(T("2019-03-25T18:58:17")));
		assertEquals(0, service.toIndex(T("2019-03-25T18:58:47")));
		assertEquals(0, service.toIndex(T("2019-03-25T18:58:59.999")));
		assertEquals(1, service.toIndex(T("2019-03-25T18:59:00")));
		assertEquals(1, service.toIndex(T("2019-03-25T18:59:00.001")));
		assertEquals(1, service.toIndex(T("2019-03-25T18:59:59.999")));
		assertEquals(2, service.toIndex(T("2019-03-25T19:00:00")));
	}
	
	@Test
	public void testToKey() throws Exception {
		fillDataSet1();
		
		assertEquals(T("2019-03-25T18:58:00"), service.toKey(0));
		assertEquals(T("2019-03-25T18:59:00"), service.toKey(1));
		assertEquals(T("2019-03-25T19:00:00"), service.toKey(2));
		assertEquals(T("2019-03-25T18:59:00"), service.toKey(-1));
		assertEquals(T("2019-03-25T18:58:00"), service.toKey(-2));
	}
	
	@Test
	public void testGetFirstBefore() throws Exception {
		fillDataSet1();
		ZTFrame tf = service.getTimeFrame();
		
		assertNull(service.getFirstBefore(T("2019-03-24T00:00:00")));
		assertEquals(
			OHLCV(tf, "2019-03-25T18:58:00", "12340.00", "13250.13", "10012.00", "10012.00", 4),
			service.getFirstBefore(T("2019-03-25T18:59:00"))
		);
		assertEquals(
			OHLCV(tf, "2019-03-25T18:59:00", "24930.07", "24930.07", "19508.34", "23750.98", 3),
			service.getFirstBefore(T("2019-03-25T19:00:00"))
		);
		assertEquals(
			OHLCV(tf, "2019-03-25T19:00:00", "13486.05", "13486.05", "10107.18", "12400.47", 5),
			service.getFirstBefore(T("2019-03-25T20:00:00"))
		);
	}
	
	@Test
	public void testGetFirstIndexBefore() {
		fillDataSet1();
		
		assertEquals(-1, service.getFirstIndexBefore(T("2019-03-24T00:00:00")));
		assertEquals( 0, service.getFirstIndexBefore(T("2019-03-25T18:59:00")));
		assertEquals( 1, service.getFirstIndexBefore(T("2019-03-25T19:00:00")));
		assertEquals( 2, service.getFirstIndexBefore(T("2019-03-25T19:01:00")));
		assertEquals( 2, service.getFirstIndexBefore(T("2019-03-25T20:00:00")));
	}

}
