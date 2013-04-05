package ru.prolib.aquila.ta;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.BarPattern;
import ru.prolib.aquila.ta.BarPatternBuilder;
import ru.prolib.aquila.ta.BarPatternBuilderImpl;
import ru.prolib.aquila.ta.BarPatternException;
import ru.prolib.aquila.ta.BarSequencePattern;
import ru.prolib.aquila.ta.BarSequencePatternBuilderImpl;
import ru.prolib.aquila.ta.ds.MarketData;

public class BarSequencePatternBuilderImplTest {
	IMocksControl control;
	MarketData data;
	BarPatternBuilder bb;
	BarSequencePatternBuilderImpl builder;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		data = control.createMock(MarketData.class);
		bb = control.createMock(BarPatternBuilder.class);
		builder = new BarSequencePatternBuilderImpl(bb);
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertSame(bb, builder.getBarPatternBuilder());
	}
	
	@Test
	public void testConstruct0() throws Exception {
		builder = new BarSequencePatternBuilderImpl();
		assertNotNull(builder.getBarPatternBuilder());
		assertTrue(builder.getBarPatternBuilder()
				instanceof BarPatternBuilderImpl);
	}
	
	@Test
	public void testBuildBarSequencePattern_Ok() throws Exception {
		Candle b1 = new Candle(new Date(), 120d, 160d, 100d, 130d, 1);
		Candle b2 = new Candle(new Date(), 2d, 2d, 2d, 2d, 2);
		Candle b3 = new Candle(new Date(), 3d, 3d, 3d, 3d, 3);
		BarPattern p1 = new BarPattern(1, 1, 1, 1);
		BarPattern p2 = new BarPattern(2, 2, 2, 2);
		BarPattern p3 = new BarPattern(3, 3, 3, 3);
		expect(data.getBar(4)).andReturn(b1);
		expect(bb.buildBarPattern(eq(100d), eq(20d), same(b1))).andReturn(p1);
		expect(data.getBar(5)).andReturn(b2);
		expect(bb.buildBarPattern(eq(100d), eq(20d), same(b2))).andReturn(p2);
		expect(data.getBar(6)).andReturn(b3);
		expect(bb.buildBarPattern(eq(100d), eq(20d), same(b3))).andReturn(p3);
		control.replay();
		
		BarSequencePattern p = builder.buildBarSequencePattern(data, 4, 3);
		
		control.verify();
		assertNotNull(p);
		assertEquals(3, p.getLength());
		List<BarPattern> bars = p.getBars();
		assertEquals(3, bars.size());
		assertSame(p1, bars.get(0));
		assertSame(p2, bars.get(1));
		assertSame(p3, bars.get(2));
	}
	
	@Test (expected=BarPatternException.class)
	public void testBuildBarSequencePattern_ThrowsNegativeLength()
		throws Exception
	{
		control.replay();
		
		builder.buildBarSequencePattern(data, 10, -1);
	}
	
	@Test (expected=BarPatternException.class)
	public void testBuildBarSequencePattern_ThrowsZeroLength()
		throws Exception
	{
		control.replay();
		
		builder.buildBarSequencePattern(data, 10, 0);
	}

}
