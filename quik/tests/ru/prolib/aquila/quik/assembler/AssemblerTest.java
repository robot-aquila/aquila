package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class AssemblerTest {
	private IMocksControl control;
	private SecuritiesAssembler securitiesAssembler;
	private Assembler assembler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		securitiesAssembler = control.createMock(SecuritiesAssembler.class);
		assembler = new Assembler(securitiesAssembler);
	}
	
	@Test
	public void testStart() throws Exception {
		securitiesAssembler.start();
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
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
		Variant<?> iterator = vSecAsm;
		int foundCnt = 0;
		Assembler x = null, found = null;
		do {
			x = new Assembler(vSecAsm.get());
			if ( assembler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(securitiesAssembler, found.getSecuritiesAssembler());
	}

}
