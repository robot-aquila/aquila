package ru.prolib.aquila.web.utils.moex.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

public class MoexPriceScaleDBTest {
	static Symbol symbol1, symbol2, symbol3, symbol4, symbol5, symbol6;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		symbol1 = new Symbol("Eu-9.16");	// 03
		symbol2 = new Symbol("RTS-3.17");	// 74
		symbol3 = new Symbol("GAZP");		// 8C (not found)
		symbol4 = new Symbol("RVI-11.17");	// D4
		symbol5 = new Symbol("UCAD-12.16");	// 9B, scale=4
		symbol6 = new Symbol("CU-6.18");	// 9B, scale=0
	}

	@Rule public ExpectedException eex = ExpectedException.none();
	IMocksControl control;
	MoexContractFileStorage storageMock, storage;
	Map<Symbol, Integer> scaleMap;
	MoexPriceScaleDB service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		storageMock = control.createMock(MoexContractFileStorage.class);
		storage = new MoexContractFileStorage(new File("fixture"));
		scaleMap = new HashMap<>();
		service = new MoexPriceScaleDB(storage, scaleMap);
	}
	
	@Test
	public void testGetScale_AlreadyLoaded() {
		scaleMap.put(symbol3, 2);
		
		assertEquals(2, service.getScale(symbol3));
	}
	
	@Test
	public void testGetScale_SymbolNotFound_Cached() {
		scaleMap.put(symbol1, MoexPriceScaleDB.SYMBOL_NOT_FOUND);
		
		assertEquals(MoexPriceScaleDB.DEFAULT_SCALE, service.getScale(symbol1));
	}
	
	@Test
	public void testGetScale_SymbolDataError_Cached() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Reading symbol data error (permanent): GAZP");
		scaleMap.put(symbol3, MoexPriceScaleDB.SYMBOL_DATA_ERROR);
		
		service.getScale(symbol3);
	}
	
	@Test
	public void testGetScale_SymbolDataError() throws Exception {
		service = new MoexPriceScaleDB(storageMock, scaleMap);
		expect(storageMock.createReader(symbol5)).andThrow(new IOException("Test error"));
		control.replay();

		try {
			service.getScale(symbol5);
			fail("Expected exception: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("Reading symbol data error (permanent): UCAD-12.16", e.getMessage());
		}
		assertEquals(MoexPriceScaleDB.SYMBOL_DATA_ERROR, (int) scaleMap.get(symbol5));
	}
	
	@Test
	public void testGetScale_SymbolNotFound() {
		assertEquals(MoexPriceScaleDB.DEFAULT_SCALE, service.getScale(symbol3));
		
		assertEquals(MoexPriceScaleDB.SYMBOL_NOT_FOUND, (int) scaleMap.get(symbol3));
	}
	
	@Test
	public void testGetScale_Load() {
		assertEquals(0, service.getScale(symbol1));
		assertEquals(0, service.getScale(symbol2));
		assertEquals(2, service.getScale(symbol4));
		assertEquals(4, service.getScale(symbol5));
		assertEquals(0, service.getScale(symbol6));
		
		assertEquals(0, (int) scaleMap.get(symbol1));
		assertEquals(0, (int) scaleMap.get(symbol2));
		assertEquals(2, (int) scaleMap.get(symbol4));
		assertEquals(4, (int) scaleMap.get(symbol5));
		assertEquals(0, (int) scaleMap.get(symbol6));
	}

}
