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
import ru.prolib.aquila.quik.dde.MirrorTableHandler;

public class MirrorTableHandlerTest {
	private static String[] headers = { "foo", "bar" };
	private IMocksControl control;
	private DDETable ddeTable;
	private CacheGateway gateway;
	private static Map<String, Integer> expectedValidHeaders;
	private MirrorTableHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 expectedValidHeaders = new Hashtable<String, Integer>();
		 expectedValidHeaders.put("foo", 0);
		 expectedValidHeaders.put("bar", 1);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		gateway = control.createMock(CacheGateway.class);
		expect(gateway.getRequiredHeaders()).andStubReturn(headers);
		handler = new MirrorTableHandler(gateway);
	}
	
	@Test
	public void testHandle_FirstTimeThrowsInvalidHeaders() throws Exception {
		Object cells[] = { "foo", "zubba" };
		ddeTable = new DDETableImpl(cells, "orders", "R1C1:R2C1", 2);
		gateway.clearCache();
		control.replay();
		
		try {
			handler.handle(ddeTable);
			fail("Expected: " +
					NotAllRequiredFieldsException.class.getSimpleName());
		} catch ( NotAllRequiredFieldsException e ) {
			assertEquals(new NotAllRequiredFieldsException("orders", "bar"), e);
		}
	}

	@Test
	public void testHandle_NextTimeThrowsInvalidHeaders() throws Exception {
		Object cells1[] = { "foo", "bar" };
		Object cells2[] = { "foo", "buz" };
		gateway.clearCache();
		gateway.fireUpdateCache();
		gateway.clearCache();
		control.replay();
		
		handler.handle(new DDETableImpl(cells1, "orders", "R1C1:R1C2", 2));
		assertEquals(expectedValidHeaders, handler.getCurrentHeadersMap());
		
		try {
			handler.handle(new DDETableImpl(cells2, "orders", "R1C1:R1C2", 2));
			fail("Expected: " +
					NotAllRequiredFieldsException.class.getSimpleName());
		} catch ( NotAllRequiredFieldsException e ) {
			assertEquals(new NotAllRequiredFieldsException("orders", "bar"), e);
			assertEquals(MirrorTableHandler.EMPTY_MAP,
					handler.getCurrentHeadersMap());
		}
	}

	
	@Test
	public void testHandle_WithValidHeaders() throws Exception {
		Object cells[] = { "foo", "bar", 1, 2, 11, 22 };
		ddeTable = new DDETableImpl(cells, "orders", "R1C1:R3C2", 2);
		RowSet rs1 = new DDETableRowSet(ddeTable, expectedValidHeaders, 1),
			   rs2 = new DDETableRowSet(ddeTable, expectedValidHeaders, 1);
		rs1.next();
		rs2.next(); rs2.next();
		gateway.clearCache();
		gateway.toCache(eq(rs1));
		gateway.toCache(eq(rs2));
		gateway.fireUpdateCache();
		control.replay();
		
		handler.handle(ddeTable);
		
		control.verify();
		assertEquals(expectedValidHeaders, handler.getCurrentHeadersMap());
	}

	@Test
	public void testHandle_WithoutHeaders() throws Exception {
		Object cells2[] = { 1, "a", 3, "b", 5, "c" };
		ddeTable = new DDETableImpl(cells2, "orders", "R2C1:R4C2", 2);
		RowSet rs1 = new DDETableRowSet(ddeTable, expectedValidHeaders),
			   rs2 = new DDETableRowSet(ddeTable, expectedValidHeaders),
			   rs3 = new DDETableRowSet(ddeTable, expectedValidHeaders);
		rs1.next();
		rs2.next(); rs2.next();
		rs3.next(); rs3.next(); rs3.next();
		gateway.clearCache(); // first pass: for headers
		gateway.fireUpdateCache(); // first pass: for headers
		gateway.toCache(eq(rs1));
		gateway.toCache(eq(rs2));
		gateway.toCache(eq(rs3));
		gateway.fireUpdateCache();
		control.replay();

		Object cells1[] = { "foo", "bar" };
		handler.handle(new DDETableImpl(cells1, "orders", "R1C1:R1C2", 2));
		handler.handle(ddeTable);
		
		control.verify();
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(this));
		assertFalse(handler.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		control.resetToNice();
		CacheGateway gateway2 = control.createMock(CacheGateway.class);
		expect(gateway.getRequiredHeaders()).andStubReturn(headers);
		expect(gateway2.getRequiredHeaders()).andStubReturn(headers);
		control.replay();
		Object cells[] = { "foo", "bar" };
		ddeTable = new DDETableImpl(cells, "orders", "R1C1:R1C2", 2);
		handler.handle(ddeTable);
		
		Variant<CacheGateway> vGw = new Variant<CacheGateway>()
			.add(gateway)
			.add(gateway2);
		Variant<Boolean> vHdr = new Variant<Boolean>(vGw)
			.add(true)
			.add(false);
		Variant<?> iterator = vHdr;
		int foundCnt = 0;
		MirrorTableHandler x = null, found = null;
		do {
			x = new MirrorTableHandler(vGw.get());
			if ( vHdr.get() ) {
				x.handle(ddeTable);
			}
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(gateway, found.getCacheGateway());
		assertEquals(expectedValidHeaders, found.getCurrentHeadersMap());
	}

}
