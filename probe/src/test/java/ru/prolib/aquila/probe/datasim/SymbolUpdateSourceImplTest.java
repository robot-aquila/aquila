package ru.prolib.aquila.probe.datasim;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.probe.datasim.SymbolUpdateSourceImpl;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateHandler;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateHandlerFactory;

public class SymbolUpdateSourceImplTest {
	private static final Symbol symbol1 = new Symbol("AAPL"),
			symbol2 = new Symbol("MSFT"),
			symbol3 = new Symbol("SBER");
	private IMocksControl control;
	private Map<Symbol, SymbolUpdateHandler> handlerMap;
	private SymbolUpdateHandler handlerMock1, handlerMock2, handlerMock3;
	private SymbolUpdateHandlerFactory handlerFactoryMock;
	private DeltaUpdateConsumer consumerMock;
	private SymbolUpdateSourceImpl updateSource;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handlerMap = new LinkedHashMap<>();
		handlerMock1 = control.createMock(SymbolUpdateHandler.class);
		handlerMock2 = control.createMock(SymbolUpdateHandler.class);
		handlerMock3 = control.createMock(SymbolUpdateHandler.class);
		handlerFactoryMock = control.createMock(SymbolUpdateHandlerFactory.class);
		consumerMock = control.createMock(DeltaUpdateConsumer.class);
		updateSource = new SymbolUpdateSourceImpl(handlerFactoryMock, handlerMap);
	}
	
	@Test
	public void testClose() {
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
	public void testSubscribeSymbol_ExistingHandler() throws Exception {
		handlerMap.put(symbol1, handlerMock1);
		handlerMock1.subscribe(consumerMock);
		control.replay();
		
		updateSource.subscribeSymbol(symbol1, consumerMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeSymbol_NonExistentHandler() throws Exception {
		expect(handlerFactoryMock.produce(symbol1)).andReturn(handlerMock1);
		handlerMock1.subscribe(consumerMock);
		control.replay();
		
		updateSource.subscribeSymbol(symbol1, consumerMock);
		
		control.verify();
		assertSame(handlerMock1, handlerMap.get(symbol1));
	}
	
	@Test
	public void testUnsubscribeSymbol_ExistingHandler() {
		handlerMap.put(symbol1, handlerMock1);
		handlerMock1.unsubscribe(consumerMock);
		control.replay();
		
		updateSource.unsubscribeSymbol(symbol1, consumerMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeSymbol_NonExistentHandler() {
		control.replay();
		
		updateSource.unsubscribeSymbol(symbol1, consumerMock);
		
		control.verify();
	}

}
