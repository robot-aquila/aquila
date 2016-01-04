package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.Tick;

@SuppressWarnings({"unchecked","rawtypes"})
public class TickReaderFilterByTimeTest {
	private IMocksControl control;
	private Aqiterator<Tick> decorated;
	private Instant startTime;
	private TickReaderFilterByTime iterator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		decorated = control.createMock(Aqiterator.class);
		startTime = Instant.parse("2014-10-30T07:58:18Z");
		iterator = new TickReaderFilterByTime(decorated, startTime);
	}
	
	private Tick newTick(String time, double price, long size) {
		return Tick.of(Instant.parse(time), price, size);
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
		list.add(newTick("2014-10-28T15:24:19.000Z", 1.23d, 100));
		list.add(newTick("2014-10-29T00:00:00.000Z", 1.30d, 115));
		list.add(newTick("2014-10-30T07:58:17.999Z", 1.31d, 300));
		list.add(newTick("2014-10-30T07:58:18.000Z", 1.29d, 200));
		list.add(newTick("2014-10-30T14:28:29.175Z", 1.32d, 800));
		list.add(newTick("2015-01-01T00:00:00.000Z", 1.19d,  10));
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
			it2 = new TickReaderFilterByTime(decorated, startTime.plusSeconds(1)),
			it3 = new TickReaderFilterByTime(decorated2, startTime),
			it4 = new TickReaderFilterByTime(decorated2, startTime.plusSeconds(1));
		assertTrue(iterator.equals(iterator));
		assertTrue(iterator.equals(it1));
		assertFalse(iterator.equals(it2));
		assertFalse(iterator.equals(it3));
		assertFalse(iterator.equals(it4));
		assertFalse(iterator.equals(null));
		assertFalse(iterator.equals(this));
	}

}
