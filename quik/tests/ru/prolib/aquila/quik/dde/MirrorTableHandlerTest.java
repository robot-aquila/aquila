package ru.prolib.aquila.quik.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.dde.MirrorTableHandler;

public class MirrorTableHandlerTest {
	private IMocksControl control;
	private DDETable table;
	private CacheGateway gateway;
	private MirrorTableHelper helper;
	private RowSet rs;
	private MirrorTableHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gateway = control.createMock(CacheGateway.class);
		helper = control.createMock(MirrorTableHelper.class);
		table = control.createMock(DDETable.class);
		rs = control.createMock(RowSet.class);
		handler = new MirrorTableHandler(helper);
		expect(helper.getCacheGateway()).andStubReturn(gateway);
	}
	
	@Test
	public void testHandle_WithHeaders() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		expect(helper.createTableMeta(same(table))).andReturn(meta);
		helper.updateHeaders(same(table));
		gateway.clearCache();
		expect(helper.createRowSet(same(meta), same(table))).andReturn(rs);
		helper.checkRowSetChanged(same(meta), same(rs));
		helper.cacheRowSet(same(meta), same(rs));
		gateway.fireUpdateCache();
	}

	@Test
	public void testHandle_WithoutHeaders() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(2, 1, 20, 10));
		expect(helper.createTableMeta(same(table))).andReturn(meta);
		expect(helper.createRowSet(same(meta), same(table))).andReturn(rs);
		helper.checkRowSetChanged(same(meta), same(rs));
		helper.cacheRowSet(same(meta), same(rs));
		gateway.fireUpdateCache();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(this));
		assertFalse(handler.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(handler.equals(new MirrorTableHandler(helper)));
		MirrorTableHelper helper2 = control.createMock(MirrorTableHelper.class);
		assertFalse(handler.equals(new MirrorTableHandler(helper2)));
	}
	
	@Test
	public void testConstruct1Gw() throws Exception {
		handler = new MirrorTableHandler(gateway);
		MirrorTableHandler expected =
			new MirrorTableHandler(new MirrorTableHelper(gateway));
		assertEquals(expected, handler);
	}

}
