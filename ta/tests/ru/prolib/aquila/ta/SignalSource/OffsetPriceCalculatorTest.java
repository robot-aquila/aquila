package ru.prolib.aquila.ta.SignalSource;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ta.*;

public class OffsetPriceCalculatorTest {
	TestValue<Double> src;
	AssetImpl asset;
	
	@Before
	public void setUp() throws Exception {
		src = new TestValue<Double>();
		asset = new AssetImpl("RTS", "SPBFUT", 10, 0);
	}

	@Test
	public void testGetPrice_PositiveOffset() throws Exception {
		src.addToStackAndUpdate(100.00d);
		
		OffsetPriceCalculator pc = new OffsetPriceCalculator(src, asset, 1);
		assertEquals(110.00d, pc.getPrice(), 0.01d);
		
		assertSame(src, pc.getSourceValue());
		assertSame(asset, pc.getAsset());
		assertEquals(1.0d, pc.getMul(), 0.01d);
	}
	
	@Test
	public void testGetPrice_NegativeOffset() throws Exception {
		src.addToStackAndUpdate(100.00d);
		
		OffsetPriceCalculator pc = new OffsetPriceCalculator(src, asset, -1);
		assertEquals(90.00d, pc.getPrice(), 0.01d);
		
		assertSame(src, pc.getSourceValue());
		assertSame(asset, pc.getAsset());
		assertEquals(-1.0d, pc.getMul(), 0.01d);
	}

}
