package ru.prolib.aquila.ta;

import static org.junit.Assert.*;
import org.junit.*;

public class ValueTest {
	private ValueImpl<Integer> val1,val2;

	@Before
	public void setUp() throws Exception {
		val1 = new ValueImpl<Integer>("zulu4");
		val1.add( 2); val1.add( 4); val1.add( 8); val1.add(16);
		val2 = new ValueImpl<Integer>("foo", 4);
		val2.add( 2); val2.add( 4); val2.add( 8); val2.add(16);
		val2.add(-1); val2.add(-2); val2.add(-3); val2.add(-4); // здесь оптим.
		val2.add( 0); val2.add( 9); val2.add( 8); val2.add( 7); // здесь оптим.
	}
	
	public void testConstruct0_Ok() {
		val1 = new ValueImpl<Integer>();
		assertEquals(ValueImpl.DEFAULT_ID, val1.getId());
		assertEquals(ValueImpl.LENGTH_NOT_LIMITED, val1.getLengthLimit());
	}
	
	@Test
	public void testConstruct1_Ok() {
		assertEquals("zulu4", val1.getId());
		assertEquals(ValueImpl.LENGTH_NOT_LIMITED, val1.getLengthLimit());
	}
	
	@Test
	public void testConstruct2_Ok() {
		assertEquals("foo", val2.getId());
		assertEquals(4, val2.getLengthLimit());
	}
	
	@Test
	public void testGetId() {
		assertEquals("zulu4", val1.getId());
		assertEquals("foo",	  val2.getId());
	}

	@Test (expected=ValueNotExistsException.class)
	public void testGet0_ThrowsIfNoValue() throws Exception {
		val1 = new ValueImpl<Integer>();
		val1.get();
	}
	
	@Test
	public void testGet0_Ok() throws Exception {
		assertEquals(16, (int)val1.get());
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet1_ThrowsIfOutOfRange() throws Exception {
		val1.get(4);
	}
	
	@Test 
	public void testGet1_Ok() throws Exception {
		assertEquals( 2, (int)val1.get(0));
		assertEquals( 4, (int)val1.get(1));
		assertEquals( 8, (int)val1.get(2));
		assertEquals(16, (int)val1.get(3));
		assertEquals( 2, (int)val1.get(-3));
		assertEquals( 4, (int)val1.get(-2));
		assertEquals( 8, (int)val1.get(-1));
	}

	@Test
	public void testSet_Ok() throws Exception {
		val1.set(12345);
		assertEquals(12345, (int)val1.get());
		assertEquals(12345, (int)val1.get(3));
	}
	
	@Test (expected=ValueNotExistsException.class)
	public void testSet_ThrowsIfNoValue() throws Exception {
		val1 = new ValueImpl<Integer>();
		val1.set(12345);
	}
	
	@Test
	public void testGet0_AfterLimited() throws Exception {
		assertEquals(7, (int)val2.get());
	}
	
	@Test (expected=ValueOutOfDateException.class)
	public void testGet1_AfterLimitedThrowsIfOutOfDatePos() throws Exception {
		val2.get(7);
	}
	
	@Test (expected=ValueOutOfDateException.class)
	public void testGet1_AfterLimitedThrowsIfOutOfDateNeg() throws Exception {
		val2.get(-4);
	}
	
	@Test
	public void testGet1_AfterLimitedOkPos() throws Exception {
		assertEquals(0, (int)val2.get( 8));
		assertEquals(9, (int)val2.get( 9));
		assertEquals(8, (int)val2.get(10));
		assertEquals(7, (int)val2.get(11));
	}
	
	@Test
	public void testGet1_AfterLimitedOkNeg() throws Exception {
		assertEquals(0, (int)val2.get(-3));
		assertEquals(9, (int)val2.get(-2));
		assertEquals(8, (int)val2.get(-1));
	}

	@Test
	public void testGetLength_AfterLimited() throws Exception {
		assertEquals(12, val2.getLength());
	}
	
}
