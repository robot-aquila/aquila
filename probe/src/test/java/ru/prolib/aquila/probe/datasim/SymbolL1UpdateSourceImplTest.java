package ru.prolib.aquila.probe.datasim;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.probe.datasim.l1.SymbolL1UpdateHandler;
import ru.prolib.aquila.probe.datasim.l1.SymbolL1UpdateHandlerFactory;

public class SymbolL1UpdateSourceImplTest {
	private static final Symbol symbol1 = new Symbol("SBER"),
			symbol2 = new Symbol("GAZP"),
			symbol3 = new Symbol("AAPL");
	private IMocksControl control;
	private Map<Symbol, SymbolL1UpdateHandler> handlerMap;
	private SymbolL1UpdateHandler handlerMock1, handlerMock2, handlerMock3;
	private SymbolL1UpdateHandlerFactory handlerFactoryMock;
	private L1UpdateConsumer consumerMock;
	private SymbolL1UpdateSourceImpl updateSource;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handlerMap = new LinkedHashMap<>();
		handlerMock1 = control.createMock(SymbolL1UpdateHandler.class);
		handlerMock2 = control.createMock(SymbolL1UpdateHandler.class);
		handlerMock3 = control.createMock(SymbolL1UpdateHandler.class);
		handlerFactoryMock = control.createMock(SymbolL1UpdateHandlerFactory.class);
		consumerMock = control.createMock(L1UpdateConsumer.class);
		updateSource = new SymbolL1UpdateSourceImpl(handlerFactoryMock, handlerMap);
	}
	
	@Test
	public void testClose() throws Exception {
		handlerMap.put(symbol1, handlerMock1);
		handlerMap.put(symbol2, handlerMock2);
		handlerMap.put(symbol3, handlerMock3);
		handlerMock1.close();
		handlerMock2.close();
		handlerMock3.close();
		control.replay();
		
		updateSource.close();
		
		control.verify();
		assertEquals(0, handlerMap.size());
	}
	
	@Test
	public void testSubscribeL1_ExistingHandler() throws Exception {
		handlerMap.put(symbol1, handlerMock1);
		handlerMock1.subscribe(consumerMock);
		control.replay();
		
		updateSource.subscribeL1(symbol1, consumerMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeL1_NonExistentHandler() throws Exception {
		expect(handlerFactoryMock.produce(symbol1)).andReturn(handlerMock1);
		handlerMock1.subscribe(consumerMock);
		control.replay();
		
		updateSource.subscribeL1(symbol1, consumerMock);
		
		control.verify();
		assertSame(handlerMock1, handlerMap.get(symbol1));
	}
	
	@Test
	public void testUnsubscribeL1_ExistingHandler() throws Exception {
		handlerMap.put(symbol1, handlerMock1);
		handlerMock1.unsubscribe(consumerMock);
		control.replay();
		
		updateSource.unsubscribeL1(symbol1, consumerMock);
		
		control.verify();
	}

	@Test
	public void testUnsubscribeL1_NonExistingHandler() throws Exception {
		control.replay();
		
		updateSource.unsubscribeL1(symbol1, consumerMock);
		
		control.verify();
	}

}
