package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.util.Vector;
import org.joda.time.DateTime;

/**
 * Тест выполняет проверку содержимого потока на соответствие файлу
 * fixture/GAZP_ticks.txt
 */
public class TickReader_FunctionalTest {
	private final Vector<Tick> expected = new Vector<Tick>();
	
	public TickReader_FunctionalTest() {
		expected.add(new Tick(new DateTime(2014,6,18,9,59,59,0),144.79d, 250d));
		expected.add(new Tick(new DateTime(2014,6,18,9,59,59,0),144.79d,   5d));
		expected.add(new Tick(new DateTime(2014,6,18,9,59,59,0),144.79d,  10d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,0,0),144.98d, 1.54d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,0,0),144.80d, 500d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,1,0),144.70d, 300d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,1,0),144.70d, 1.4d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,2,0),144.64d, 80d));
		expected.add(new Tick(new DateTime(2014,6,18,10,0,2,0),144.70d, 3d));
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
