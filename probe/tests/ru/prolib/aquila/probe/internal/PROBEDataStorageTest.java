package ru.prolib.aquila.probe.internal;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.DataStorageImpl;

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
	
}
