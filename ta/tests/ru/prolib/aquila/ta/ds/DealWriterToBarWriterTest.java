package ru.prolib.aquila.ta.ds;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.Deal;
import ru.prolib.aquila.ta.DealImpl;

public class DealWriterToBarWriterTest {
	IMocksControl control;
	BarWriter barWriter;
	DealWriterToBarWriter dealWriter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		barWriter = control.createMock(BarWriter.class);
		dealWriter = new DealWriterToBarWriter(barWriter);
	}
	
	@Test
	public void testConstruct() {
		assertSame(barWriter, dealWriter.getBarWriter());
	}
	
	@Test
	public void testAddDeal_Ok() throws Exception {
		Date date = new Date();
		Deal deal = new DealImpl(date, 35d, 10L);
		Candle expected = new Candle(date, 35d, 35d, 35d, 35d, 10L);
		expect(barWriter.addBar(eq(expected))).andReturn(true);
		control.replay();
		
		assertTrue(dealWriter.addDeal(deal));
		
		control.verify();
	}
	
	@Test (expected=DealWriterException.class)
	public void testAddDeal_Throws() throws Exception {
		expect(barWriter.addBar(isA(Candle.class)))
			.andThrow(new BarWriterException("foobar"));
		control.replay();
		
		dealWriter.addDeal(new DealImpl(new Date(), 1d, 1L));
	}
	
	@Test
	public void testFlush_Ok() throws Exception {
		expect(barWriter.flush()).andReturn(true);
		expect(barWriter.flush()).andReturn(false);
		control.replay();
		
		assertTrue(dealWriter.flush());
		assertFalse(dealWriter.flush());
		
		control.verify();
	}
	
	@Test (expected=DealWriterException.class)
	public void testFlush_Throws() throws Exception {
		expect(barWriter.flush()).andThrow(new BarWriterException("error"));
		control.replay();
		
		dealWriter.flush();
	}

}
