package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.Vector;
import ru.prolib.aquila.core.BusinessEntities.Tick;

/**
 * Тест выполняет проверку содержимого потока на соответствие файлу
 * fixture/GAZP_ticks.txt
 */
public class TickReader_FunctionalTest {
	private final Vector<Tick> expected = new Vector<Tick>();
	
	public TickReader_FunctionalTest() {
		expected.add(Tick.of(Instant.parse("2014-06-18T09:59:59Z"), 144.79d, 250));
		expected.add(Tick.of(Instant.parse("2014-06-18T09:59:59Z"), 144.79d,   5));
		expected.add(Tick.of(Instant.parse("2014-06-18T09:59:59Z"), 144.79d,  10));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:00Z"), 144.98d,   1));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:00Z"), 144.80d, 500));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:01Z"), 144.70d, 300));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:01Z"), 144.70d,   1));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:02Z"), 144.64d,  80));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:02Z"), 144.70d,   3));
	}
	
	/**
	 * Сравни содержимое потока с ожидаемым набором тиков. 
	 * <p>
	 * @param reader
	 * @throws Exception
	 */
	public void testStreamContent(final Aqiterator<Tick> reader)
			throws Exception
	{
		Vector<Tick> actual = new Vector<Tick>();
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		reader.close();
		
		assertEquals(expected, actual);
	}

}
