package ru.prolib.aquila.data;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;

public class CyclicReaderTest {
	
	static class FactoryStub implements ReaderFactory<String> {

		@Override
		public CloseableIterator<String> createReader() {
			List<String> list = new ArrayList<>();
			list.add("You can find");
			list.add("the custom");
			list.add("tree model");
			return new CloseableIteratorStub<>(list);
		}
		
	}
	
	static class FactoryEmptyReader implements ReaderFactory<String> {

		@Override
		public CloseableIterator<String> createReader() {
			return new CloseableIteratorStub<>();
		}
		
	}

	private ReaderFactory<String> factory;
	private CyclicReader<String> reader;
	
	@Before
	public void setUp() throws Exception {
		factory = new FactoryStub();
		reader = new CyclicReader<String>(factory, 5);
	}
	
	@After
	public void tearDown() throws Exception {
		reader.close();
	}
	
	@Test
	public void testCtor2() {
		assertEquals(factory, reader.getReaderFactory());
		assertEquals(5, reader.getRepeat());
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor2_ThrowsWhenRepeatLessThanZero() throws Exception {
		try ( CyclicReader<String> x = new CyclicReader<>(factory, -1) ) { }
	}
	
	@Test
	public void testIterate_SkipWhenInfinitelyAndEmpty() throws Exception {
		factory = new FactoryEmptyReader();
		reader = new CyclicReader<String>(factory, CyclicReader.REPEAT_INFINITELY);
		
		assertFalse(reader.next());
		// It is safe repeat more than once
		assertFalse(reader.next());
		assertFalse(reader.next());
	}

	@Test
	public void testIterate_SkipWhenLimitedAndEmpty() throws Exception {
		factory = new FactoryEmptyReader();
		reader = new CyclicReader<String>(factory, 5);
		
		assertFalse(reader.next());
		// It is safe repeat more than once
		assertFalse(reader.next());
		assertFalse(reader.next());
	}
	
	@Test
	public void testIterate_Limited() throws Exception {
		List<String> actual = new ArrayList<>();
		while ( reader.next() ) {
			actual.add(reader.item());
		}
		
		List<String> expected = new ArrayList<>();
		for ( int i = 0; i < 5; i ++ ) {
			expected.add("You can find");
			expected.add("the custom");
			expected.add("tree model");
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_Infinitely() throws Exception {
		reader = new CyclicReader<String>(factory, CyclicReader.REPEAT_INFINITELY);
		
		List<String> actual = new ArrayList<>();
		for ( int i = 0; i < 30; i ++ ) {
			assertTrue(reader.next());
			actual.add(reader.item());
		}
		
		List<String> expected = new ArrayList<>();
		for ( int i = 0; i < 10; i ++ ) {
			expected.add("You can find");
			expected.add("the custom");
			expected.add("tree model");
		}
		assertEquals(expected, actual);
	}

}
