package ru.prolib.aquila.probe.internal;

import static org.junit.Assert.*;
import java.io.File;
import java.util.Vector;

import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.DataStorageImpl;
import ru.prolib.aquila.core.data.Tick;

public class PROBEDataStorageTest {
	private static SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("RTS-12.14", "FORTS", "USD", SecurityType.FUT);
	}
	
	private PROBEDataStorage storage;
	
	@Before
	public void setUp() throws Exception {
		storage = new PROBEDataStorage(new File("fixture"));
	}
	
	@Test
	public void testGetSecurityProperties() throws Exception {
		SecurityProperties actual = storage.getSecurityProperties(descr);
		assertEquals("RTS-12.14", actual.getDisplayName());
		assertEquals(1, actual.getLotSize());
		assertEquals(10d, actual.getMinStepSize(), 0.1d);
		assertEquals(0, actual.getPricePrecision());
		assertEquals(0.1d, actual.getInitialMarginCalcBase(), 0.1d);
		assertEquals(0.2d, actual.getStepPriceCalcBase(), 0.1d);
	}
	
	@Test
	public void testBase() throws Exception {
		assertTrue(storage instanceof DataStorageImpl);
	}
	
	@Test
	public void testConstruct_MustCreateIteratorStorage() throws Exception {
		DateTime start = new DateTime(2014, 10, 1, 10, 0, 0, 0);
		Vector<Tick> expected = new Vector<Tick>(),
				actual = new Vector<Tick>();
		expected.add(new Tick(start, 143250d, 100d));
		expected.add(new Tick(start.plusMinutes(15), 143500d, 8d));

		Aqiterator<Tick> it = storage.getIterator(descr, start);
		while ( it.next() ) {
			actual.add(it.item());
		}
		it.close();
		
		assertEquals(expected, actual);
	}
	
}
