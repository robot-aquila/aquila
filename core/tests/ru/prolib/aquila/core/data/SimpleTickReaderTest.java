package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

public class SimpleTickReaderTest {
	private final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	
	/**
	 * Parse string "yyyy-MM-dd HH:mm:ss" datetime format into joda-time.
	 * <p> 
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public DateTime time(String time) throws Exception {
		return new DateTime(f.parse(time));
	}
	
	/**
	 * Get expected tick-list.
	 * <p>
	 * @return
	 * @throws Exception
	 */
	public List<Tick> getExpectedAsList() throws Exception {
		List<Tick> ticks = new LinkedList<Tick>();
		ticks.add(new Tick(time("2014-06-18 09:59:59"), 144.79, 250.0));
		ticks.add(new Tick(time("2014-06-18 18:34:20"), 148.79,   5.0));
		ticks.add(new Tick(time("2014-06-18 18:44:15"), 141.79,  10.0));
		ticks.add(new Tick(time("2014-06-19 10:00:00"), 154.98,   1.54));
		ticks.add(new Tick(time("2014-06-19 15:00:00"), 154.80, 500.0));
		ticks.add(new Tick(time("2014-06-25 10:00:01"), 148.70, 300.0));
		ticks.add(new Tick(time("2014-06-25 10:00:02"), 147.70,   1.4));
		return ticks;
	}
	
	public SimpleTickReader getExpectedAsReader() throws Exception {
		return new SimpleTickReader(getExpectedAsList());
	}

	@Test
	public void testRead() throws Exception {
		List<Tick> expected = getExpectedAsList(),
				actual = new LinkedList<Tick>();
		TickReader reader = getExpectedAsReader();
		Tick tick = null;
		while ( (tick = reader.read()) != null ) {
			actual.add(tick);
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRead_Closed() throws Exception {
		List<Tick> expected = new LinkedList<Tick>(),
				actual = new LinkedList<Tick>();
		
		TickReader reader = getExpectedAsReader();
		reader.close();
		Tick tick = null;
		while ( (tick = reader.read()) != null ) {
			actual.add(tick);
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConstruct0() throws Exception {
		List<Tick> expected = new LinkedList<Tick>(),
				actual = new LinkedList<Tick>();
		
		TickReader reader = new SimpleTickReader();
		Tick tick;
		while ( (tick = reader.read()) != null ) {
			actual.add(tick);
		}
		
		assertEquals(expected, actual);
	}

}
