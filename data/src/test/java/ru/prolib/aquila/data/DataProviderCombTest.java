package ru.prolib.aquila.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;

public class DataProviderCombTest {
	private static Symbol symbol = new Symbol("MSFT");
	private IMocksControl control;
	private SymbolUpdateSource symbolSrcMock;
	private L1UpdateSource l1SrcMock;
	private DataProvider dpMock;
	private DataProviderComb service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		symbolSrcMock = control.createMock(SymbolUpdateSource.class);
		l1SrcMock = control.createMock(L1UpdateSource.class);
		dpMock = control.createMock(DataProvider.class);
		service = new DataProviderComb(symbolSrcMock, l1SrcMock, dpMock);
	}
	
	@Test
	public void testSubscribeStateUpdates_Security() {
		EditableSecurity secMock = control.createMock(EditableSecurity.class);
		expect(secMock.getSymbol()).andReturn(symbol);
		symbolSrcMock.subscribeSymbol(symbol, secMock);
		dpMock.subscribeStateUpdates(secMock);
		control.replay();
		
		service.subscribeStateUpdates(secMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeLevel1Data() {
		L1UpdatableStreamContainer streamMock = control.createMock(L1UpdatableStreamContainer.class);
		l1SrcMock.subscribeL1(symbol, streamMock);
		dpMock.subscribeLevel1Data(symbol, streamMock);
		control.replay();
		
		service.subscribeLevel1Data(symbol, streamMock);
		
		control.verify();
	}

	@Test
	public void testSubscribeLevel2Data() {
		MDUpdatableStreamContainer streamMock = control.createMock(MDUpdatableStreamContainer.class);
		dpMock.subscribeLevel2Data(symbol, streamMock);
		control.replay();
		
		service.subscribeLevel2Data(symbol, streamMock);
		
		control.verify();
	}

	@Test
	public void testSubscribeStateUpdates_Portfolio() {
		EditablePortfolio portMock = control.createMock(EditablePortfolio.class);
		dpMock.subscribeStateUpdates(portMock);
		control.replay();
		
		service.subscribeStateUpdates(portMock);
		
		control.verify();
	}
	
	@Test
	public void testGetNextOrderID() {
		expect(dpMock.getNextOrderID()).andReturn(800L);
		control.replay();
		
		assertEquals(800L, service.getNextOrderID());
		
		control.verify();
	}
	
	@Test
	public void testSubscribeRemoteObjects() {
		EditableTerminal termMock = control.createMock(EditableTerminal.class);
		dpMock.subscribeRemoteObjects(termMock);
		control.replay();
		
		service.subscribeRemoteObjects(termMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeRemoteObjects() {
		EditableTerminal termMock = control.createMock(EditableTerminal.class);
		dpMock.unsubscribeRemoteObjects(termMock);
		control.replay();
		
		service.unsubscribeRemoteObjects(termMock);
		
		control.verify();
	}
	
	@Test
	public void testRegisterNewOrder() throws Exception {
		EditableOrder ordMock = control.createMock(EditableOrder.class);
		dpMock.registerNewOrder(ordMock);
		control.replay();
		
		service.registerNewOrder(ordMock);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		EditableOrder ordMock = control.createMock(EditableOrder.class);
		dpMock.cancelOrder(ordMock);
		control.replay();
		
		service.cancelOrder(ordMock);
		
		control.verify();
	}

}
