package ru.prolib.aquila.ta.ds;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.data.Candle;

public class BarWriterDataSetBarTest {
	DataSetBar dataset;
	Candle bar1,bar2;
	BarWriterDataSetBar writer;

	@Before
	public void setUp() throws Exception {
		bar1 = new Candle(new Date(), 100, 200, 90, 80, 10L);
		bar2 = new Candle(new Date(), 150, 180, 10, 90, 50L);
		dataset = new DataSetBar();
		writer = new BarWriterDataSetBar(dataset);
	}
	
	@Test
	public void testAddBar() throws Exception {
		assertTrue(writer.addBar(bar1));
		assertEquals(bar1, dataset.getBar());
		assertTrue(writer.addBar(bar2));
		assertEquals(bar2, dataset.getBar());
	}
	
	@Test
	public void testFlush() throws Exception {
		assertFalse(writer.flush());
	}

}
