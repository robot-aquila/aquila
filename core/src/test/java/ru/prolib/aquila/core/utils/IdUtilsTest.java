package ru.prolib.aquila.core.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.LocalDate;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.IdUtils;

public class IdUtilsTest {
	private IMocksControl control;
	private StrCoder coder;
	private IdUtils utilsWithMocks, utils;
	private Symbol symbol;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		coder = control.createMock(StrCoder.class);
		utilsWithMocks = new IdUtils(coder);
		utils = new IdUtils();
	}
	
	@Test
	public void testCtor1() throws Exception {
		assertSame(coder, utilsWithMocks.getStrCoder());
	}
	
	@Test
	public void testCtor0() throws Exception {
		utilsWithMocks = new IdUtils();
		assertNotNull(utilsWithMocks.getStrCoder());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetSafeId1_Symbol() throws Exception {
		symbol = new Symbol("S:XXX@YYY:GBP");
		expect(coder.encode("XXX")).andReturn("P1");
		expect(coder.encode("YYY")).andReturn("P2");
		expect(coder.encode("GBP")).andReturn("P3");
		expect(coder.encode("S")).andReturn("P4");
		control.replay();

		String expected = "P1-P2-P3-P4", actual = utilsWithMocks.getSafeId(symbol);
		assertEquals(expected, actual);
		
		control.verify();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetSafeId2() throws Exception {
		symbol = new Symbol("F:RTS@ZZZ:USD");
		expect(coder.encode("RTS")).andReturn("X1");
		expect(coder.encode("ZZZ")).andReturn("X2");
		expect(coder.encode("USD")).andReturn("X3");
		expect(coder.encode("F")).andReturn("X4");
		control.replay();
		
		String expected = "X1-X2-X3-X4-20120805",
				actual = utilsWithMocks.getSafeId(symbol, LocalDate.of(2012, 8, 5));
		assertEquals(expected, actual);
		
		control.verify();
	}
	
	@Test
	public void testAppendSeparator() throws Exception {
		assertEquals("foo-", utilsWithMocks.appendSeparator("foo"));
	}
	
	@Test
	public void testGetSafeSymbolId() throws Exception {
		String expected = "F%3ARTS%40SPBFUT%3AUSD";
		assertEquals(expected, utils.getSafeSymbolId(new Symbol("F:RTS@SPBFUT:USD")));
		
		expected = "RTS%40SPBFUT%3AUSD";
		assertEquals(expected, utils.getSafeSymbolId(new Symbol("RTS@SPBFUT:USD")));
		
		expected = "F%3ARTS%40SPBFUT";
		assertEquals(expected, utils.getSafeSymbolId(new Symbol("F:RTS@SPBFUT")));
		
		expected = "RTS%40SPBFUT";
		assertEquals(expected, utils.getSafeSymbolId(new Symbol("RTS@SPBFUT")));
		
		expected = "RTS";
		assertEquals(expected, utils.getSafeSymbolId(new Symbol("RTS")));
	}
	
	@Test
	public void testToSymbol() throws Exception {
		assertEquals(new Symbol("F:RTS@SPBFUT:USD"), utils.toSymbol("F%3ARTS%40SPBFUT%3AUSD"));
		assertEquals(new Symbol("RTS@SPBFUT:USD"), utils.toSymbol("RTS%40SPBFUT%3AUSD"));
		assertEquals(new Symbol("F:RTS@SPBFUT"), utils.toSymbol("F%3ARTS%40SPBFUT"));
		assertEquals(new Symbol("RTS@SPBFUT"), utils.toSymbol("RTS%40SPBFUT"));
		assertEquals(new Symbol("RTS"), utils.toSymbol("RTS"));
	}
	
	@Test
	public void testGetSafeFilename3() throws Exception {
		String expected = "F%3ARTS%40SPBFUT%3AUSD-20071015.csv.gz";
		
		String actual = utils.getSafeFilename(new Symbol("F:RTS@SPBFUT:USD"),
				LocalDate.of(2007, 10, 15), ".csv.gz");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetSafeFilename2() throws Exception {
		String expected = "F%3ARTS%40SPBFUT%3AUSD-moex-daily-session.txt";
		
		String actual = utils.getSafeFilename(new Symbol("F:RTS@SPBFUT:USD"), "-moex-daily-session.txt");
		
		assertEquals(expected, actual);		
	}
	
	@Test
	public void testIsSafeFilename3() throws Exception {
		symbol = new Symbol("F:RTS@SPBFUT:USD");
		assertTrue(utils.isSafeFilename3("F%3ARTS%40SPBFUT%3AUSD-20071015.csv.gz", symbol, ".csv.gz"));
		assertFalse(utils.isSafeFilename3("F%3ARTS%40SPBFUT%3AUSD-2007xx15.csv.gz", symbol, ".csv.gz"));
	}

	@Test
	public void testParseSafeFilename3() throws Exception {
		symbol = new Symbol("F:RTS@SPBFUT:USD");
		
		LocalDate actual = utils.parseSafeFilename3("F%3ARTS%40SPBFUT%3AUSD-20071015.csv.gz", symbol, ".csv.gz");
		
		assertEquals(LocalDate.of(2007, 10, 15), actual);
	}

}
