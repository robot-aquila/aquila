package ru.prolib.aquila.ta.ds;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.util.AlignDate;
import ru.prolib.aquila.util.AlignDateMinute;

public class BarWriterGrouperTest {
	IMocksControl control;
	BarWriter writer;
	AlignDate aligner;
	BarWriterGrouper grouper;
	Calendar c = Calendar.getInstance();

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		writer = control.createMock(BarWriter.class);
		aligner = new AlignDateMinute(5);
		grouper = new BarWriterGrouper(aligner, writer);
		c.set(2012, 1, 9, 11, 17, 11);
		c.set(Calendar.MILLISECOND, 0);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(aligner, grouper.getDateAligner());
		assertSame(writer, grouper.getTargetBarWriter());
	}
	
	@Test
	public void testAddBar() throws Exception {
		c.set(Calendar.MINUTE, 17);
		Candle bar1 = new Candle(c.getTime(), 125d, 130d,  99d, 110d, 5L);
		c.set(Calendar.MINUTE, 19);
		Candle bar2 = new Candle(c.getTime(), 110d, 115d, 105d, 105d, 1L);
		c.set(Calendar.MINUTE, 21);
		Candle bar3 = new Candle(c.getTime(), 105d, 112d, 103d, 108d, 4L);
		
		c.set(Calendar.MINUTE, 15);
		c.set(Calendar.SECOND, 0);
		Candle expected = new Candle(c.getTime(), 125d, 130d,  99d, 105d, 6L);
		expect(writer.addBar(eq(expected))).andReturn(true);
		control.replay();
		
		assertFalse(grouper.addBar(bar1));
		assertFalse(grouper.addBar(bar2));
		assertTrue(grouper.addBar(bar3));
		
		control.verify();
	}
	
	@Test
	public void testFlush_NoBar() throws Exception {
		control.replay();
		
		assertFalse(grouper.flush());
		
		control.verify();
	}
	
	@Test
	public void testFlush_HasBar() throws Exception {
		c.set(Calendar.MINUTE, 17);
		Candle bar1 = new Candle(c.getTime(), 125d, 130d,  99d, 110d, 5L);
		
		c.set(Calendar.MINUTE, 15);
		c.set(Calendar.SECOND, 0);
		Candle expected = new Candle(c.getTime(), 125d, 130d,  99d, 110d, 5L);
		expect(writer.addBar(eq(expected))).andReturn(false);
		expect(writer.flush()).andReturn(false);
		control.replay();
		
		assertFalse(grouper.addBar(bar1));
		assertTrue(grouper.flush());
		
		control.verify();
	}

}
