package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class PriceScaleDBLazyTest {
	private static Symbol symbol = new Symbol("GAZP");
	private IMocksControl control;
	private PriceScaleDB parentDBMock;
	private PriceScaleDBLazy service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parentDBMock = control.createMock(PriceScaleDB.class);
		service = new PriceScaleDBLazy();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetScale_ThrowsIfParentIsNoSet() {
		service.getScale(symbol);
	}

	@Test
	public void testGetScale() {
		service.setParentDB(parentDBMock);
		expect(parentDBMock.getScale(symbol)).andReturn(4);
		control.replay();
		
		assertEquals(4, service.getScale(symbol));
		
		control.verify();
	}

}
