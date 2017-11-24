package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class PriceScaleDBImplTest {
	private static Symbol symbol1 = new Symbol("RTS-12.17"), symbol2 = new Symbol("BR-12.17");
	private PriceScaleDBImpl db;
	
	@Before
	public void setUp() throws Exception {
		db = new PriceScaleDBImpl();
	}

	@Test
	public void testSetterAndGetter() {
		db.setScale(symbol1, 0);
		db.setScale(symbol2, 2);
		
		assertEquals(0, db.getScale(symbol1));
		assertEquals(2, db.getScale(symbol2));
	}

}
