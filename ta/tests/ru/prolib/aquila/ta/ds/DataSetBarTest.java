package ru.prolib.aquila.ta.ds;


import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.data.Candle;

public class DataSetBarTest {
	DataSetBar set;
	Candle bar;
	Date date;

	@Before
	public void setUp() throws Exception {
		date = new Date();
		bar = new Candle(date, 100d, 200d, 90d, 105d, 20L);
		set = new DataSetBar();
		set.setBar(bar);
	}
	
	@Test
	public void testGetBar() throws Exception {
		assertSame(bar, set.getBar());
		set.setBar(null);
		assertNull(set.getBar());
	}
	
	@Test (expected=DataSetValueNotExistsException.class)
	public void testGetDate_ThrowsNotExists() throws Exception {
		set.getDate("zulu");
	}
	
	@Test
	public void testGetDate_Ok() throws Exception {
		assertEquals(date, set.getDate(MarketData.TIME));
	}
	
	@Test (expected=DataSetBarNotSpecifiedException.class)
	public void testGetDate_ThrowsBarNotSpecified() throws Exception {
		set.setBar(null);
		set.getDate(MarketData.TIME);
	}
	
	@Test (expected=DataSetValueNotExistsException.class)
	public void testGetDouble_ThrowsNotExists() throws Exception {
		set.getDouble("alpha");
	}
	
	@Test
	public void testGetDouble_Ok() throws Exception {
		assertEquals(100d, set.getDouble(MarketData.OPEN), 0.001d);
		assertEquals(200d, set.getDouble(MarketData.HIGH), 0.001d);
		assertEquals( 90d, set.getDouble(MarketData.LOW), 0.001d);
		assertEquals(105d, set.getDouble(MarketData.CLOSE), 0.001d);
		assertEquals( 20d, set.getDouble(MarketData.VOL), 0.001d);
	}
	
	@Test (expected=DataSetBarNotSpecifiedException.class)
	public void testGetDouble_ThrowsBarNotSpecified() throws Exception {
		set.setBar(null);
		set.getDouble(MarketData.CLOSE);
	}
	
	@Test (expected=DataSetValueNotExistsException.class)
	public void testGetString_AlwaysThrows() throws Exception {
		set.getString("delta");
	}
	
	@Test (expected=DataSetValueNotExistsException.class)
	public void testGetLong_AlwaysThrows() throws Exception {
		set.getLong("charlie");
	}

}
