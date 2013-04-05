package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.utils.Variant;


/**
 * 2012-04-19<br>
 * $Id: SeriesImplTest.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class SeriesImplTest {
	private IMocksControl control;
	private SeriesImpl<Integer> val1,val2;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		val1 = new SeriesImpl<Integer>("zulu4");
		val1.add( 2); val1.add( 4); val1.add( 8); val1.add(16);
		val2 = new SeriesImpl<Integer>("foo", 4);
		val2.add( 2); val2.add( 4); val2.add( 8); val2.add(16);
		val2.add(-1); val2.add(-2); val2.add(-3); val2.add(-4); // здесь оптим.
		val2.add( 0); val2.add( 9); val2.add( 8); val2.add( 7); // здесь оптим.
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
	public void testSet2_Ok() throws Exception {
		val1.set(80, 2); // by positive index
		assertEquals(80, (int)val1.get(2));
		
		val1.set(200, -1); // by negative index
		assertEquals(200, (int)val1.get(2));
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
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(val1.equals(val1));
		assertFalse(val1.equals(null));
		assertFalse(val1.equals(this));
	}
	
	@Test
	public void testEquals_CommonParams() throws Exception {
		val1 = new SeriesImpl<Integer>("foo", 5);
		Variant<String> vId = new Variant<String>()
			.add("foo")
			.add("bar");
		Variant<Integer> vLmt = new Variant<Integer>(vId)
			.add(5)
			.add(150);
		Variant<?> iterator = vLmt;
		int foundCnt = 0;
		SeriesImpl<Integer> x = null, found = null;
		do {
			x = new SeriesImpl<Integer>(vId.get(), vLmt.get());
			if ( val1.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getId());
		assertEquals(5, found.getStorageLimit());
	}
	
	@Test
	public void testEquals_StorageAffected() throws Exception {
		val1 = new SeriesImpl<Integer>("foo", 5);
		val2 = new SeriesImpl<Integer>("foo", 5);
		assertTrue(val1.equals(val2));
		
		val1.add(1);
		assertFalse(val1.equals(val2));
		val2.add(1);
		assertTrue(val1.equals(val2));
	}
	
	@Test
	public void testEquals_StorageOffsetAffected() throws Exception {
		val1 = new SeriesImpl<Integer>("foo", 4);
		val1.add(-1); val1.add(-2); val1.add(-3); val1.add(-4);
		
		val2 = new SeriesImpl<Integer>("foo", 4);
		val2.add( 2); val2.add( 4); val2.add( 8); val2.add(16);
		val2.add(-1); val2.add(-2); val2.add(-3); val2.add(-4); // здесь оптим.
		
		assertFalse(val1.equals(val2));
		val1.add(-1); val1.add(-2); val1.add(-3); val1.add(-4);
		
		assertTrue(val1.equals(val2));
	}
	
	@Test
	public void testOnAdd() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		listener.onEvent(eq(new ValueEvent<Integer>(val1.OnAdd(), 80, 4)));
		listener.onEvent(eq(new ValueEvent<Integer>(val1.OnAdd(), 75, 5)));
		control.replay();
		
		val1.OnAdd().addListener(listener);
		val1.add(80);
		val1.add(75);
		
		control.verify();
	}
	
	@Test
	public void testOnUpd() throws Exception {
		EventListener listener = control.createMock(EventListener.class);
		listener.onEvent(eq(new ValueEvent<Integer>(val1.OnUpd(), 4, 6, 1)));
		listener.onEvent(eq(new ValueEvent<Integer>(val1.OnUpd(), 8, 7, 2)));
		listener.onEvent(eq(new ValueEvent<Integer>(val1.OnUpd(), 16, 24, 3)));
		control.replay();
		
		val1.OnUpd().addListener(listener);
		val1.set(6, 1);
		val1.set(7, 2);
		val1.set(24);
		
		control.verify();
	}
	
}
