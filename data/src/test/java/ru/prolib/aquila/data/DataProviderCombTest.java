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
import ru.prolib.aquila.core.utils.Variant;

public class DataProviderCombTest {
	private static Symbol symbol = new Symbol("MSFT");
	private IMocksControl control;
	private SymbolUpdateSource symbolSrcMock1, symbolSrcMock2;
	private L1UpdateSource l1SrcMock1, l1SrcMock2;
	private DataProvider dpMock1, dpMock2;
	private DataProviderComb service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		symbolSrcMock1 = control.createMock(SymbolUpdateSource.class);
		l1SrcMock1 = control.createMock(L1UpdateSource.class);
		dpMock1 = control.createMock(DataProvider.class);
		service = new DataProviderComb(symbolSrcMock1, l1SrcMock1, dpMock1);
	}
	
	@Test
	public void testCtor3() {
		assertSame(symbolSrcMock1, service.getSymbolUpdateSource());
		assertSame(l1SrcMock1, service.getL1UpdateSource());
		assertSame(dpMock1, service.getParentDataProvider());
	}
	
	@Test
	public void testSubscribeStateUpdates_Security() {
		EditableSecurity secMock = control.createMock(EditableSecurity.class);
		expect(secMock.getSymbol()).andReturn(symbol);
		symbolSrcMock1.subscribeSymbol(symbol, secMock);
		dpMock1.subscribeStateUpdates(secMock);
		control.replay();
		
		service.subscribeStateUpdates(secMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeLevel1Data() {
		L1UpdatableStreamContainer streamMock = control.createMock(L1UpdatableStreamContainer.class);
		l1SrcMock1.subscribeL1(symbol, streamMock);
		dpMock1.subscribeLevel1Data(symbol, streamMock);
		control.replay();
		
		service.subscribeLevel1Data(symbol, streamMock);
		
		control.verify();
	}

	@Test
	public void testSubscribeLevel2Data() {
		MDUpdatableStreamContainer streamMock = control.createMock(MDUpdatableStreamContainer.class);
		dpMock1.subscribeLevel2Data(symbol, streamMock);
		control.replay();
		
		service.subscribeLevel2Data(symbol, streamMock);
		
		control.verify();
	}

	@Test
	public void testSubscribeStateUpdates_Portfolio() {
		EditablePortfolio portMock = control.createMock(EditablePortfolio.class);
		dpMock1.subscribeStateUpdates(portMock);
		control.replay();
		
		service.subscribeStateUpdates(portMock);
		
		control.verify();
	}
	
	@Test
	public void testGetNextOrderID() {
		expect(dpMock1.getNextOrderID()).andReturn(800L);
		control.replay();
		
		assertEquals(800L, service.getNextOrderID());
		
		control.verify();
	}
	
	@Test
	public void testSubscribeRemoteObjects() {
		EditableTerminal termMock = control.createMock(EditableTerminal.class);
		dpMock1.subscribeRemoteObjects(termMock);
		control.replay();
		
		service.subscribeRemoteObjects(termMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeRemoteObjects() {
		EditableTerminal termMock = control.createMock(EditableTerminal.class);
		dpMock1.unsubscribeRemoteObjects(termMock);
		control.replay();
		
		service.unsubscribeRemoteObjects(termMock);
		
		control.verify();
	}
	
	@Test
	public void testRegisterNewOrder() throws Exception {
		EditableOrder ordMock = control.createMock(EditableOrder.class);
		dpMock1.registerNewOrder(ordMock);
		control.replay();
		
		service.registerNewOrder(ordMock);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		EditableOrder ordMock = control.createMock(EditableOrder.class);
		dpMock1.cancelOrder(ordMock);
		control.replay();
		
		service.cancelOrder(ordMock);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
	}
	
	@Test
	public void testEquals() {
		Variant<SymbolUpdateSource> vSUS = new Variant<>(symbolSrcMock1, symbolSrcMock2);
		Variant<L1UpdateSource> vL1S = new Variant<>(vSUS, l1SrcMock1, l1SrcMock2);
		Variant<DataProvider> vParent = new Variant<>(vL1S, dpMock1, dpMock2);
		Variant<?> iterator = vParent;
		int foundCnt = 0;
		DataProviderComb x, found = null;
		do {
			x = new DataProviderComb(vSUS.get(), vL1S.get(), vParent.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(symbolSrcMock1, found.getSymbolUpdateSource());
		assertSame(l1SrcMock1, found.getL1UpdateSource());
		assertSame(dpMock1, found.getParentDataProvider());
	}

}
