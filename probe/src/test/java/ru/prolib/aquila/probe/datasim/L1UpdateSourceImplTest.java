package ru.prolib.aquila.probe.datasim;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.probe.datasim.l1.L1UpdateHandler;
import ru.prolib.aquila.probe.datasim.l1.L1UpdateHandlerFactory;

public class L1UpdateSourceImplTest {
	private static final Symbol symbol1 = new Symbol("SBER"),
			symbol2 = new Symbol("GAZP"),
			symbol3 = new Symbol("AAPL");
	private IMocksControl control;
	private Map<Symbol, L1UpdateHandler> handlerMap;
	private L1UpdateHandler handlerMock1, handlerMock2, handlerMock3;
	private L1UpdateHandlerFactory handlerFactoryMock;
	private L1UpdateConsumer consumerMock;
	private L1UpdateSourceImpl updateSource;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handlerMap = new LinkedHashMap<>();
		handlerMock1 = control.createMock(L1UpdateHandler.class);
		handlerMock2 = control.createMock(L1UpdateHandler.class);
		handlerMock3 = control.createMock(L1UpdateHandler.class);
		handlerFactoryMock = control.createMock(L1UpdateHandlerFactory.class);
		consumerMock = control.createMock(L1UpdateConsumer.class);
		updateSource = new L1UpdateSourceImpl(handlerFactoryMock, handlerMap);
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
	public void testSetStartTime_NewHandler() throws Exception {
		Instant t = Instant.parse("2017-08-06T19:45:00Z");
		expect(handlerFactoryMock.produce(symbol1)).andReturn(handlerMock1);
		handlerMock1.setStartTime(t);
		control.replay();
		
		updateSource.setStartTimeL1(symbol1, t);
		
		control.verify();
		assertSame(handlerMock1, handlerMap.get(symbol1));
	}
	
	@Test
	public void testSetStartTime_ExistingHandler() throws Exception {
		Instant t = Instant.parse("2017-08-06T19:46:00Z");
		handlerMap.put(symbol1, handlerMock1);
		handlerMock1.setStartTime(t);
		control.replay();
		
		updateSource.setStartTimeL1(symbol1, t);
		
		control.verify();
	}

}
