package ru.prolib.aquila.stat;

import static org.junit.Assert.*;
import org.junit.*;

public class TradeEventTest {
	TradeReport trade;
	TradeEvent event;
	
	@Before
	public void setUp() throws Exception {
		trade = new TradeReport();
		event = null;
	}
	
	@Test
	public void testNewTrade() throws Exception {
		event = TradeEvent.newTrade(trade);
		assertNotNull(event);
		assertSame(trade, event.getTradeReport());
		assertEquals(TradeEvent.NEW_TRADE, event.getEventId());
	}
	
	@Test
	public void testTradeChange() throws Exception {
		event = TradeEvent.tradeChange(trade);
		assertNotNull(event);
		assertSame(trade, event.getTradeReport());
		assertEquals(TradeEvent.TRADE_CHANGE, event.getEventId());
	}
	
	@Test
	public void testTradeClosed() throws Exception {
		event = TradeEvent.tradeClosed(trade);
		assertNotNull(event);
		assertSame(trade, event.getTradeReport());
		assertEquals(TradeEvent.TRADE_CLOSED, event.getEventId());
	}

}
