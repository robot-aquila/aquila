package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-06-01<br>
 * $Id: SecurityDescriptorTest.java 341 2012-12-18 17:16:30Z whirlwind $
 */
public class SecurityDescriptorTest {
	private SecurityDescriptor descr;

	@Before
	public void setUp() throws Exception {
		descr = new SecurityDescriptor("SBER", "EQBR", "USD", SecurityType.STK);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals("SBER", descr.getCode());
		assertEquals("EQBR", descr.getClassCode());
		assertEquals("USD", descr.getCurrency());
		assertEquals(SecurityType.STK, descr.getType());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder()
			.append("SBER")
			.append("EQBR")
			.append("USD")
			.append(SecurityType.STK)
			.hashCode(), descr.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertFalse(descr.equals(null));
		assertTrue(descr.equals(descr));
		assertFalse(descr.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vCode = new Variant<String>()
			.add("SBER")
			.add("GAZP")
			.add(null);
		Variant<String> vClass = new Variant<String>(vCode)
			.add("EQBR")
			.add("SMART")
			.add(null);
		Variant<String> vCurr = new Variant<String>(vClass)
			.add("RUR")
			.add("USD")
			.add(null);
		Variant<SecurityType> vType = new Variant<SecurityType>(vCurr)
			.add(SecurityType.UNK)
			.add(SecurityType.STK)
			.add(null);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		SecurityDescriptor x = null, found = null;
		do {
			x = new SecurityDescriptor(vCode.get(), vClass.get(), vCurr.get(),
					vType.get());
			if ( descr.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("SBER", found.getCode());
		assertEquals("EQBR", found.getClassCode());
		assertEquals("USD", found.getCurrency());
		assertEquals(SecurityType.STK, found.getType());
	}
	
	@Test
	public void testIsValid() throws Exception {
		Variant<String> vCode = new Variant<String>()
			.add("")
			.add("AAPL")
			.add(null);
		Variant<String> vClass = new Variant<String>(vCode)
			.add("")
			.add("SMART")
			.add(null);
		Variant<String> vCurr = new Variant<String>(vClass)
			.add("")
			.add("USD")
			.add(null);
		Variant<SecurityType> vType = new Variant<SecurityType>(vCurr)
			.add(SecurityType.STK)
			.add(null);
		Variant<?> iterator = vType;
		int foundCnt = 0;
		SecurityDescriptor x = null, found = null;
		do {
			x = new SecurityDescriptor(vCode.get(), vClass.get(), vCurr.get(),
					vType.get());
			if ( x.isValid() ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("AAPL", found.getCode());
		assertEquals("SMART", found.getClassCode());
		assertEquals("USD", found.getCurrency());
		assertEquals(SecurityType.STK, found.getType());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("SBER@EQBR(STK/USD)", descr.toString());
	}

}
