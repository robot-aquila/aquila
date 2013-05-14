package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.dde.*;

public class AssemblerTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private Cache cache;
	private SecuritiesAssembler securitiesAssembler;
	private PortfoliosAssembler portfoliosAssembler;
	private PositionsAssembler positionsAssembler;
	private Assembler assembler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		securitiesAssembler = control.createMock(SecuritiesAssembler.class);
		portfoliosAssembler = control.createMock(PortfoliosAssembler.class);
		positionsAssembler = control.createMock(PositionsAssembler.class);
		assembler = new Assembler(securitiesAssembler,
				portfoliosAssembler,
				positionsAssembler);
	}
	
	@Test
	public void testStart() throws Exception {
		securitiesAssembler.start();
		portfoliosAssembler.start();
		positionsAssembler.start();
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		positionsAssembler.stop();
		portfoliosAssembler.stop();
		securitiesAssembler.stop();
		control.replay();
		
		assembler.stop();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(assembler.equals(assembler));
		assertFalse(assembler.equals(null));
		assertFalse(assembler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<SecuritiesAssembler> vSecAsm =
				new Variant<SecuritiesAssembler>()
			.add(securitiesAssembler)
			.add(control.createMock(SecuritiesAssembler.class));
		Variant<PortfoliosAssembler> vPortAsm =
				new Variant<PortfoliosAssembler>(vSecAsm)
			.add(portfoliosAssembler)
			.add(control.createMock(PortfoliosAssembler.class));
		Variant<PositionsAssembler> vPosAsm =
				new Variant<PositionsAssembler>(vPortAsm)
			.add(positionsAssembler)
			.add(control.createMock(PositionsAssembler.class));
		Variant<?> iterator = vPosAsm;
		int foundCnt = 0;
		Assembler x = null, found = null;
		do {
			x = new Assembler(vSecAsm.get(), vPortAsm.get(), vPosAsm.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(securitiesAssembler, found.getSecuritiesAssembler());
		assertSame(portfoliosAssembler, found.getPortfoliosAssembler());
		assertSame(positionsAssembler, found.getPositionsAssembler());
	}
	
	@Test
	public void testConstructor_Minimal() throws Exception {
		Assembler expected = new Assembler(new SecuritiesAssembler(cache,
				new SecurityAssembler(terminal, cache)),
			new PortfoliosAssembler(cache,
				new PortfolioAssembler(terminal, cache)),
			new PositionsAssembler(cache,
				new PositionAssembler(terminal, cache)));
		Assembler actual = new Assembler(terminal, cache);
		assertEquals(expected, actual);
	}

}
