package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.assembler.cache.dde.TableGateway;
import ru.prolib.aquila.quik.assembler.cache.dde.TableHandler;
import ru.prolib.aquila.quik.assembler.cache.dde.TableHelper;
import ru.prolib.aquila.quik.assembler.cache.dde.TableMeta;

public class TableHandlerTest {
	private IMocksControl control;
	private DDETable table;
	private TableGateway gateway;
	private TableHelper helper;
	private RowSet rs;
	private TableHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gateway = control.createMock(TableGateway.class);
		helper = control.createMock(TableHelper.class);
		table = control.createMock(DDETable.class);
		rs = control.createMock(RowSet.class);
		handler = new TableHandler(helper);
		expect(helper.getTableGateway()).andStubReturn(gateway);
	}
	
	@Test
	public void testHandle_WithHeaders() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(1, 1, 20, 10));
		expect(helper.createTableMeta(same(table))).andReturn(meta);
		helper.updateHeaders(same(table));
		expect(helper.createRowSet(same(meta), same(table))).andReturn(rs);
		helper.process(same(meta), same(rs));
	}

	@Test
	public void testHandle_WithoutHeaders() throws Exception {
		TableMeta meta = new TableMeta(new DDETableRange(2, 1, 20, 10));
		expect(helper.createTableMeta(same(table))).andReturn(meta);
		expect(helper.createRowSet(same(meta), same(table))).andReturn(rs);
		helper.process(same(meta), same(rs));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(this));
		assertFalse(handler.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(handler.equals(new TableHandler(helper)));
		TableHelper helper2 = control.createMock(TableHelper.class);
		assertFalse(handler.equals(new TableHandler(helper2)));
	}
	
	@Test
	public void testConstruct1Gw() throws Exception {
		handler = new TableHandler(gateway);
		TableHandler expected =
			new TableHandler(new TableHelper(gateway));
		assertEquals(expected, handler);
	}

}
