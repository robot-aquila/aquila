package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.Assembler;
import ru.prolib.aquila.quik.assembler.cache.PortfolioEntry;
import ru.prolib.aquila.quik.assembler.cache.dde.FortsPortfoliosGateway;
import ru.prolib.aquila.quik.assembler.cache.dde.RowDataConverter;

public class FortsPortfoliosGatewayTest {
	private IMocksControl control;
	private Assembler asm;
	private RowDataConverter converter;
	private FortsPortfoliosGateway gateway;
	private Map<String, Object> map;
	private Row row;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		asm = control.createMock(Assembler.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new FortsPortfoliosGateway(converter, asm);
		map = new HashMap<String, Object>();
		row = new SimpleRow(map);
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
			"TRDACCID",
			"FIRMID",
			"CBPLPLANNED",
			"CBPLIMIT",
			"VARMARGIN",
			"LIMIT_TYPE",
		};
		assertArrayEquals(expected, gateway.getRequiredHeaders());
	}
	
	@Test
	public void testShouldProcess_SkipForNotMoney() throws Exception {
		map.put("LIMIT_TYPE", "Клиринговые");
		assertFalse(gateway.shouldProcess(row));
	}
	
	@Test
	public void testShouldProcess_PassForMoney() throws Exception {
		map.put("LIMIT_TYPE", "Ден.средства");
		assertTrue(gateway.shouldProcess(row));
	}
	
	@Test
	public void testProcess() throws Exception {
		map.put("TRDACCID", "TEST");
		map.put("FIRMID", "ZULU");
		map.put("CBPLPLANNED", 18.21d);
		map.put("CBPLIMIT", 200.0d);
		map.put("VARMARGIN", -19.11d);
		PortfolioEntry expected = new PortfolioEntry(
				new Account("ZULU", "TEST", "TEST"), 200.0d, 18.21d, -19.11d);
		asm.assemble(eq(expected));
		control.replay();
		
		gateway.process(row);
		
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
		FortsPortfoliosGateway x, found = null;
		do {
			x = new FortsPortfoliosGateway(vConv.get(), vAsm.get());
			if ( gateway.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(converter, found.getRowDataConverter());
		assertSame(asm, found.getAssembler());
	}
	
	@Test
	public void testShouldProcessRowByRow() throws Exception {
		assertTrue(gateway.shouldProcessRowByRow(null, null));
	}

}
