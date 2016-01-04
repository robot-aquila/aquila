package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class SeamlessTickReaderTest {
	private static final Symbol symbol;	
	static {
		symbol = new Symbol("SBRF", "EQBR", "RUR");
	}
	
	private InMemoryManager manager;
	
	private static Tick newTick(String time, double last, int vol) {
		return Tick.of(Instant.parse(time), last, vol);
	}

	@Before
	public void setUp() throws Exception {
		manager = new InMemoryManager();
	}
	
	@Test
	public void testCtor() {
		Instant time = Instant.parse("2015-07-06T00:00:00Z"); 
		SeamlessTickReader reader = new SeamlessTickReader(symbol, time, manager);
		assertEquals(symbol, reader.getSymbol());
		assertEquals(time, reader.getCurrentTime());
		assertEquals(manager, reader.getDataSegmentManager());
	}
	
	@Test
	public void testIterate_ComplexTest() throws Exception {
		List<Tick>
		data = new Vector<Tick>();
		data.add(newTick("2015-07-06T15:30:00Z", 201.2, 10)); // to skip
		data.add(newTick("2015-07-06T15:44:55Z", 200.0, 15)); // to skip
		data.add(newTick("2015-07-06T15:45:00Z", 201.0, 20));
		data.add(newTick("2015-07-06T00:00:00Z", 215.0, 10)); // to skip
		data.add(newTick("2015-07-01T00:00:00Z", 218.0, 80)); // to skip
		data.add(newTick("2015-07-06T15:45:10Z", 202.0, 20));
		manager.setSegmentData(symbol, LocalDate.of(2015, 7, 6), data);
		data = new Vector<Tick>();
		data.add(newTick("2015-07-07T10:00:00Z", 198.8, 11));
		data.add(newTick("2015-07-07T10:00:01Z", 199.2, 12));
		data.add(newTick("2015-07-07T10:00:02Z", 201.5, 15));
		data.add(newTick("2015-07-06T00:00:00Z", 197.1, 10)); // to skip
		manager.setSegmentData(symbol, LocalDate.of(2015, 7, 7), data);
		// there's gap between segments
		data = new Vector<Tick>();
		data.add(newTick("2015-07-09T10:00:00Z", 186.5, 20));
		data.add(newTick("2015-07-09T11:00:00Z", 185.1, 10));
		manager.setSegmentData(symbol, LocalDate.of(2015, 7, 9), data);
		
		List<Tick> expected = new Vector<Tick>(), actual = new Vector<Tick>();
		expected.add(newTick("2015-07-06T15:45:00Z", 201.0, 20));
		expected.add(newTick("2015-07-06T15:45:10Z", 202.0, 20));
		expected.add(newTick("2015-07-07T10:00:00Z", 198.8, 11));
		expected.add(newTick("2015-07-07T10:00:01Z", 199.2, 12));
		expected.add(newTick("2015-07-07T10:00:02Z", 201.5, 15));
		expected.add(newTick("2015-07-09T10:00:00Z", 186.5, 20));
		expected.add(newTick("2015-07-09T11:00:00Z", 185.1, 10));
		
		SeamlessTickReader reader = new SeamlessTickReader(symbol,
				Instant.parse("2015-07-06T15:45:00Z"), manager);
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testX() throws Exception {
		System.out.println(Instant.now());
	}

}
