package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
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
		ticks.add(Tick.of(time("2014-06-18T09:59:59Z"), CDecimalBD.of("144.79"), CDecimalBD.of(250L)));
		ticks.add(Tick.of(time("2014-06-18T18:34:20Z"), CDecimalBD.of("148.79"), CDecimalBD.of(5L)));
		ticks.add(Tick.of(time("2014-06-18T18:44:15Z"), CDecimalBD.of("141.79"), CDecimalBD.of(10L)));
		ticks.add(Tick.of(time("2014-06-19T10:00:00Z"), CDecimalBD.of("154.98"), CDecimalBD.of(1L)));
		ticks.add(Tick.of(time("2014-06-19T15:00:00Z"), CDecimalBD.of("154.80"), CDecimalBD.of(500L)));
		ticks.add(Tick.of(time("2014-06-25T10:00:01Z"), CDecimalBD.of("148.70"), CDecimalBD.of(300L)));
		ticks.add(Tick.of(time("2014-06-25T10:00:02Z"), CDecimalBD.of("147.70"), CDecimalBD.of(1L)));
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
