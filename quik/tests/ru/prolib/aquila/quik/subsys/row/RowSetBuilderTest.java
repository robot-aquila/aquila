package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.data.row.RowSetAdapter;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.utils.table.DDETableRowSetBuilder;

/**
 * 2013-02-16<br>
 * $Id$
 */
public class RowSetBuilderTest {
	private IMocksControl control;
	private DDETableRowSetBuilder originalBuilder;
	private Map<String, G<?>> adapters;
	private RowSetBuilder builder;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		originalBuilder = control.createMock(DDETableRowSetBuilder.class);
		adapters = new HashMap<String, G<?>>();
		adapters.put("foo", control.createMock(G.class));
		builder = new RowSetBuilder(originalBuilder, adapters);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(originalBuilder, builder.getOriginalBuilder());
		assertSame(adapters, builder.getAdapters());
	}
	
	@Test
	public void testCreateRowSet() throws Exception {
		DDETable table = control.createMock(DDETable.class);
		RowSet rs = control.createMock(RowSet.class);
		expect(originalBuilder.createRowSet(same(table))).andReturn(rs);
		control.replay();
		
		RowSet expected = new RowSetAdapter(rs, adapters);
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
		Map<String, G<?>> adapters2 = new HashMap<String, G<?>>();
		Variant<DDETableRowSetBuilder> vOrigBldr =
				new Variant<DDETableRowSetBuilder>()
			.add(originalBuilder)
			.add(control.createMock(DDETableRowSetBuilder.class));
		Variant<Map<String, G<?>>> vAdapts =
				new Variant<Map<String, G<?>>>(vOrigBldr)
			.add(adapters2)
			.add(adapters);
		Variant<?> iterator = vAdapts;
		int foundCnt = 0;
		RowSetBuilder x = null, found = null;
		do {
			x = new RowSetBuilder(vOrigBldr.get(), vAdapts.get());
			if ( builder.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(originalBuilder, found.getOriginalBuilder());
		assertSame(adapters, found.getAdapters());
	}

}
