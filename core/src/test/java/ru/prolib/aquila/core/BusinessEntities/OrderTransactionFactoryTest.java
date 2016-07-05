package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.DataProviderStub;

public class OrderTransactionFactoryTest {
	private static Account account = new Account("DL-01");
	private static Symbol symbol = new Symbol("SBRF");
	private Terminal terminal;
	private EditableOrder order;
	private OrderTransactionFactory factory;

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		order = null;
		factory = new OrderTransactionFactory();
	}
	
	@Test
	public void testCreateNewExecution7_FirstExecution() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol, OrderAction.SELL, 10L, 45.34d);
		
		Instant time = Instant.parse("2016-07-05T00:00:01Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.CURRENT_VOLUME, 8L);
		tokens.put(OrderField.EXECUTED_VALUE, 20.19d);
		OrderChange expected = new OrderChangeImpl(order, tokens, new OrderExecutionImpl(terminal,
				2000L, "foo", symbol, OrderAction.SELL, order.getID(), time, 40.12d, 2L, 20.19d));
		
		OrderChange actual = factory.createNewExecution(order, 2000L, "foo", time, 40.12d, 2L, 20.19d);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateNewExecution7_NextExecution() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol, OrderAction.BUY, 10L, 45.34d);
		factory.createNewExecution(order, 1000L, "EXC01", Instant.EPOCH, 42.15d, 2L, 18.92d).apply();
		
		Instant time = Instant.parse("2016-07-05T22:22:35Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.CURRENT_VOLUME, 3L);
		tokens.put(OrderField.EXECUTED_VALUE, 31.11d);
		OrderChange expected = new OrderChangeImpl(order, tokens, new OrderExecutionImpl(terminal,
				1001L, "EXC02", symbol, OrderAction.BUY, order.getID(), time, 43.86d, 5L, 12.19d));
		
		OrderChange actual = factory.createNewExecution(order, 1001L, "EXC02", time, 43.86d, 5L, 12.19d);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateNewExecution7_LastExecution() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol, OrderAction.BUY, 10L, 45.34d);
		factory.createNewExecution(order, 1000L, "EXC01", Instant.EPOCH, 42.15d, 2L, 18.92d).apply();
		factory.createNewExecution(order, 1001L, "EXC02", Instant.EPOCH, 43.86d, 5L, 12.19d).apply();
		
		Instant time = Instant.parse("2016-07-05T22:32:00Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.CURRENT_VOLUME, 0L);
		tokens.put(OrderField.EXECUTED_VALUE, 47.4d); // 16.29d
		tokens.put(OrderField.STATUS, OrderStatus.FILLED);
		tokens.put(OrderField.TIME_DONE, time);
		OrderChange expected = new OrderChangeImpl(order, tokens, new OrderExecutionImpl(terminal,
				1002L, "EXC03", symbol, OrderAction.BUY, order.getID(), time, 43.52d, 3L, 16.29d));
		
		OrderChange actual = factory.createNewExecution(order, 1002L, "EXC03", time, 43.52d, 3L, 16.29d);

		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateNewExecution5() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol, OrderAction.SELL, 90L);
		
		Instant approxTime = terminal.getCurrentTime();
		
		OrderChange actual = factory.createNewExecution(order, 800L, 720.0d, 20L, 1215.34d);
		
		assertEquals(70, actual.getCurrentVolume());
		assertEquals(1215.34d, actual.getExecutedValue(), 0.01d);
		OrderExecution actualExecution = actual.getExecution();
		assertEquals(OrderAction.SELL, actualExecution.getAction());
		assertNull(actualExecution.getExternalID());
		assertEquals(800L, actualExecution.getID());
		assertEquals(order.getID(), actualExecution.getOrderID());
		assertEquals(720.0d, actualExecution.getPricePerUnit(), 0.01d);
		assertEquals(symbol, actualExecution.getSymbol());
		assertEquals(terminal, actualExecution.getTerminal());
		assertTrue(ChronoUnit.MILLIS.between(approxTime, actualExecution.getTime()) <= 200L);
		assertEquals(1215.34d, actualExecution.getValue(), 0.01d);
		assertEquals(20L, actualExecution.getVolume());
	}

}
