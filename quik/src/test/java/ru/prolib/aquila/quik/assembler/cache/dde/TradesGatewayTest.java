package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.utils.table.DDETableRange;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.Assembler;
import ru.prolib.aquila.quik.assembler.cache.*;

public class TradesGatewayTest {
	private static QUIKSymbol symbol;
	private IMocksControl control;
	private Assembler asm;
	private RowDataConverter converter;
	private TradesGateway gateway;
	private Map<String, Object> map;
	private Row row;
	private QUIKTerminal terminal;
	private Cache cache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol = new QUIKSymbol("A", "B", ISO4217.USD, SymbolType.CURRENCY, "A", "A", "A");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		asm = control.createMock(Assembler.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new TradesGateway(converter, asm);
		map = new HashMap<String, Object>();
		row = new SimpleRow(map);
		terminal = control.createMock(QUIKTerminal.class);
		cache = control.createMock(Cache.class);
		expect(terminal.getDataCache()).andStubReturn(cache);
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
				"TRADENUM",
				"TRADEDATE",
				"TRADETIME",
				"SECCODE",
				"CLASSCODE",
				"PRICE",
				"QTY",
				"BUYSELL",
				"VALUE",
				"TRADETIME_MSEC",
		};
		assertArrayEquals(expected, gateway.getRequiredHeaders());
	}
	
	@Test
	public void testShouldProcess() throws Exception {
		assertFalse(gateway.shouldProcess(row));
	}
	
	@Test
	public void testProcess() throws Exception {
		gateway.process(row);
	}
	
	@Test
	public void testShouldProcessRowByRow() throws Exception {
		RowSet rs = control.createMock(RowSet.class);
		TableMeta meta = new TableMeta(new DDETableRange(2, 1, 11, 10));
		TradesEntry expected = new TradesEntry(gateway, rs, 10);
		asm.assemble(eq(expected));
		control.replay();
		
		assertFalse(gateway.shouldProcessRowByRow(meta, rs));
		
		control.verify();
	}

	@Test
	public void testMakeTrade_NoSymbol() throws Exception {
		map.put("SECCODE", "SBER");
		map.put("CLASSCODE", "EQBR");
		expect(cache.getSymbol(eq("SBER"), eq("EQBR"))).andReturn(null);
		control.replay();
		
		assertNull(gateway.makeTrade(terminal, row));
		
		control.verify();
	}
	
	@Test
	public void testMakeTrade() throws Exception {
		map.put("SECCODE", "SBER");
		map.put("CLASSCODE", "EQBR");
		map.put("BUYSELL", "SELL");
		map.put("TRADENUM", 894d);
		map.put("PRICE", 45.90d);
		map.put("QTY", 1000d);
		map.put("TRADEDATE", "1998-01-15");
		map.put("TRADETIME", "00:20:19");
		map.put("VALUE", 200d);
		map.put("TRADETIME_MSEC", 214525d);
		expect(cache.getSymbol(eq("SBER"), eq("EQBR"))).andReturn(symbol);
		control.replay();
		
		Trade expected = new Trade(terminal);
		expected.setDirection(OrderAction.SELL);
		expected.setId(894L);
		expected.setPrice(45.90d);
		expected.setQty(1000L);
		expected.setSymbol(symbol);
		expected.setTime(LocalDateTime.of(1998, 1, 15, 0, 20, 19, 214000000)
				.atZone(QUIKSettings.TIMEZONE).toInstant());
		expected.setVolume(200d);
		assertEquals(expected, gateway.makeTrade(terminal, row));

		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(gateway.equals(gateway));
		assertFalse(gateway.equals(null));
		assertFalse(gateway.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<RowDataConverter> vConv = new Variant<RowDataConverter>()
			.add(converter)
			.add(control.createMock(RowDataConverter.class));
		Variant<Assembler> vAsm = new Variant<Assembler>(vConv)
			.add(asm)
			.add(control.createMock(Assembler.class));
		Variant<?> iterator = vAsm;
		int foundCnt = 0;
		TradesGateway x, found = null;
		do {
			x = new TradesGateway(vConv.get(), vAsm.get());
			if ( gateway.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(converter, found.getRowDataConverter());
		assertSame(asm, found.getAssembler());
	}

}
