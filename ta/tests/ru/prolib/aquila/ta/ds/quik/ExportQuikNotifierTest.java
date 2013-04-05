package ru.prolib.aquila.ta.ds.quik;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ipc.*;
import ru.prolib.aquila.ta.DealImpl;
import ru.prolib.aquila.ta.ds.DealWriter;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ExportQuikNotifierTest {
	private static SimpleDateFormat df;
	private IEvent event;
	private DealWriter writer;
	private IMocksControl control;
	private ExportQuikNotifier obj;
	private DealImpl deal;
	private Date startSignalAfter;
	
	@BeforeClass
	static public void setUpBeforeClass() {
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		event = control.createMock(IEvent.class);
		writer = control.createMock(DealWriter.class);
		startSignalAfter = df.parse("2011-01-01 00:00:00");
		obj = new ExportQuikNotifier(writer, event, startSignalAfter);
		deal = new DealImpl(new Date(), 10.25d, 15);
	}
	
	@Test
	public void testAccessors() {
		assertSame(event, obj.getEvent());
		assertSame(writer, obj.getQuoteWriter());
		assertSame(startSignalAfter, obj.getStartSignalAfter());
	}
	
	@Test
	public void testAccessorWithoutTime() {
		obj = new ExportQuikNotifier(writer, event);
		assertSame(event, obj.getEvent());
		assertSame(writer, obj.getQuoteWriter());
		assertEquals(new Date(), obj.getStartSignalAfter());
	}
	
	@Test
	public void testAddDeal_PulseIfBarAddedAndBarAfterStartTime()
		throws Exception
	{
		deal = new DealImpl(df.parse("2011-01-02 00:00:00"), 10.25d, 15);
		expect(writer.addDeal(same(deal))).andReturn(true);
		event.pulse();
		control.replay();
		
		assertTrue(obj.addDeal(deal));
		
		control.verify();
	}
	
	@Test
	public void testAddDeal_NotPulseIfBarAddedButBarBeforeStartTime()
		throws Exception
	{
		// deal with same time
		deal = new DealImpl(df.parse("2011-01-01 00:00:00"), 10.25d, 15);
		expect(writer.addDeal(same(deal))).andReturn(true);
		control.replay();
		
		assertTrue(obj.addDeal(deal));
		
		control.verify();
	}
	
	@Test
	public void testAddDeal_NotPulseIfBarNotAdded() throws Exception {
		expect(writer.addDeal(same(deal))).andReturn(false);
		control.replay();
		
		assertFalse(obj.addDeal(deal));
		
		control.verify();
	}
	
	@Test
	public void testFlush_NotPulseIfBarAdded() throws Exception {
		expect(writer.flush()).andReturn(true);
		control.replay();
		
		assertTrue(obj.flush());
		
		control.verify();
	}
	
	@Test
	public void testFlush_NotPulseIfBarNotAdded() throws Exception {
		expect(writer.flush()).andReturn(false);
		control.replay();
		
		assertFalse(obj.flush());
		
		control.verify();
	}

}
