package ru.prolib.aquila.ta;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;

public class SignalListTest {
	private SignalList sa;
	private Signal s;

	@Before
	public void setUp() throws Exception {
		sa = new SignalList();
	}
	
	private void fillSignalList() {
		sa.setSourceId(1);
		sa.signalToBuy(100.10d);
		sa.signalToSell(108.24d);
		sa.signalToSell(108.24d);
		sa.setSourceId(2);
		sa.signalToBuy(242.45d);
		sa.signalToSell(245.62d);
		sa.signalToBuy(344.45d);
	}
	
	@Test
	public void testSignalToBuy_WoComment() {
		sa.setSourceId(500);
		sa.signalToBuy(123.456);
		List<Signal> list = sa.getSignals();
		assertEquals(1, list.size());
		s = sa.getSignals().get(0);
		assertEquals(500, s.getSourceId());
		assertEquals(Signal.BUY, s.getType());
		assertEquals(123.456d, s.getPrice(), 0.001d);
	}
	
	@Test
	public void testSignalToBuy_WithComment() {
		sa.setSourceId(500);
		sa.signalToBuy(123.456, "test signal comment");
		List<Signal> list = sa.getSignals();
		assertEquals(1, list.size());
		s = sa.getSignals().get(0);
		assertEquals(500, s.getSourceId());
		assertEquals(Signal.BUY, s.getType());
		assertEquals(123.456d, s.getPrice(), 0.001d);
		assertEquals("test signal comment", s.getComment());
	}
	
	@Test
	public void testSignalToSell_WoComment() {
		sa.signalToSell(56.32);
		List<Signal> list = sa.getSignals();
		assertEquals(1, list.size());
		s = sa.getSignals().get(0);
		assertEquals(-1, s.getSourceId());
		assertEquals(Signal.SELL, s.getType());
		assertEquals(56.32d, s.getPrice(), 0.01d);
	}
	
	@Test
	public void testSignalToSell_WithComment() {
		sa.signalToSell(56.32, "hello signal");
		List<Signal> list = sa.getSignals();
		assertEquals(1, list.size());
		s = sa.getSignals().get(0);
		assertEquals(-1, s.getSourceId());
		assertEquals(Signal.SELL, s.getType());
		assertEquals(56.32d, s.getPrice(), 0.01d);
		assertEquals("hello signal", s.getComment());
	}
	
	@Test
	public void testClearSignals() {
		sa.setSourceId(100500);
		sa.signalToBuy(12.34d);
		sa.signalToSell(45.33d);
		sa.clearSignals();
		sa.signalToSell(56.32);
		List<Signal> list = sa.getSignals();
		assertEquals(1, list.size());
		s = sa.getSignals().get(0);
		assertEquals(-1, s.getSourceId());
		assertEquals(Signal.SELL, s.getType());
		assertEquals(56.32d, s.getPrice(), 0.01d);
	}
	
	@Test
	public void testGetSignals_ReturnsListCopy() {
		sa.signalToBuy(12.33d);
		List<Signal> list = sa.getSignals();
		assertNotSame(list, sa.getSignals());
		assertEquals(list, sa.getSignals());
	}
	
	@Test
	public void testFind() {
		fillSignalList();
		List<Signal> full = sa.getSignals();
		
		List<Signal> list = sa.find(2, Signal.BUY);
		assertEquals(2, list.size());
		assertSame(full.get(3), list.get(0));
		assertSame(full.get(5), list.get(1));
		
		list = sa.find(null, Signal.SELL);
		assertEquals(3, list.size());
		assertSame(full.get(1), list.get(0));
		assertSame(full.get(2), list.get(1));
		assertSame(full.get(4), list.get(2));

		list = sa.find(2, null);
		assertEquals(3, list.size());
		assertSame(full.get(3), list.get(0));
		assertSame(full.get(4), list.get(1));
		assertSame(full.get(5), list.get(2));
	}
	
	@Test
	public void testFindOne() {
		fillSignalList();
		List<Signal> full = sa.getSignals();
		
		assertSame(full.get(4), sa.findOne(2, Signal.SELL));
		assertSame(full.get(0), sa.findOne(null, Signal.BUY));
		assertSame(full.get(3), sa.findOne(2, null));
		assertSame(full.get(0), sa.findOne(null, null));
		assertNull(sa.findOne(100500, null));
		
	}

}
