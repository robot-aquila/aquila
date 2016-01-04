package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class SimpleIteratorTest {
	
	public Instant time(String time) throws Exception {
		return Instant.parse(time);
	}
	
	/**
	 * Get expected tick-list.
	 * <p>
	 * @return
	 * @throws Exception
	 */
	public List<Tick> getExpectedAsList() throws Exception {
		List<Tick> ticks = new LinkedList<Tick>();
		ticks.add(Tick.of(time("2014-06-18T09:59:59Z"), 144.79, 250));
		ticks.add(Tick.of(time("2014-06-18T18:34:20Z"), 148.79,   5));
		ticks.add(Tick.of(time("2014-06-18T18:44:15Z"), 141.79,  10));
		ticks.add(Tick.of(time("2014-06-19T10:00:00Z"), 154.98,   1));
		ticks.add(Tick.of(time("2014-06-19T15:00:00Z"), 154.80, 500));
		ticks.add(Tick.of(time("2014-06-25T10:00:01Z"), 148.70, 300));
		ticks.add(Tick.of(time("2014-06-25T10:00:02Z"), 147.70,   1));
		return ticks;
	}
	
	public Aqiterator<Tick> getExpectedAsReader() throws Exception {
		return new SimpleIterator<Tick>(getExpectedAsList());
	}

	@Test
	public void testRead() throws Exception {
		List<Tick> expected = getExpectedAsList(),
				actual = new LinkedList<Tick>();
		Aqiterator<Tick> reader = getExpectedAsReader();
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRead_Closed() throws Exception {
		List<Tick> expected = new LinkedList<Tick>(),
				actual = new LinkedList<Tick>();
		
		Aqiterator<Tick> reader = getExpectedAsReader();
		reader.close();
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConstruct0() throws Exception {
		List<Tick> expected = new LinkedList<Tick>(),
				actual = new LinkedList<Tick>();
		
		Aqiterator<Tick> reader = new SimpleIterator<Tick>();
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		assertEquals(expected, actual);
	}
	
	@Test (expected=DataException.class)
	public void testCurrent_ThrowsIfBeforeStart() throws Exception {
		new SimpleIterator<Tick>().item();
	}
	
	@Test (expected=DataException.class)
	public void testCurrent_ThrowsIfAfterEnd() throws Exception {
		Aqiterator<Tick> reader = getExpectedAsReader();
		reader.next();
		reader.close();
		reader.item();
	}
	
	@Test (expected=DataException.class)
	public void testCurrent_ThrowsIfClosed() throws Exception {
		Aqiterator<Tick> reader = getExpectedAsReader();
		reader.next();
		reader.close();
		reader.item();
	}

}
