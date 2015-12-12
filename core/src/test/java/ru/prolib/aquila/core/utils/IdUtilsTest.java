package ru.prolib.aquila.core.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.IdUtils;

public class IdUtilsTest {
	private IMocksControl control;
	private StrCoder coder;
	private IdUtils utils;
	private Symbol symbol;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		coder = control.createMock(StrCoder.class);
		utils = new IdUtils(coder);
	}
	
	@Test
	public void testCtor1() throws Exception {
		assertSame(coder, utils.getStrCoder());
	}
	
	@Test
	public void testCtor0() throws Exception {
		utils = new IdUtils();
		assertNotNull(utils.getStrCoder());
	}

	@Test
	public void testGetSafeId1() throws Exception {
		symbol = new Symbol("XXX", "YYY", "GBP");
		expect(coder.encode("XXX")).andReturn("P1");
		expect(coder.encode("YYY")).andReturn("P2");
		expect(coder.encode("GBP")).andReturn("P3");
		expect(coder.encode("STK")).andReturn("P4");
		control.replay();

		String expected = "P1-P2-P3-P4", actual = utils.getSafeId(symbol);
		assertEquals(expected, actual);
		
		control.verify();
	}
	
	@Test
	public void testGetSafeId2() throws Exception {
		symbol = new Symbol("RTS", "ZZZ", "USD", SymbolType.FUT);
		expect(coder.encode("RTS")).andReturn("X1");
		expect(coder.encode("ZZZ")).andReturn("X2");
		expect(coder.encode("USD")).andReturn("X3");
		expect(coder.encode("FUT")).andReturn("X4");
		control.replay();
		
		String expected = "X1-X2-X3-X4-20120805",
				actual = utils.getSafeId(symbol, new LocalDate(2012, 8, 5));
		assertEquals(expected, actual);
		
		control.verify();
	}
	
	@Test
	public void testAppendSeparator() throws Exception {
		assertEquals("foo-", utils.appendSeparator("foo"));
	}

}
