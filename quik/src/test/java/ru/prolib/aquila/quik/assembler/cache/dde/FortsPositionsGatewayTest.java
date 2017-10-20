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
import ru.prolib.aquila.quik.assembler.cache.PositionEntry;
import ru.prolib.aquila.quik.assembler.cache.dde.FortsPositionsGateway;
import ru.prolib.aquila.quik.assembler.cache.dde.RowDataConverter;

public class FortsPositionsGatewayTest {
	private IMocksControl control;
	private RowDataConverter converter;
	private FortsPositionsGateway gateway;
	private Assembler asm;
	private Map<String, Object> map;
	private Row row;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		asm = control.createMock(Assembler.class);
		converter = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		gateway = new FortsPositionsGateway(converter, asm);
		map = new HashMap<String, Object>();
		row = new SimpleRow(map);
	}
	
	@Test
	public void testGetRequiredHeaders() throws Exception {
		String expected[] = {
			"TRDACCID",
			"FIRMID",
			"SEC_SHORT_NAME",
			"START_NET",
			"TOTAL_NET",
			"VARMARGIN",
		};
		assertArrayEquals(expected, gateway.getRequiredHeaders());
	}
	
	@Test
	public void testShouldProcess() throws Exception {
		assertTrue(gateway.shouldProcess(row));
	}
	
	@Test
	public void testProcess() throws Exception {
		map.put("TRDACCID", "eqe02");
		map.put("FIRMID", "BCS01");
		map.put("SEC_SHORT_NAME", "RIM3");
		map.put("START_NET", 12.0d);
		map.put("TOTAL_NET", 6.0d);
		map.put("VARMARGIN", 134.96d);
		PositionEntry expected = new PositionEntry(
				new Account("BCS01", "eqe02", "eqe02"), "RIM3",
				12L, 6L, 134.96d);
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
		FortsPositionsGateway x, found = null;
		do {
			x = new FortsPositionsGateway(vConv.get(), vAsm.get());
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
