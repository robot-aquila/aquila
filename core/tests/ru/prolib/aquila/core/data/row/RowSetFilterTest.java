package ru.prolib.aquila.core.data.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-18<br>
 * $Id$
 */
public class RowSetFilterTest {
	private IMocksControl control;
	private RowSet rs;
	private Validator validator;
	private RowSetFilter filter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rs = control.createMock(RowSet.class);
		validator = control.createMock(Validator.class);
		filter = new RowSetFilter(rs, validator);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(rs, filter.getSourceRowSet());
		assertSame(validator, filter.getValidator());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(filter.equals(filter));
		assertFalse(filter.equals(null));
		assertFalse(filter.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<RowSet> vRs = new Variant<RowSet>()
			.add(rs)
			.add(control.createMock(RowSet.class));
		Variant<Validator> vVal = new Variant<Validator>(vRs)
			.add(validator)
			.add(control.createMock(Validator.class));
		Variant<?> iterator = vVal;
		int foundCnt = 0;
		RowSetFilter x = null, found = null;
		do {
			x = new RowSetFilter(vRs.get(), vVal.get());
			if ( filter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(rs, found.getSourceRowSet());
		assertSame(validator, found.getValidator());
	}
	
	@Test
	public void testReset() throws Exception {
		rs.reset();
		control.replay();
		
		filter.reset();
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		rs.close();
		control.replay();
		
		filter.close();
		
		control.verify();
	}
	
	@Test
	public void testGet() throws Exception {
		Object value = new Object();
		expect(rs.get("foo")).andReturn(value);
		control.replay();
		
		assertSame(value, filter.get("foo"));
		
		control.verify();
	}
	
	@Test
	public void testNext_EOS() throws Exception {
		expect(rs.next()).andReturn(true);
		expect(validator.validate(same(rs))).andReturn(false);
		expect(rs.next()).andReturn(true);
		expect(validator.validate(same(rs))).andReturn(false);
		expect(rs.next()).andReturn(false);
		control.replay();
		
		assertFalse(filter.next());
		
		control.verify();
	}
	
	@Test
	public void testNext_FoundValid() throws Exception {
		expect(rs.next()).andReturn(true);
		expect(validator.validate(same(rs))).andReturn(false);
		expect(rs.next()).andReturn(true);
		expect(validator.validate(same(rs))).andReturn(true);
		control.replay();
		
		assertTrue(filter.next());
		
		control.verify();
	}

}
