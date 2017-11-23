package ru.prolib.aquila.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class TimeLimitedL1UpdateIteratorTest {
	private static final Symbol symbol = new Symbol("MSFT");
	private static List<L1Update> FIXTURE =  new ArrayList<>();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FIXTURE.add(new L1UpdateBuilder(symbol)
			.withTime("2016-01-01T00:00:00Z")
			.withTrade()
			.withPrice("12.34")
			.withSize(100L)
			.buildL1Update());
		FIXTURE.add(new L1UpdateBuilder(symbol)
			.withTime("2016-03-01T00:00:00Z")
			.withAsk()
			.withPrice("12.50")
			.withSize(200L)
			.buildL1Update());
		FIXTURE.add(new L1UpdateBuilder(symbol)
			.withTime("2016-03-01T00:00:10Z")
			.withBid()
			.withPrice("12.01")
			.withSize(150L)
			.buildL1Update());
		FIXTURE.add(new L1UpdateBuilder(symbol)
			.withTime("2016-03-05T00:00:00Z")
			.withTrade()
			.withPrice("12.50")
			.withSize(300L)
			.buildL1Update());
		FIXTURE.add(new L1UpdateBuilder(symbol)
			.withTime("2016-05-01T00:00:00Z")
			.withTrade()
			.withPrice("13.01")
			.withSize(350L)
			.buildL1Update());
	}
	
	private static CloseableIterator<L1Update> createReader() {
		return new CloseableIteratorStub<>(FIXTURE);
	}
	
	private static CloseableIterator<L1Update> createEmptyReader() {
		return new CloseableIteratorStub<>();
	}
	
	private static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TimeLimitedL1UpdateIterator iterator;
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void testIterate_AllUpdatesBeforeStartTime() throws Exception {
		iterator = new TimeLimitedL1UpdateIterator(createReader(), T("2017-01-01T00:00:00Z"));
		List<L1Update> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		List<L1Update> expected = new ArrayList<>();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_AllUpdatesAfterStartTime() throws Exception {
		iterator = new TimeLimitedL1UpdateIterator(createReader(), T("2016-01-01T00:00:00Z"));
		List<L1Update> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		assertEquals(FIXTURE, actual);
	}
	
	@Test
	public void testIterate_UpdatesBeforeAndAfterStartTime() throws Exception {
		iterator = new TimeLimitedL1UpdateIterator(createReader(), T("2016-03-01T00:00:05Z"));
		List<L1Update> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		assertEquals(FIXTURE.subList(2, 5), actual);
	}
	
	@Test
	public void testIterate_EmptySource() throws Exception {
		iterator = new TimeLimitedL1UpdateIterator(createEmptyReader(), T("2016-03-01T00:00:00Z"));
		List<L1Update> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		
		assertEquals(new ArrayList<>(), actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsAfterClose() throws Exception {
		iterator = new TimeLimitedL1UpdateIterator(createEmptyReader(), T("2016-03-01T00:00:00Z"));
		iterator.close();
		
		iterator.next();
	}

	@Test (expected=IOException.class)
	public void testItem_ThrowsAfterClose() throws Exception {
		iterator = new TimeLimitedL1UpdateIterator(createEmptyReader(), T("2016-03-01T00:00:00Z"));
		iterator.close();
		
		iterator.item();		
	}		

}
