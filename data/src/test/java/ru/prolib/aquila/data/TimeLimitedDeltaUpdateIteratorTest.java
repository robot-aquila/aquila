package ru.prolib.aquila.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

public class TimeLimitedDeltaUpdateIteratorTest {
	private static final int FIELD1 = 1;
	private static final int FIELD2 = 2;
	private static final int FIELD3 = 3;
	private static final List<DeltaUpdate> FIXTURE = new ArrayList<>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FIXTURE.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-17T00:00:00Z")
			.withToken(FIELD1, 100)
			.withToken(FIELD2, "zulu")
			.withToken(FIELD3, 10.25d)
			.buildUpdate());
		FIXTURE.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-10-17T01:00:00Z")
			.withToken(FIELD2, "abba")
			.buildUpdate());
		FIXTURE.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-10-17T02:00:00Z")
			.withToken(FIELD3, 10.40d)
			.buildUpdate());
		FIXTURE.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-10-17T03:00:00Z")
			.withToken(FIELD1, 101)
			.withToken(FIELD2, "kosh")
			.buildUpdate());
		FIXTURE.add(new DeltaUpdateBuilder()
			.withSnapshot(false)
			.withTime("2016-10-17T04:00:00Z")
			.withToken(FIELD3, 10.45d)
			.buildUpdate());
	}
	
	private static CloseableIterator<DeltaUpdate> createReader() {
		return new CloseableIteratorStub<>(FIXTURE);
	}
	
	private static CloseableIterator<DeltaUpdate> createEmptyReader() {
		return new CloseableIteratorStub<>();
	}
	
	private static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private TimeLimitedDeltaUpdateIterator iterator;

	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
		if ( iterator != null ) {
			iterator.close();
		}
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor3_ThrowsIfEndTimeBeforeStartTime() throws Exception {
		try ( TimeLimitedDeltaUpdateIterator x =
				new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-18T00:00:00Z"), T("2016-10-17T23:59:59.999Z")) ) { }
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor3_ThrowsIfEndTimeEqualsStartTime() throws Exception {
		try ( TimeLimitedDeltaUpdateIterator x =
				new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-18T00:00:00Z"), T("2016-10-18T00:00:00Z")) ) { }
	}

	@Test
	public void testIterate_AllUpdatesBeforeStartTime() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-18T00:00:00Z"));
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		iterator.close();
		
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime(T("2016-10-18T00:00:00Z"))
			.withToken(FIELD1, 101)
			.withToken(FIELD2, "kosh")
			.withToken(FIELD3, 10.45d)
			.buildUpdate());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_AllUpdatesAfterStartTime() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-16T00:00:00Z"));
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		iterator.close();

		List<DeltaUpdate> expected = new ArrayList<>(FIXTURE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_UpdatesBeforeAndAfterStartTime() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-17T02:00:00Z"));
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		iterator.close();
		
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-17T02:00:00Z")
			.withToken(FIELD1, 100)
			.withToken(FIELD2, "abba")
			.withToken(FIELD3, 10.40d)
			.buildUpdate());
		expected.addAll(FIXTURE.subList(3, 5));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_EndTimeLimit() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-17T01:00:00Z"), T("2016-10-17T03:00:00.001Z"));
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		iterator.close();
		
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-17T01:00:00Z")
			.withToken(FIELD1, 100)
			.withToken(FIELD2, "abba")
			.withToken(FIELD3, 10.25d)
			.buildUpdate());
		expected.addAll(FIXTURE.subList(2, 4));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_EndTimeLimit_PendingUpdateIsAfterEndTime() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-17T01:00:00Z"), T("2016-10-17T02:30:00.000Z"));
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		iterator.close();
		
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
			.withSnapshot(true)
			.withTime("2016-10-17T01:00:00Z")
			.withToken(FIELD1, 100)
			.withToken(FIELD2, "abba")
			.withToken(FIELD3, 10.25d)
			.buildUpdate());
		expected.add(FIXTURE.get(2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_EmptySource() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createEmptyReader(), T("2016-10-17T02:00:00Z"));
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		iterator.close();
		
		List<DeltaUpdate> expected = new ArrayList<>();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIterate_FirstUpdateIsAfterEndTime() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createReader(), T("2016-10-10T00:00:00Z"), T("2016-10-11T00:00:00Z"));
		List<DeltaUpdate> actual = new ArrayList<>();
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		iterator.close();
		
		List<DeltaUpdate> expected = new ArrayList<>();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IOException.class)
	public void testNext_ThrowsAfterClose() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createEmptyReader(), T("2016-10-17T02:00:00Z"));
		iterator.close();
		
		iterator.next();
	}
	
	@Test (expected=IOException.class)
	public void testItem_ThrowsAfterClose() throws Exception {
		iterator = new TimeLimitedDeltaUpdateIterator(createEmptyReader(), T("2016-10-17T02:00:00Z"));
		iterator.close();
		
		iterator.item();
	}

}
