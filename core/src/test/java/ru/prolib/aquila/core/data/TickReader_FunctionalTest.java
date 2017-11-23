package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.Vector;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Tick;

/**
 * Тест выполняет проверку содержимого потока на соответствие файлу
 * fixture/GAZP_ticks.txt
 */
public class TickReader_FunctionalTest {
	private final Vector<Tick> expected = new Vector<Tick>();
	
	public TickReader_FunctionalTest() {
		expected.add(Tick.of(Instant.parse("2014-06-18T09:59:59Z"), CDecimalBD.of("144.79"), CDecimalBD.of(250L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T09:59:59Z"), CDecimalBD.of("144.79"),   CDecimalBD.of(5L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T09:59:59Z"), CDecimalBD.of("144.79"),  CDecimalBD.of(10L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:00Z"), CDecimalBD.of("144.98"),   CDecimalBD.of(1L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:00Z"), CDecimalBD.of("144.80"), CDecimalBD.of(500L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:01Z"), CDecimalBD.of("144.70"), CDecimalBD.of(300L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:01Z"), CDecimalBD.of("144.70"),   CDecimalBD.of(1L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:02Z"), CDecimalBD.of("144.64"),  CDecimalBD.of(80L)));
		expected.add(Tick.of(Instant.parse("2014-06-18T10:00:02Z"), CDecimalBD.of("144.70"),   CDecimalBD.of(3L)));
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
