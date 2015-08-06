package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import org.junit.*;



/**
 * 2012-04-19<br>
 * $Id: SeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class SeriesImplTest {
	private SeriesImpl<Integer> val1,val2;
	
	@Before
	public void setUp() throws Exception {
		val1 = new SeriesImpl<Integer>("zulu4");
		val1.add( 2); val1.add( 4); val1.add( 8); val1.add(16);
		val2 = new SeriesImpl<Integer>("foo", 4);
		val2.add( 2); val2.add( 4); val2.add( 8); val2.add(16);
		val2.add(-1); val2.add(-2); val2.add(-3); val2.add(-4); // здесь оптим.
		val2.add( 0); val2.add( 9); val2.add( 8); val2.add( 7); // здесь оптим.
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct1_ThrowsIfIdIsNull() throws Exception {
		new SeriesImpl<Integer>(null);
	}

	@Test (expected=NullPointerException.class)
	public void testConstruct2_ThrowsIfIdIsNull() throws Exception {
		new SeriesImpl<Integer>(null, 128);
	}
	
	@Test
	public void testConstruct0_Ok() throws Exception {
		val2 = new SeriesImpl<Integer>();
		assertEquals(Series.DEFAULT_ID, val2.getId());
		assertEquals(SeriesImpl.STORAGE_NOT_LIMITED, val2.getStorageLimit());
	}

	@Test
	public void testGetId() throws Exception {
		assertEquals("zulu4", val1.getId());
		assertEquals("foo",	  val2.getId());
	}

	@Test (expected=ValueNotExistsException.class)
	public void testGet0_ThrowsIfNoValue() throws Exception {
		val1 = new SeriesImpl<Integer>();
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
	public void testSet1_Ok() throws Exception {
		val1.set(12345);
		assertEquals(12345, (int)val1.get());
		assertEquals(12345, (int)val1.get(3));
	}
	
	@Test (expected=ValueNotExistsException.class)
	public void testSet1_ThrowsIfNoValue() throws Exception {
		val1.clear();
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
	
	@Test
	public void testClear() throws Exception {
		val1.clear();
		val1.add(100);
		val1.add(200);
		assertEquals(2, val1.getLength());
		assertEquals(100, (int)val1.get(0));
		assertEquals(200, (int)val1.get(1));
	}
	
}
