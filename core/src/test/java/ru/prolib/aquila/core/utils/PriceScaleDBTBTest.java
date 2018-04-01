package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityNotExistsException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class PriceScaleDBTBTest {
	private static Symbol symbol = new Symbol("SBER");
	
	private IMocksControl control;
	private Terminal terminalMock;
	private Security securityMock;
	private PriceScaleDBTB service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(Terminal.class);
		securityMock = control.createMock(Security.class);
		service = new PriceScaleDBTB(terminalMock);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetScale_ThrowsIfSecurityNotExists() throws Exception {
		expect(terminalMock.getSecurity(symbol)).andThrow(new SecurityNotExistsException(symbol));
		control.replay();
		
		service.getScale(symbol);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetScale_ThrowsIfSecurityNotAvailable() throws Exception {
		expect(terminalMock.getSecurity(symbol)).andReturn(securityMock);
		expect(securityMock.isAvailable()).andReturn(false);
		control.replay();
		
		service.getScale(symbol);
	}
	
	@Test
	public void testGetScale() throws Exception {
		expect(terminalMock.getSecurity(symbol)).andReturn(securityMock);
		expect(securityMock.isAvailable()).andReturn(true);
		expect(securityMock.getScale()).andReturn(5);
		control.replay();
		
		assertEquals(5, service.getScale(symbol));
		
		control.verify();
	}

}
