package ru.prolib.aquila.ta;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.*;

import ru.prolib.aquila.ta.DealImpl;

public class DealImplTest {

	@Test
	public void testAccessors() {
		Date time = new Date();
		DealImpl deal = new DealImpl(time, 123.45d, 10000);
		assertEquals(123.45d, deal.getPrice(), 0.01d);
		assertEquals(10000, deal.getQuantity());
		Date t2 = deal.getTime();
		assertNotSame(time, t2);
		assertEquals(time, t2);
		assertNotSame(t2, deal.getTime());
	}

}
