package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class OrderChangeImplTest {
	private IMocksControl control;
	private EditableTerminal terminalMock;
	private EditableOrder orderMock;
	private Map<Integer, Object> tokens;
	private OrderExecution execution;
	private OrderChangeImpl change, changeWithExecution;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		orderMock = control.createMock(EditableOrder.class);
		execution = new OrderExecutionImpl(terminalMock, 100L,
				null, new Symbol("AAPL"), OrderAction.BUY, 800L, Instant.now(),
				FDecimal.of(86930, 0), 1L, FMoney.ofRUB2(121532.14));
		tokens = new HashMap<Integer, Object>();
		change = new OrderChangeImpl(orderMock, tokens);
		changeWithExecution = new OrderChangeImpl(orderMock, tokens, execution);
	}
	
	@Test
	public void testIsStatusChanged() {
		assertFalse(change.isStatusChanged());
		tokens.put(OrderField.STATUS, OrderStatus.CANCELLED);
		assertTrue(change.isStatusChanged());
	}
	
	@Test
	public void testIsFinalized() {
		assertFalse(change.isFinalized());
		tokens.put(OrderField.STATUS, OrderStatus.FILLED);
		assertTrue(change.isFinalized());
	}
	
	@Test
	public void testGetStatus() {
		tokens.put(OrderField.STATUS, OrderStatus.REJECTED);
		assertEquals(OrderStatus.REJECTED, change.getStatus());
	}
	
	@Test
	public void testGetDoneTime() {
		Instant time = Instant.now();
		tokens.put(OrderField.TIME_DONE, time);
		assertEquals(time, change.getDoneTime());
	}
	
	@Test
	public void testGetCurrentVolume() {
		tokens.put(OrderField.CURRENT_VOLUME, 100L);
		assertEquals(100L, change.getCurrentVolume());
	}

	@Test
	public void testGetExecutedValue() {
		tokens.put(OrderField.EXECUTED_VALUE, FMoney.ofRUB2(286.15));
		assertEquals(FMoney.ofRUB2(286.15), change.getExecutedValue());
	}
	
	@Test
	public void testGetSystemMessage() {
		tokens.put(OrderField.SYSTEM_MESSAGE, "foobar");
		assertEquals("foobar", change.getSystemMessage());
	}
	
	@Test
	public void testIsApplied() throws Exception {
		orderMock.update(tokens);
		control.replay();
		
		assertFalse(change.isApplied());
		change.apply();
		assertTrue(change.isApplied());
	}
	
	@Test
	public void testApply() throws Exception {
		orderMock.update(tokens);
		control.replay();
		
		change.apply();
		
		control.verify();
	}
	
	@Test (expected=ContainerTransactionException.class)
	public void testApply_ThrowsIfAlreadyApplied() throws Exception {
		orderMock.update(tokens);
		control.replay();
		
		change.apply();
		change.apply();
	}
	
	@Test
	public void testApply_WithNewExecution() throws Exception {
		orderMock.addExecution(execution);
		orderMock.update(tokens);
		orderMock.fireExecution(execution);
		control.replay();
		
		changeWithExecution.apply();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(change.equals(change));
		assertFalse(change.equals(null));
		assertFalse(change.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		control.resetToNice();
		Map<Integer, Object> tokens1 = new HashMap<>(), tokens2 = new HashMap<>();
		tokens.put(OrderField.ACTION, OrderAction.BUY);
		tokens1.put(OrderField.ACTION, OrderAction.BUY);
		tokens2.put(OrderField.COMMENT, "zulu34");
		OrderExecution execution1 = new OrderExecutionImpl(terminalMock, 100L,
				null, new Symbol("AAPL"), OrderAction.BUY, 800L,
				execution.getTime(),
				FDecimal.of(86930, 0), 1L, FMoney.ofRUB2(121532.14));
		OrderExecution execution2 = new OrderExecutionImpl(terminalMock, 100L,
				null, new Symbol("MSFT"), OrderAction.BUY, 800L,
				execution.getTime(),
				FDecimal.of(86930, 0), 1L, FMoney.ofRUB2(121532.14));
		Variant<EditableOrder> vOrd = new Variant<EditableOrder>()
				.add(orderMock)
				.add(control.createMock(EditableOrder.class));
		Variant<Map<Integer, Object>> vTok = new Variant<Map<Integer, Object>>(vOrd)
				.add(tokens1)
				.add(tokens2);
		Variant<OrderExecution> vExe = new Variant<OrderExecution>(vTok)
				.add(execution1)
				.add(execution2)
				.add(null);
		Variant<Boolean> vAppl = new Variant<Boolean>(vExe)
				.add(false)
				.add(true);
		Variant<?> iterator = vAppl;
		int foundCnt = 0;
		OrderChangeImpl found = null, x;
		do {
			x = new OrderChangeImpl(vOrd.get(), vTok.get(), vExe.get());
			if ( vAppl.get() ) {
				x.apply();
			}
			if ( changeWithExecution.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(orderMock, found.getOrder());
		assertEquals(tokens, found.getTokens());
		assertEquals(execution, found.getExecution());
		assertFalse(found.isApplied());
	}

}
