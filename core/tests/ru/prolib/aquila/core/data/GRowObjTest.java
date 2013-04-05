package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-10-13<br>
 * $Id: GRowObjTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class GRowObjTest {
	private static IMocksControl control;
	private static Row row;
	private static G<Integer> adapter;
	private static GRowObj<Integer> getter;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		row = control.createMock(Row.class);
		adapter = control.createMock(G.class);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		getter = new GRowObj<Integer>("object", adapter);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals("object", getter.getName());
		assertSame(adapter, getter.getAdapter());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		getter = new GRowObj<Integer>("foobar");
		assertEquals("foobar", getter.getName());
		assertNull(getter.getAdapter());
	}
	
	@Test
	public void testGet_WoAdapterOk() throws Exception {
		getter = new GRowObj<Integer>("object");
		expect(row.get(eq("object"))).andReturn(10);
		expect(row.get(eq("object"))).andReturn(null);
		control.replay();

		assertSame(10, getter.get(row));
		assertNull(getter.get(row));

		control.verify();
	}
	
	@Test
	public void testGet_WoAdapterNok() throws Exception {
		getter = new GRowObj<Integer>("foobar");
		control.replay();
		
		assertNull(getter.get(null));
		assertNull(getter.get(this));
		
		control.verify();
	}
	
	@Test
	public void testGet_WithAdapter() throws Exception {
		Integer r = 150;
		expect(row.get(eq("object"))).andReturn(r);
		expect(adapter.get(same(r))).andReturn(300);
		control.replay();
		assertEquals((Integer) 300, getter.get(row));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<String> vName = new Variant<String>()
			.add(null)
			.add("foobar")
			.add("object");
		Variant<G<Integer>> vAdapter = new Variant<G<Integer>>(vName)
			.add(null)
			.add(adapter)
			.add(control.createMock(G.class));
		int foundCnt = 0;
		GRowObj<Integer> found = null;
		do {
			GRowObj<Integer> actual =
				new GRowObj<Integer>(vName.get(), vAdapter.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( vAdapter.next() );
		assertEquals(1, foundCnt);
		assertEquals("object", found.getName());
		assertSame(adapter, found.getAdapter());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
		assertTrue(getter.equals(getter));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121031, /*0*/55359)
			.append("object")
			.append(adapter)
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
