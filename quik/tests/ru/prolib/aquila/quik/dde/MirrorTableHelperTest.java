package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;

public class MirrorTableHelperTest {
	private IMocksControl control;
	private String requiredFields[] = { "foo", "bar" };
	private Map<String, Integer> headers;
	private Map<Integer, Object> keyValues;
	private CacheGateway gateway;
	private DDEUtils utils;
	private DDETable table;
	private MirrorTableHelper helper;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gateway = control.createMock(CacheGateway.class);
		utils = control.createMock(DDEUtils.class);
		table = control.createMock(DDETable.class);
		helper = new MirrorTableHelper(gateway, utils);
		
		headers = new Hashtable<String, Integer>();
		headers.put("foo", 1);
		headers.put("bar", 0);
		
		keyValues = new Hashtable<Integer, Object>();
		keyValues.put(2, new Integer(12345));
		keyValues.put(4, new Integer(18210));
		
		expect(gateway.getRequiredHeaders()).andStubReturn(requiredFields);
	}
	
	@Test (expected=XltItemFormatException.class)
	public void testCreateTableMeta_ThrowsIfUtilsThrows() throws Exception {
		expect(utils.parseXltRange(same(table)))
			.andThrow(new XltItemFormatException("yep", "bop"));
		control.replay();
		
		helper.createTableMeta(table);
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
	public void testCheckRowSetChanged_NotChanged() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(2, 1, 4, 10));
		helper.setKeyValues(new Hashtable<Integer, Object>(keyValues));
		RowSet rs = control.createMock(RowSet.class);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		expect(gateway.getKeyValue(same(rs))).andReturn(new Integer(12345));
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		// no keyValue previously registered for row#3
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		expect(gateway.getKeyValue(same(rs))).andReturn(new Integer(18210));
		expect(rs.next()).andReturn(false); // end of set
		rs.reset();
		control.replay();
		
		helper.checkRowSetChanged(meta, rs);
		
		control.verify();
		// keyValues map unchanged
		assertEquals(keyValues, helper.getKeyValues());
	}
	
	@Test
	public void testCheckRowSetChanged_ClearIfChanged() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(3, 1, 4, 10));
		helper.setKeyValues(new Hashtable<Integer, Object>(keyValues));
		RowSet rs = control.createMock(RowSet.class);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		// following row with different keyObject
		expect(gateway.getKeyValue(same(rs))).andReturn(new Integer(0));
		gateway.clearCache();
		rs.reset();
		control.replay();
		
		helper.checkRowSetChanged(meta, rs);
		
		control.verify();
		// keyValues map now clean
		assertEquals(new Hashtable<Integer, Object>(), helper.getKeyValues());
	}
	
	@Test
	public void testCheckRowSetChanged_SkipIfShouldntCache() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(3, 1, 4, 10));
		RowSet rs = control.createMock(RowSet.class);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(false);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(false);
		expect(rs.next()).andReturn(false);
		rs.reset();
		control.replay();
		
		helper.checkRowSetChanged(meta, rs);
		
		control.verify();
	}
	
	@Test
	public void testCheckRowSetChanged_NoDataRows() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 1, 10));
		RowSet rs = control.createMock(RowSet.class);
		control.replay();
		
		helper.checkRowSetChanged(meta, rs);
		
		control.verify();
	}
	
	@Test
	public void testCacheRowSet() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 4, 10));
		RowSet rs = control.createMock(RowSet.class);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		gateway.toCache(same(rs));
		expect(gateway.getKeyValue(same(rs))).andReturn(new Integer(861));
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		gateway.toCache(same(rs));
		expect(gateway.getKeyValue(same(rs))).andReturn(new Integer(182));
		expect(rs.next()).andReturn(false);
		control.replay();
		
		helper.cacheRowSet(meta, rs);
		
		control.verify();
		keyValues.clear();
		keyValues.put(2, new Integer(861));
		keyValues.put(3, new Integer(182));
		assertEquals(keyValues, helper.getKeyValues());
	}
	
	@Test
	public void testCacheRowSet_SkipIfShouldntCache() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 4, 10));
		RowSet rs = control.createMock(RowSet.class);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(false);
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(true);
		gateway.toCache(same(rs));
		expect(gateway.getKeyValue(same(rs))).andReturn(new Integer(182));
		expect(rs.next()).andReturn(true);
		expect(gateway.shouldCache(same(rs))).andReturn(false);
		expect(rs.next()).andReturn(false);
		control.replay();
		
		helper.cacheRowSet(meta, rs);
		
		control.verify();
	}
	
	@Test
	public void testCacheRowSet_NoDataRows() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 1, 10));
		RowSet rs = control.createMock(RowSet.class);
		control.replay();
		
		helper.cacheRowSet(meta, rs);
		
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
		helper.setKeyValues(keyValues);
		Variant<CacheGateway> vGw = new Variant<CacheGateway>()
			.add(gateway)
			.add(control.createMock(CacheGateway.class));
		Variant<DDEUtils> vUtl = new Variant<DDEUtils>(vGw)
			.add(utils)
			.add(new DDEUtils());
		Variant<Map<String, Integer>> vHdr =
				new Variant<Map<String, Integer>>(vUtl)
			.add(headers)
			.add(new Hashtable<String, Integer>());
		Variant<Map<Integer, Object>> vKeys =
				new Variant<Map<Integer, Object>>(vHdr)
			.add(keyValues)
			.add(new Hashtable<Integer, Object>());
		Variant<?> iterator = vKeys;
		int foundCnt = 0;
		MirrorTableHelper x = null, found = null;
		do {
			x = new MirrorTableHelper(vGw.get(), vUtl.get());
			x.setHeaders(vHdr.get());
			x.setKeyValues(vKeys.get());
			if ( helper.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gateway, found.getCacheGateway());
		assertSame(utils, found.getDdeUtils());
		assertSame(headers, found.getHeaders());
		assertSame(keyValues, found.getKeyValues());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		helper = new MirrorTableHelper(gateway);
		assertEquals(new DDEUtils(), helper.getDdeUtils());
	}

}
