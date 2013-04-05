package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.data.row.RowSetFilter;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.utils.table.DDETableRowSetBuilder;

/**
 * 2013-02-18<br>
 * $Id$
 */
public class RowSetBuilderFilterTest {
	private IMocksControl control;
	private DDETableRowSetBuilder originalBuilder;
	private Validator validator;
	private RowSetBuilderFilter builder;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		originalBuilder = control.createMock(DDETableRowSetBuilder.class);
		validator = control.createMock(Validator.class);
		builder = new RowSetBuilderFilter(originalBuilder, validator);
	}
	
	@Test
	public void testCreateRowSet() throws Exception {
		RowSet rs = control.createMock(RowSet.class);
		DDETable table = control.createMock(DDETable.class);
		expect(originalBuilder.createRowSet(same(table))).andReturn(rs);
		RowSet expected = new RowSetFilter(rs, validator);
		control.replay();
		
		assertEquals(expected, builder.createRowSet(table));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(builder.equals(builder));
		assertFalse(builder.equals(null));
		assertFalse(builder.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<DDETableRowSetBuilder> vBldr =
				new Variant<DDETableRowSetBuilder>()
			.add(originalBuilder)
			.add(control.createMock(DDETableRowSetBuilder.class));
		Variant<Validator> vVal = new Variant<Validator>(vBldr)
			.add(validator)
			.add(control.createMock(Validator.class));
		Variant<?> iterator = vVal;
		int foundCnt = 0;
		RowSetBuilderFilter x = null, found = null;
		do {
			x = new RowSetBuilderFilter(vBldr.get(), vVal.get());
			if ( builder.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(originalBuilder, found.getOriginalBuilder());
		assertSame(validator, found.getRowValidator());
	}

}
