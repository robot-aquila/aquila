package ru.prolib.aquila.core.data.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-13<br>
 * $Id$
 */
public class RowElementTest {
	private IMocksControl control;
	private Row row;
	private RowElement getter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		row = control.createMock(Row.class);
		getter = new RowElement("foobar", Double.class);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals("foobar", getter.getElementId());
		assertSame(Double.class, getter.getElementClass());
	}
	
	@Test
	public void testGet_NullIfSourceIsNotRow() throws Exception {
		control.replay();
		
		assertNull(getter.get(this));
		assertNull(getter.get(null));
		
		control.verify();
	}
	
	@Test
	public void testGet_NullIfElementIsNull() throws Exception {
		expect(row.get("foobar")).andReturn(null);
		control.replay();
		
		assertNull(getter.get(row));
		
		control.verify();
	}
	
	@Test
	public void testGet_NullIfElementOfUnexpectedClass() throws Exception {
		expect(row.get("foobar")).andReturn(new Integer(12345));
		control.replay();
		
		assertNull(getter.get(row));
		
		control.verify();
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		Double value = new Double(12.34d);
		expect(row.get("foobar")).andReturn(value);
		control.replay();
		
		assertSame(value, getter.get(row));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(this));
		assertFalse(getter.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("foobar")
			.add("zulu24");
		Variant<Class<?>> vClass = new Variant<Class<?>>(vId)
			.add(Double.class)
			.add(getClass());
		Variant<?> iterator = vClass;
		int foundCnt = 0;
		RowElement x = null, found = null;
		do {
			x = new RowElement(vId.get(), vClass.get());
			if ( getter.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foobar", found.getElementId());
		assertSame(Double.class, found.getElementClass());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("RowElement[foobar, Double]", getter.toString());
	}

}
