package ru.prolib.aquila.quik.assembler.cache;

import static org.junit.Assert.*;
import java.util.Currency;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;

public class QUIKSecurityDescriptorTest {
	private QUIKSecurityDescriptor descr;

	@Before
	public void setUp() throws Exception {
		descr = new QUIKSecurityDescriptor("RTS-12.13", "SPBFUT", ISO4217.USD,
				SecurityType.FUT, "RIZ3", "ShortName", "Future RTS-12.13");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals("RTS-12.13", descr.getCode());
		assertEquals("SPBFUT", descr.getClassCode());
		assertEquals(ISO4217.USD, descr.getCurrency());
		assertEquals(SecurityType.FUT, descr.getType());
		assertEquals("RIZ3", descr.getSystemCode());
		assertEquals("ShortName", descr.getShortName());
		assertEquals("Future RTS-12.13", descr.getDisplayName());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int expected = new SecurityDescriptor("RTS-12.13", "SPBFUT",
				ISO4217.USD, SecurityType.FUT).hashCode(); 
		assertEquals(expected, descr.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(descr.equals(new SecurityDescriptor("RTS-12.13", "SPBFUT",
				ISO4217.USD, SecurityType.FUT)));
		assertTrue(descr.equals(descr));
		assertFalse(descr.equals(null));
		assertFalse(descr.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vCode = new Variant<String>()
			.add("RTS-12.13")
			.add("GZDBRSDS2");
		Variant<String> vClass = new Variant<String>(vCode)
			.add("SPBFUT")
			.add("ZEBRA");
		Variant<Currency> vCurr = new Variant<Currency>(vClass)
			.add(ISO4217.USD)
			.add(ISO4217.EUR);
		Variant<SecurityType> vType = new Variant<SecurityType>(vCurr)
			.add(SecurityType.FUT)
			.add(SecurityType.CASH);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		QUIKSecurityDescriptor x, found = null;
		do {
			x = new QUIKSecurityDescriptor(vCode.get(), vClass.get(),
					vCurr.get(), vType.get(), "NotUsed", "AnyName", "Jubba");
			if ( descr.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("RTS-12.13", found.getCode());
		assertEquals("SPBFUT", found.getClassCode());
		assertEquals(ISO4217.USD, found.getCurrency());
		assertEquals(SecurityType.FUT, found.getType());
		assertEquals("NotUsed", found.getSystemCode());
		assertEquals("AnyName", found.getShortName());
		assertEquals("Jubba", found.getDisplayName());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("RTS-12.13@SPBFUT(FUT/USD)", descr.toString());
	}

}
