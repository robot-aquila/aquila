package ru.prolib.aquila.ta.ds;

import org.junit.*;
import org.easymock.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class DataSetIteratorLimitTest {
	IMocksControl control;
	DataSetIterator iterator;
	DataSetIteratorLimit limit;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		iterator = control.createMock(DataSetIterator.class);
		limit = new DataSetIteratorLimit(iterator, 2);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(iterator, limit.getDataSetIterator());
		assertEquals(2, limit.getLimit());
	}
	
	@Test
	public void testNextAndClose_IfLimitReached() throws Exception {
		expect(iterator.next()).andReturn(true).times(2);
		control.replay();
		
		assertTrue(limit.next());
		assertTrue(limit.next());
		assertFalse(limit.next());
		limit.close();
		
		control.verify();
	}
	
	@Test
	public void testNextAndClose_IfRealEnd() throws Exception {
		expect(iterator.next()).andReturn(true);
		expect(iterator.next()).andReturn(false);
		iterator.close();
		control.replay();
		
		assertTrue(limit.next());
		assertFalse(limit.next());
		limit.close();
		
		control.verify();
	}

}
