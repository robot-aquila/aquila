package ru.prolib.aquila.ta.SignalSource;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ta.*;

public class FractalPriceCalculatorTest {
	AssetImpl asset;
	TestValue<Double> price;
	FractalPriceCalculator pc;

	@Before
	public void setUp() throws Exception {
		asset = new AssetImpl("RTS", "SPBFUT", 10, 0);
		price = new TestValue<Double>();
		pc = new FractalPriceCalculator(price, asset, -1, 5);
	}
	
	@Test
	public void testAccessors() {
		assertSame(price, pc.getSourceValue());
		assertSame(asset, pc.getAsset());
		assertEquals(-1.d, pc.getMul(), 0.01d);
		assertEquals(5, pc.getPeriods());
	}
	
	@Test
	public void testGetPrice() throws Exception {
		price.addToStackAndUpdate(40.0d);
		price.addToStackAndUpdate(35.0d);
		price.addToStackAndUpdate(20.0d);
		price.addToStackAndUpdate(10.0d);
		assertEquals(25.0d, pc.getPrice(), 0.01d);
	}

}
