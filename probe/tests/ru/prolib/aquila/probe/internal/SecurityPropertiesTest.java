package ru.prolib.aquila.probe.internal;

import static org.junit.Assert.*;

import org.junit.*;

public class SecurityPropertiesTest {
	private SecurityProperties props;
	
	@Before
	public void setUp() throws Exception {
		props = new SecurityProperties();
	}
	
	@Test
	public void testSettersAndGetters() throws Exception {
		props.setDisplayName("foobar");
		props.setLotSize(10);
		props.setMinStepSize(0.001d);
		props.setStepPriceCalcBase(0.2d);
		props.setInitialMarginCalcBase(0.5d);
		props.setPricePrecision(3);
		
		assertEquals("foobar", props.getDisplayName());
		assertEquals(10, props.getLotSize());
		assertEquals(0.001d, props.getMinStepSize(), 0.001d);
		assertEquals(0.2d, props.getStepPriceCalcBase(), 0.01d);
		assertEquals(0.5d, props.getInitialMarginCalcBase(), 0.01d);
		assertEquals(3, props.getPricePrecision());
	}

}
