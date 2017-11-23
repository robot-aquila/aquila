package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.TimeConverter;

public class SimpleCsvL1UpdateReaderFactoryTest {
	private IMocksControl control;
	private TimeConverter timeConverterMock;
	private SimpleCsvL1UpdateReaderFactory factory;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeConverterMock = control.createMock(TimeConverter.class);
	}
	
	private void assertIteratorContainsTheRightData(CloseableIterator<L1Update> it)
		throws Exception
	{
		List<L1Update> expected = new ArrayList<>();
		expected.add(new L1UpdateBuilder(new Symbol("GILD"))
			.withBid()
			.withTime("2016-03-07T17:25:01.122Z")
			.withPrice(CDecimalBD.of("89.43"))
			.withSize(CDecimalBD.of(100L))
			.buildL1Update());
		expected.add(new L1UpdateBuilder(new Symbol("MSFT"))
			.withAsk()
			.withTime("2016-03-07T17:25:01.122Z")
			.withPrice(CDecimalBD.of("89.44"))
			.withSize(CDecimalBD.of(400L))
			.buildL1Update());
		expected.add(new L1UpdateBuilder(new Symbol("TWTR"))
			.withTrade()
			.withTime("2016-03-07T17:25:01.103Z")
			.withPrice(CDecimalBD.of("19.56"))
			.withSize(CDecimalBD.of(100L))
			.buildL1Update());
		
		List<L1Update> actual = new ArrayList<>();
		while ( it.next() ) {
			actual.add(it.item());
		}
		assertEquals(expected, actual);
	}

	@Test
	public void testCtor2() {
		File file = new File("foo/bar");
		
		factory = new SimpleCsvL1UpdateReaderFactory(file, timeConverterMock);
		
		assertEquals(file, factory.getFile());
		assertSame(timeConverterMock, factory.getTimeConverter());
	}
	
	@Test
	public void testCtor1() {
		File file = new File("foo/bar");
		
		factory = new SimpleCsvL1UpdateReaderFactory(file);
		
		assertEquals(file, factory.getFile());
		assertNull(factory.getTimeConverter());
	}
	
	@Test
	public void testCtor0() {
		factory = new SimpleCsvL1UpdateReaderFactory();
		
		assertNull(factory.getFile());
		assertNull(factory.getTimeConverter());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testCreateReader_ThrowsIfFileWasNotDefined() throws Exception {
		factory = new SimpleCsvL1UpdateReaderFactory();
		
		factory.createReader();
	}

	@Test
	public void testCreateReader_WithoutTimeConverter() throws Exception {
		factory = new SimpleCsvL1UpdateReaderFactory(new File("fixture/SimpleCsvL1.txt"));
		control.replay();
		
		CloseableIterator<L1Update> it = factory.createReader();
		
		control.verify();
		assertIteratorContainsTheRightData(it);
	}

	@Test
	public void testCreateReader_WithTimeConverter() throws Exception {
		factory = new SimpleCsvL1UpdateReaderFactory(new File("fixture/SimpleCsvL1.txt"), timeConverterMock);
		timeConverterMock.reset();
		control.replay();
		CloseableIterator<L1Update> it = factory.createReader();
		
		control.verify();
		assertIteratorContainsTheRightData(it);
	}

}
