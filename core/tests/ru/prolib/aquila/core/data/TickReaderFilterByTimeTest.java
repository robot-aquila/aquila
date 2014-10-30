package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class TickReaderFilterByTimeTest {
	private static final DateTimeFormatter df;
	
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
	}

	private IMocksControl control;
	private Aqiterator<Tick> decorated;
	private DateTime startTime;
	private TickReaderFilterByTime iterator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		decorated = control.createMock(Aqiterator.class);
		startTime = new DateTime(2014, 10, 30, 7, 58, 18, 0);
		iterator = new TickReaderFilterByTime(decorated, startTime);
	}
	
	
	/**
	 * Создать тик.
	 * <p>
	 * @param time время в формате {@link #df}.
	 * @param price цена
	 * @param value объем
	 * @return тик
	 */
	private Tick newTick(String time, double price, double value) {
		return new Tick(df.parseDateTime(time), price, value);
	}
	
	@Test
	public void testClose() throws Exception {
		decorated.close();
		control.replay();
		
		iterator.close();
		
		control.verify();
	}
	
	@Test
	public void testIterate() throws Exception {
		List<Tick> expected = new Vector<Tick>(),
				actual = new Vector<Tick>(),
				list = new Vector<Tick>();
		list.add(newTick("2014-10-28 15:24:19.000", 1.23d, 100));
		list.add(newTick("2014-10-29 00:00:00.000", 1.30d, 115));
		list.add(newTick("2014-10-30 07:58:17.999", 1.31d, 300));
		list.add(newTick("2014-10-30 07:58:18.000", 1.29d, 200));
		list.add(newTick("2014-10-30 14:28:29.175", 1.32d, 800));
		list.add(newTick("2015-01-01 00:00:00.000", 1.19d,  10));
		expected.add(list.get(3));
		expected.add(list.get(4));
		expected.add(list.get(5));
		
		iterator = new TickReaderFilterByTime(new SimpleIterator<Tick>(list), startTime);
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		assertEquals(expected, actual);
	}

	@Test
	public void testEquals() {
		Aqiterator decorated2 = control.createMock(Aqiterator.class);
		TickReaderFilterByTime
			it1 = new TickReaderFilterByTime(decorated, startTime),
			it2 = new TickReaderFilterByTime(decorated, startTime.plus(1)),
			it3 = new TickReaderFilterByTime(decorated2, startTime),
			it4 = new TickReaderFilterByTime(decorated2, startTime.plus(1));
		assertTrue(iterator.equals(iterator));
		assertTrue(iterator.equals(it1));
		assertFalse(iterator.equals(it2));
		assertFalse(iterator.equals(it3));
		assertFalse(iterator.equals(it4));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}

}
