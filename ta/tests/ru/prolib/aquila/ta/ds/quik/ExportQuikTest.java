package ru.prolib.aquila.ta.ds.quik;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.rxltdde.Xlt.Table;
import ru.prolib.aquila.ta.DealImpl;
import ru.prolib.aquila.ta.ds.DealWriter;

public class ExportQuikTest {
	private ExportQuik disp;
	private ExportQuikActualityPoint ap;
	private IMocksControl control; 
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		//Logger.getRootLogger().setLevel(Level.ALL);
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		disp = new ExportQuik("allDeals");
	}
	
	private Date date(String timestr) throws Exception {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(timestr);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertEquals("allDeals",disp.getDealsTableName());
	}
	
	@Test
	public void testAttachWriter() {
		DealWriter writer1 = control.createMock(DealWriter.class);
		DealWriter writer2 = control.createMock(DealWriter.class);
		disp.attachWriter("SBER", writer1);
		disp.attachWriter("RIZ1", writer2);
		assertSame(writer1, disp.map.get("SBER"));
		assertSame(writer2, disp.map.get("RIZ1"));
	}
	
	@Test
	public void testDetachWriter() {
		DealWriter writer1 = control.createMock(DealWriter.class);
		DealWriter writer2 = control.createMock(DealWriter.class);
		disp.attachWriter("SBER", writer1);
		disp.attachWriter("GAZP", writer2);
		disp.detachWriter("GAZP");
		disp.detachWriter("ZUKA");
		
		assertSame(writer1, disp.map.get("SBER"));
		assertNull(disp.map.get("GAZP"));
	}
	
	@Test
	public void testDispatch_DoNothingIfNoWriter() throws Exception {
		DealWriter writer1 = control.createMock(DealWriter.class);
		disp.attachWriter("foo", writer1);
		assertNull(disp.actuality);
		control.replay();
		
		disp.dispatch(12345, date("2001-01-15 08:00:32"), "bar", 123.45d, 123);
		
		control.verify();
		
		assertEquals(date("2001-01-15 08:00:32"), disp.actuality.time);
		assertEquals(12345, disp.actuality.number);
	}
	
	@Test
	public void testDispatch_IgnoresPastDealsByTime() throws Exception {
		DealWriter writer1 = control.createMock(DealWriter.class);
		disp.attachWriter("foo", writer1);
		ap = new ExportQuikActualityPoint(date("2011-11-16 21:33:04"), 111);
		disp.actuality = ap;
	
		control.replay();
		
		disp.dispatch(12345, date("2011-11-16 08:00:32"), "foo", 123.45d, 123);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_IgnoresPastDealsByNumber() throws Exception {
		DealWriter writer1 = control.createMock(DealWriter.class);
		disp.attachWriter("foo", writer1);
		ap = new ExportQuikActualityPoint(date("2011-11-16 21:33:04"), 12345);
		disp.actuality = ap;
	
		control.replay();
		
		disp.dispatch(12300, date("2011-11-16 22:00:32"), "foo", 123.45d, 100);
		
		control.verify();
	}
	
	@Test
	public void testDispatch_Ok() throws Exception {
		DealWriter writer1 = control.createMock(DealWriter.class);
		DealWriter writer2 = control.createMock(DealWriter.class);
		DealImpl deal1 = new DealImpl(date("2011-11-16 21:33:04"), 19.05d, 30);
		DealImpl deal2 = new DealImpl(date("2011-11-16 22:00:05"), 10.19d, 20);
		expect(writer2.addDeal(eq(deal1))).andReturn(false);
		expect(writer1.addDeal(eq(deal2))).andReturn(false);
		disp.attachWriter("foo", writer1);
		disp.attachWriter("bar", writer2);
	
		control.replay();
		
		disp.dispatch(123, date("2011-11-16 21:33:04"), "bar", 19.05d, 30);
		disp.dispatch(124, date("2011-11-16 22:00:05"), "foo", 10.19d, 20);
		
		control.verify();
	}
	
	@Test
	public void testFlushAll() throws Exception {
		DealWriter writer1 = control.createMock(DealWriter.class);
		DealWriter writer2 = control.createMock(DealWriter.class);
		expect(writer1.flush()).andReturn(true);
		expect(writer2.flush()).andReturn(false);
		disp.attachWriter("foo", writer1);
		disp.attachWriter("bar", writer2);
		
		control.replay();
		disp.flushAll();
		control.verify();
	}
	
	@Test
	public void testRegisterHandler() throws Exception {
		RXltDdeDispatcher ddeDisp = control.createMock(RXltDdeDispatcher.class);
		ddeDisp.add("allDeals", disp);
		control.replay();
		
		disp.registerHandler(ddeDisp);
		
		control.verify();
	}
	
	@Test
	public void testUnregisterHandler() throws Exception {
		RXltDdeDispatcher ddeDisp = control.createMock(RXltDdeDispatcher.class);
		ddeDisp.remove("allDeals", disp);
		control.replay();
		
		disp.unregisterHandler(ddeDisp);
		
		control.verify();
	}
	
	@Test
	public void testOnTable_IgnoreTableWithNe6Cols() throws Exception {
		Object cells[] = { "one", "two" };
		control.replay();
		
		disp.onTable(new Table(cells, "bar", "RXCX", 2));
		
		control.verify();
	}
	
	@Test
	public void testOnTable_IgnoreRowException() throws Exception {
		Object cells[] = {
			// number, DD.MM.YYYY, hh:mm:ss, asset, price, qty
			1.0d, "01.01.2000", "00:00:00", "RIH2", 100.00d, 1.0d,
			2.0d, "error-date", "00:00:00", "RIH2", 105.00d, 2.0d,
			3.0d, "31.12.2000", "23:59:59", "RIH2", 120.00d, 5.0d,
			
		};
		DealWriter writer = control.createMock(DealWriter.class);
		expect(writer.addDeal(eq(new DealImpl(date("2000-01-01 00:00:00"),
				100.00d, 1))))
			.andReturn(false);
		expect(writer.addDeal(eq(new DealImpl(date("2000-12-31 23:59:59"),
				120.00d, 5))))
			.andReturn(false);
		disp.attachWriter("RIH2", writer);
		control.replay();
		
		disp.onTable(new Table(cells, "allDeals", "RXCX", 6));
		
		control.verify();
	}


}
