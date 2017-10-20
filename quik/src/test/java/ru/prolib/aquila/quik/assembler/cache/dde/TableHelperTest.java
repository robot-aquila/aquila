package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.assembler.cache.dde.TableGateway;
import ru.prolib.aquila.quik.assembler.cache.dde.TableHelper;
import ru.prolib.aquila.quik.assembler.cache.dde.TableMeta;


public class TableHelperTest {
	private IMocksControl control;
	private String requiredFields[] = { "foo", "bar" };
	private Map<String, Integer> headers;
	private TableGateway gateway;
	private DDEUtils utils;
	private DDETable table;
	private TableHelper helper;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gateway = control.createMock(TableGateway.class);
		utils = control.createMock(DDEUtils.class);
		table = control.createMock(DDETable.class);
		helper = new TableHelper(gateway, utils);
		
		headers = new Hashtable<String, Integer>();
		headers.put("foo", 1);
		headers.put("bar", 0);
		
		expect(gateway.getRequiredHeaders()).andStubReturn(requiredFields);
	}
	
	@Test
	public void testCreateTableMeta() throws Exception {
		DDETableRange range = new DDETableRange(1, 1, 20, 10);
		expect(utils.parseXltRange(same(table))).andReturn(range);
		control.replay();
		
		TableMeta meta = helper.createTableMeta(table);
		
		control.verify();
		assertSame(range, meta.getTableRange());
	}
	
	@Test
	public void testUpdateHeaders() throws Exception {
		expect(utils.makeHeadersMap(same(table), aryEq(requiredFields)))
			.andReturn(headers);
		control.replay();
		
		helper.updateHeaders(table);
		
		control.verify();
		assertSame(headers, helper.getHeaders());
	}
	
	@Test
	public void testUpdateHeaders_ClearPrevIfThrows() throws Exception {
		helper.setHeaders(headers);
		expect(utils.makeHeadersMap(same(table), aryEq(requiredFields)))
			.andThrow(new NotAllRequiredFieldsException("foo", "bar"));
		control.replay();
		
		try {
			helper.updateHeaders(table);
			fail("Expected: " +
					NotAllRequiredFieldsException.class.getSimpleName());
		} catch ( NotAllRequiredFieldsException e ) { }
		
		control.verify();
		assertEquals(new Hashtable<String, Integer>(), helper.getHeaders());
	}
	
	@Test
	public void testCreateRowSet_ForMetaWithoutHeaders() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(10, 1, 20, 10));
		helper.setHeaders(headers);
		control.replay();
		RowSet expected = new DDETableRowSet(table, headers, 0);
		
		RowSet actual = helper.createRowSet(meta, table);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateRowSet_ForMetaWithHeaders() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		helper.setHeaders(headers);
		control.replay();
		RowSet expected = new DDETableRowSet(table, headers, 1);
		
		RowSet actual = helper.createRowSet(meta, table);
		
		control.verify();
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testProcess_WholeRowSet() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 4, 10));
		RowSet rs = control.createMock(RowSet.class);
		expect(gateway.shouldProcessRowByRow(meta, rs)).andReturn(false);
		control.replay();
		
		helper.process(meta, rs);
		
		control.verify();
	}
	
	@Test
	public void testProcess_RowByRow() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 4, 10));
		RowSet rs = control.createMock(RowSet.class);
		boolean fix[] = { true, false, true, true, true, false, false, true };
		expect(gateway.shouldProcessRowByRow(meta, rs)).andReturn(true);
		for ( int i = 0; i < fix.length; i ++ ) {
			expect(rs.next()).andReturn(true);
			expect(gateway.shouldProcess(same(rs))).andReturn(fix[i]);
			if ( fix[i] ) {
				gateway.process(same(rs));
			}
		}
		expect(rs.next()).andReturn(false);
		control.replay();
		
		helper.process(meta, rs);
		
		control.verify();
	}
	
	@Test
	public void testProcess_NoDataRows() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 1, 10));
		RowSet rs = control.createMock(RowSet.class);
		control.replay();
		
		helper.process(meta, rs);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(helper.equals(helper));
		assertFalse(helper.equals(null));
		assertFalse(helper.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		helper.setHeaders(headers);
		Variant<TableGateway> vGw = new Variant<TableGateway>()
			.add(gateway)
			.add(control.createMock(TableGateway.class));
		Variant<DDEUtils> vUtl = new Variant<DDEUtils>(vGw)
			.add(utils)
			.add(new DDEUtils());
		Variant<Map<String, Integer>> vHdrs =
				new Variant<Map<String, Integer>>(vUtl)
			.add(headers)
			.add(new Hashtable<String, Integer>());
		Variant<?> iterator = vHdrs;
		int foundCnt = 0;
		TableHelper x, found = null;
		do {
			x = new TableHelper(vGw.get(), vUtl.get());
			x.setHeaders(vHdrs.get());
			if ( helper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gateway, found.getTableGateway());
		assertSame(utils, found.getDdeUtils());
		assertSame(headers, found.getHeaders());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		helper = new TableHelper(gateway);
		assertEquals(new DDEUtils(), helper.getDdeUtils());
	}

}
