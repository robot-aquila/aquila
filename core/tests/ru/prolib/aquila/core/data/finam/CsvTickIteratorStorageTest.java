package ru.prolib.aquila.core.data.finam;

import static org.junit.Assert.*;
import java.io.File;
import java.util.List;
import java.util.Vector;
import org.joda.time.DateTime;
import org.joda.time.format.*;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

public class CsvTickIteratorStorageTest {
	private static final DateTimeFormatter df;
	private static final SecurityDescriptor descr;
	
	static {
		df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
		descr = new SecurityDescriptor("GAZP", "EQBR", ISO4217.RUR, SecurityType.STK);
	}

	private String dataId = "GAZP-EQBR-RUR-STK";
	private CsvTickIteratorStorage storage;
	private Aqiterator<Tick> ticks;

	@Before
	public void setUp() throws Exception {
		storage = new CsvTickIteratorStorage(new File("fixture"));
		ticks = null;
	}
	
	/**
	 * Создать тик.
	 * <p>
	 * @param time время в формате {@link #df}.
	 * @param price цена
	 * @param value объем
	 * @return тик
	 */
	private Tick newTick(String time, double price, double value) {
		return new Tick(df.parseDateTime(time), price, value);
	}
	
	private List<Tick> getExpectedAll() {
		List<Tick> expected = new Vector<Tick>();
		expected.add(newTick("2014-02-01 10:00:00.000", 143.00,   1));
		expected.add(newTick("2014-02-18 15:00:00.000", 145.00,   1));
		expected.add(newTick("2014-10-01 10:00:00.000", 143.00,   1));
		expected.add(newTick("2014-10-05 10:00:00.000", 148.00,   1));
		expected.add(newTick("2014-10-13 10:00:00.000", 143.00,   1));
		expected.add(newTick("2014-10-14 10:00:01.000", 145.70,   1));
		expected.add(newTick("2014-10-14 10:00:02.000", 145.64,   8));
		expected.add(newTick("2014-10-15 10:00:00.000", 144.00,   1));
		expected.add(newTick("2014-10-19 09:59:59.000", 144.79, 250));
		expected.add(newTick("2014-12-04 10:00:01.000", 145.70,   1));
		expected.add(newTick("2014-12-04 10:00:02.000", 145.64,   8));
		return expected;
	}
	
	@Test
	public void testGetIterator_Str_IfNoData() throws Exception {
		String dataId = "SBER-EQBR-RUR-STK";
		ticks = storage.getIterator(dataId, new DateTime(1998, 1, 1, 0, 0, 0, 0));
		assertFalse(ticks.next());
	}
	
	@Test
	public void testGetIterator_Dsc_IfNoData() throws Exception {
		storage = new CsvTickIteratorStorage(new File("fixture"));
		SecurityDescriptor descr = new SecurityDescriptor("SBER", "EQBR",
				ISO4217.RUR, SecurityType.STK);
		ticks = storage.getIterator(descr, new DateTime(1998, 1, 1, 0, 0, 0, 0));
		assertFalse(ticks.next());
	}

	private void testGetIterator_IfTimeBeforeData(Aqiterator<Tick> ticks)
			throws Exception
	{
		List<Tick> actual = new Vector<Tick>(),
				expected = getExpectedAll();
		while ( ticks.next() ) {
			actual.add(ticks.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetIterator_Str_IfTimeBeforeData() throws Exception {
		testGetIterator_IfTimeBeforeData(storage.getIterator(dataId,
				new DateTime( 538, 1, 1, 0, 0, 0, 0)));
	}
	
	@Test
	public void testGetIterator_Dsc_IfTimeBeforeData() throws Exception {
		testGetIterator_IfTimeBeforeData(storage.getIterator(descr,
				new DateTime( 538, 1, 1, 0, 0, 0, 0)));
	}

	@Test
	public void testGetIterator_Str_IfTimeAfterData() throws Exception {
		ticks = storage.getIterator(dataId, new DateTime(2100, 1, 1, 0, 0, 0, 0));
		assertFalse(ticks.next());
	}
	
	@Test
	public void testGetIterator_Dsc_IfTimeAfterData() throws Exception {
		ticks = storage.getIterator(descr, new DateTime(2100, 1, 1, 0, 0, 0, 0));
		assertFalse(ticks.next());
	}
	
	@Test
	public void testGetIterator_Str_TimeAfterEndOfTradingSession_HasNoMoreData()
			throws Exception
	{
		ticks = storage.getIterator(dataId, new DateTime(2014, 12, 4, 23, 55, 0, 0));
		assertFalse(ticks.next());
	}
	
	@Test
	public void testGetIterator_Dsc_TimeAfterEndOfTradingSession_HasNoMoreData()
			throws Exception
	{
		ticks = storage.getIterator(descr, new DateTime(2014, 12, 4, 23, 55, 0, 0));
		assertFalse(ticks.next());
	}
	
	private void testGetIterator_TimeAfterEndOfTradingSession_HasMoreData
			(Aqiterator<Tick> ticks) throws Exception
	{
		List<Tick> actual = new Vector<Tick>(),
				expected = new Vector<Tick>();
		expected.add(newTick("2014-10-15 10:00:00.000", 144.00,   1));
		expected.add(newTick("2014-10-19 09:59:59.000", 144.79, 250));
		expected.add(newTick("2014-12-04 10:00:01.000", 145.70,   1));
		expected.add(newTick("2014-12-04 10:00:02.000", 145.64,   8));
		
		while ( ticks.next() ) {
			actual.add(ticks.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetIterator_Str_TimeAfterEndOfTradingSession_HasMoreData()
			throws Exception
	{
		testGetIterator_TimeAfterEndOfTradingSession_HasMoreData(
				storage.getIterator(dataId,
						new DateTime(2014, 10, 14, 23, 55, 0, 0)));
	}
	
	@Test
	public void testGetIterator_Dsc_TimeAfterEndOfTradingSession_HasMoreData()
			throws Exception
	{
		testGetIterator_TimeAfterEndOfTradingSession_HasMoreData(
			storage.getIterator(descr,
				new DateTime(2014, 10, 14, 23, 55, 0, 0)));
	}
	
	private void testGetIterator_ThroughWeekend(Aqiterator<Tick> ticks)
			throws Exception
	{
		List<Tick> actual = new Vector<Tick>(),
				expected = getExpectedAll();
		while ( ticks.next() ) {
			actual.add(ticks.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetIterator_Str_ThroughWeekend()
			throws Exception
	{
		testGetIterator_ThroughWeekend(storage.getIterator(dataId,
				new DateTime( 538, 1, 1, 0, 0, 0, 0)));
	}
	
	@Test
	public void testGetIterator_Dsc_ThroughWeekend()
			throws Exception
	{
		testGetIterator_ThroughWeekend(storage.getIterator(descr,
				new DateTime( 538, 1, 1, 0, 0, 0, 0)));
	}

}
