package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Tick;

public class SeamlessTickReaderTest {
	private static final Symbol symbol;
	private static final DateTimeFormatter df;
	
	static {
		symbol = new Symbol("SBRF", "EQBR", "RUR");
		df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}
	
	private InMemoryManager manager;
	
	private static Tick newTick(String time, double last, int vol) {
		return new Tick(LocalDateTime.parse(time, df), last, vol);
	}

	@Before
	public void setUp() throws Exception {
		manager = new InMemoryManager();
	}
	
	@Test
	public void testCtor() {
		LocalDateTime time = LocalDateTime.of(2015, 7, 6, 0, 0, 0); 
		SeamlessTickReader reader = new SeamlessTickReader(symbol, time, manager);
		assertEquals(symbol, reader.getSymbol());
		assertEquals(time, reader.getCurrentTime());
		assertEquals(manager, reader.getDataSegmentManager());
	}
	
	@Test
	public void testIterate_ComplexTest() throws Exception {
		List<Tick>
		data = new Vector<Tick>();
		data.add(newTick("2015-07-06 15:30:00", 201.2, 10)); // to skip
		data.add(newTick("2015-07-06 15:44:55", 200.0, 15)); // to skip
		data.add(newTick("2015-07-06 15:45:00", 201.0, 20));
		data.add(newTick("2015-07-06 00:00:00", 215.0, 10)); // to skip
		data.add(newTick("2015-07-01 00:00:00", 218.0, 80)); // to skip
		data.add(newTick("2015-07-06 15:45:10", 202.0, 20));
		manager.setSegmentData(symbol, LocalDate.of(2015, 7, 6), data);
		data = new Vector<Tick>();
		data.add(newTick("2015-07-07 10:00:00", 198.8, 11));
		data.add(newTick("2015-07-07 10:00:01", 199.2, 12));
		data.add(newTick("2015-07-07 10:00:02", 201.5, 15));
		data.add(newTick("2015-07-06 00:00:00", 197.1, 10)); // to skip
		manager.setSegmentData(symbol, LocalDate.of(2015, 7, 7), data);
		// there's gap between segments
		data = new Vector<Tick>();
		data.add(newTick("2015-07-09 10:00:00", 186.5, 20));
		data.add(newTick("2015-07-09 11:00:00", 185.1, 10));
		manager.setSegmentData(symbol, LocalDate.of(2015, 7, 9), data);
		
		List<Tick> expected = new Vector<Tick>(), actual = new Vector<Tick>();
		expected.add(newTick("2015-07-06 15:45:00", 201.0, 20));
		expected.add(newTick("2015-07-06 15:45:10", 202.0, 20));
		expected.add(newTick("2015-07-07 10:00:00", 198.8, 11));
		expected.add(newTick("2015-07-07 10:00:01", 199.2, 12));
		expected.add(newTick("2015-07-07 10:00:02", 201.5, 15));
		expected.add(newTick("2015-07-09 10:00:00", 186.5, 20));
		expected.add(newTick("2015-07-09 11:00:00", 185.1, 10));
		
		SeamlessTickReader reader = new SeamlessTickReader(symbol,
				LocalDateTime.of(2015, 7, 6, 15, 45, 0), manager);
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();
		
		assertEquals(expected, actual);
	}

}
