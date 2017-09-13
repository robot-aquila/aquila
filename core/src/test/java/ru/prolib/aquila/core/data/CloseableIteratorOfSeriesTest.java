package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CloseableIteratorOfSeriesTest {
	private EditableSeries<Integer> series;
	private CloseableIteratorOfSeries<Integer> iterator;

	@Before
	public void setUp() throws Exception {
		series = new SeriesImpl<>();
		iterator = new CloseableIteratorOfSeries<>(series);
	}

	@Test
	public void testIterator() throws Exception {
		series.add(500);
		series.add(200);
		series.add(240);
		series.add(812);
		
		List<Integer> actual = new ArrayList<>(),
				expected = new ArrayList<>();
		expected.add(500);
		expected.add(200);
		expected.add(240);
		expected.add(812);
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsIfClosed() throws Exception {
		series.add(500);
		series.add(200);
		series.add(240);
		series.add(812);

		iterator.close();
		iterator.next();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsIfClosed() throws Exception {
		series.add(500);
		series.add(200);
		series.add(240);
		series.add(812);

		iterator.next();
		iterator.close();
		iterator.item();
	}

}
