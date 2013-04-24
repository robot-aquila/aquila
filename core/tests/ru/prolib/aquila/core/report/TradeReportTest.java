package ru.prolib.aquila.core.report;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * $Id$
 */
public class TradeReportTest {

	private SecurityDescriptor descr;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		descr = new SecurityDescriptor("Foo", "FooClass", "Bar", SecurityType.UNK);		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor() {
		TradeReport report = new TradeReport(PositionType.LONG, descr);
		assertEquals(PositionType.LONG, report.getType());
		assertEquals(descr, report.getSecurity());
		assertEquals((Long) 0L, report.getQty());
		assertEquals((Double) 0.0d, report.getAverageClosePrice());
		assertEquals((Double) 0.0d, report.getAverageOpenPrice());
		assertEquals((Double) 0.0d, report.getOpenVolume());
		assertEquals((Double) 0.0d, report.getCloseVolume());
	}

}
