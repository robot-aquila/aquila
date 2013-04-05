package ru.prolib.aquila.ta;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

public class SignalSourceListTest {
	
	static class TestSignalSource implements ISignalSource {
		public int type = Signal.BUY;
		public double price = 50.0d;
		public String comment = "test";

		@Override
		public void analyze(ISignalTranslator translator) {
			if ( type == Signal.BUY ) {
				translator.signalToBuy(price, comment);
			} else {
				translator.signalToSell(price, comment);
			}
		}
		
	}
	
	private SignalSourceList list;

	@Before
	public void setUp() throws Exception {
		list = new SignalSourceList();
	}
	
	@Test
	public void testGetCurrentSignals() throws Exception {
		TestSignalSource g1 = new TestSignalSource();
		g1.type = Signal.SELL;
		g1.price = 120.05d;
		g1.comment = "first generator";
		
		TestSignalSource g2 = new TestSignalSource();
		g2.type = Signal.BUY;
		g2.price = 100.05d;
		g2.comment = "second generator";
		
		assertEquals(1, list.addSignalSource(g1));
		assertEquals(2, list.addSignalSource(g2));
		
		List<Signal> signals = list.getCurrentSignals().getSignals();
		assertEquals(2, signals.size());
		Signal s = signals.get(0);
		assertEquals(1, s.getSourceId());
		assertEquals(Signal.SELL, s.getType());
		assertEquals(120.05d, s.getPrice(), 0.01d);
		assertEquals("first generator", s.getComment());
		
		s = signals.get(1);
		assertEquals(2, s.getSourceId());
		assertEquals(Signal.BUY, s.getType());
		assertEquals(100.05d, s.getPrice(), 0.01d);
		assertEquals("second generator", s.getComment());
	}

}
