package ru.prolib.aquila.quik.assembler.cache.dde;


import static org.junit.Assert.*;

import java.util.*;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.cache.dde.RowUnmappedValueException;

public class RowUnmappedValueExceptionTest {
	private Set<String> expected1, expected2;
	private RowUnmappedValueException exception;

	@Before
	public void setUp() throws Exception {
		expected1 = new LinkedHashSet<String>();
		expected1.add("zulu");
		expected1.add("herra");
		expected2 = new LinkedHashSet<String>();
		expected2.add("zenoby");
		expected2.add("bukakka");
		expected2.add("gibbon");
		exception = new RowUnmappedValueException("foobar", "zebra", expected1);
	}
	
	@Test
	public void testGetMessage() throws Exception {
		String expected = "Unmapped value [zebra] for [foobar]. "
			+ "Expected one of [zulu, herra]"; 
		assertEquals(expected, exception.getMessage());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(exception.equals(exception));
		assertFalse(exception.equals(null));
		assertFalse(exception.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vElemId = new Variant<String>()
			.add("foobar")
			.add("choowe");
		Variant<String> vValue = new Variant<String>(vElemId)
			.add("zebra")
			.add("gotcha");
		Variant<Set<String>> vExpect = new Variant<Set<String>>(vValue)
			.add(expected1)
			.add(expected2);
		Variant<?> iterator = vExpect;
		int foundCnt = 0;
		RowUnmappedValueException x = null, found = null;
		do {
			x = new RowUnmappedValueException(vElemId.get(),
					vValue.get(), vExpect.get());
			if ( exception.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foobar", found.getElementId());
		assertEquals("zebra", found.getActualValue());
		assertEquals(expected1, found.getExpectedValues());
	}

}
