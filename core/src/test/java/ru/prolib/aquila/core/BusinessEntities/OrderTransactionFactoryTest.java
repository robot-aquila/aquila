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
		order = (EditableOrder) terminal.createOrder(account, symbol, OrderAction.SELL, 90L);
		factory = new OrderTransactionFactory();
	}
	
	@Test
	public void testCreateNewExecution7_FirstExecution() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol,
				OrderAction.SELL, 10L, FDecimal.of2(45.34));
		
		Instant time = Instant.parse("2016-07-05T00:00:01Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.CURRENT_VOLUME, 8L);
		tokens.put(OrderField.EXECUTED_VALUE, FMoney.ofRUB2(20.19));
		OrderChange expected = new OrderChangeImpl(order, tokens, new OrderExecutionImpl(terminal,
				2000L, "foo", symbol, OrderAction.SELL, order.getID(), time,
				FDecimal.of2(40.12), 2L, FMoney.ofRUB2(20.19)));
		
		OrderChange actual = factory.createNewExecution(order, 2000L, "foo",
				time, FDecimal.of2(40.12), 2L, FMoney.ofRUB2(20.19));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateNewExecution7_NextExecution() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol,
				OrderAction.BUY, 10L, FDecimal.of2(45.34));
		factory.createNewExecution(order, 1000L, "EXC01", Instant.EPOCH,
				FDecimal.of2(42.15), 2L, FMoney.ofRUB2(18.92)).apply();
		
		Instant time = Instant.parse("2016-07-05T22:22:35Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.CURRENT_VOLUME, 3L);
		tokens.put(OrderField.EXECUTED_VALUE, FMoney.ofRUB2(31.11));
		OrderChange expected = new OrderChangeImpl(order, tokens, new OrderExecutionImpl(terminal,
				1001L, "EXC02", symbol, OrderAction.BUY, order.getID(), time,
				FDecimal.of2(43.86), 5L, FMoney.ofRUB2(12.19)));
		
		OrderChange actual = factory.createNewExecution(order, 1001L, "EXC02",
				time, FDecimal.of2(43.86), 5L, FMoney.ofRUB2(12.19));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateNewExecution7_LastExecution() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol,
				OrderAction.BUY, 10L, FDecimal.of2(45.34));
		factory.createNewExecution(order, 1000L, "EXC01", Instant.EPOCH,
				FDecimal.of2(42.15), 2L, FMoney.ofUSD3(18.92)).apply();
		factory.createNewExecution(order, 1001L, "EXC02", Instant.EPOCH,
				FDecimal.of2(43.86), 5L, FMoney.ofUSD3(12.19)).apply();
		
		Instant time = Instant.parse("2016-07-05T22:32:00Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.CURRENT_VOLUME, 0L);
		tokens.put(OrderField.EXECUTED_VALUE, FMoney.ofUSD3(47.4)); // 16.29d
		tokens.put(OrderField.STATUS, OrderStatus.FILLED);
		tokens.put(OrderField.TIME_DONE, time);
		OrderChange expected = new OrderChangeImpl(order, tokens, new OrderExecutionImpl(terminal,
				1002L, "EXC03", symbol, OrderAction.BUY, order.getID(), time,
				FDecimal.of2(43.52), 3L, FMoney.ofUSD3(16.29)));
		
		OrderChange actual = factory.createNewExecution(order, 1002L, "EXC03",
				time, FDecimal.of2(43.52), 3L, FMoney.ofUSD3(16.29));

		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateNewExecution5() throws Exception {
		order = (EditableOrder) terminal.createOrder(account, symbol, OrderAction.SELL, 90L);
		
		Instant approxTime = terminal.getCurrentTime();
		
		OrderChange actual = factory.createNewExecution(order, 800L,
				FDecimal.of2(720.0), 20L, FMoney.ofEUR4(1215.34));
		
		assertEquals(70, actual.getCurrentVolume());
		assertEquals(FMoney.ofEUR4(1215.34), actual.getExecutedValue());
		OrderExecution actualExecution = actual.getExecution();
		assertEquals(OrderAction.SELL, actualExecution.getAction());
		assertNull(actualExecution.getExternalID());
		assertEquals(800L, actualExecution.getID());
		assertEquals(order.getID(), actualExecution.getOrderID());
		assertEquals(FDecimal.of2(720.0), actualExecution.getPricePerUnit());
		assertEquals(symbol, actualExecution.getSymbol());
		assertEquals(terminal, actualExecution.getTerminal());
		assertTrue(ChronoUnit.MILLIS.between(approxTime, actualExecution.getTime()) <= 200L);
		assertEquals(FMoney.ofEUR4(1215.34), actualExecution.getValue());
		assertEquals(20L, actualExecution.getVolume());
	}
	
	@Test
	public void testCreateFinalization4() throws Exception {
		Instant time = Instant.parse("2016-07-06T00:00:00Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.STATUS, OrderStatus.CANCELLED);
		tokens.put(OrderField.TIME_DONE, time);
		tokens.put(OrderField.SYSTEM_MESSAGE, "Cancelled");
		OrderChange expected = new OrderChangeImpl(order, tokens);
		
		OrderChange actual = factory.createFinalization(order, OrderStatus.CANCELLED, time, "Cancelled");

		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateFinalization2() throws Exception {
		OrderChange actual = factory.createFinalization(order, OrderStatus.REJECTED);
		
		assertEquals(OrderStatus.REJECTED, actual.getStatus());
		assertTrue(ChronoUnit.MILLIS.between(terminal.getCurrentTime(), actual.getDoneTime()) < 200L);
	}
	
	@Test
	public void testCreateCancellation2() throws Exception {
		Instant time = Instant.parse("2016-07-06T00:00:00Z");
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.STATUS, OrderStatus.CANCELLED);
		tokens.put(OrderField.TIME_DONE, time);
		OrderChange expected = new OrderChangeImpl(order, tokens);
		
		OrderChange actual = factory.createCancellation(order, time);

		assertEquals(expected, actual);
	}

	@Test
	public void testCreateCancellation1() throws Exception {
		OrderChange actual = factory.createCancellation(order);
		
		assertEquals(OrderStatus.CANCELLED, actual.getStatus());
		assertTrue(ChronoUnit.MILLIS.between(terminal.getCurrentTime(), actual.getDoneTime()) < 200L);
	}

	@Test
	public void testCreateRegistration1() throws Exception {
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(OrderField.STATUS, OrderStatus.ACTIVE);
		OrderChange expected = new OrderChangeImpl(order, tokens);
		
		OrderChange actual = factory.createRegistration(order);

		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateRejection2() throws Exception {
		OrderChange actual = factory.createRejection(order, "Rejected");
		
		assertEquals(OrderStatus.REJECTED, actual.getStatus());
		assertTrue(ChronoUnit.MILLIS.between(terminal.getCurrentTime(), actual.getDoneTime()) < 200L);
		assertEquals("Rejected", actual.getSystemMessage());
	}
	
}
