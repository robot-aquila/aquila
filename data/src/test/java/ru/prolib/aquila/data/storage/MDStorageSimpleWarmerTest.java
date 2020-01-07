package ru.prolib.aquila.data.storage;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.easymock.EasyMock.*;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.data.storage.MDStorageSimpleWarmer.ID_CR_KIT;
import ru.prolib.aquila.data.storage.MDStorageSimpleWarmer.WARMUP_ENTRY;


public class MDStorageSimpleWarmerTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	static List<String> random_str_list(int count) {
		List<String> result = new ArrayList<>();
		for ( int i = 0; i < count; i ++ ) {
			result.add(UUID.randomUUID().toString());
		}
		return result;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private Instant time1 = T("2017-02-15T03:19:11Z"), time2 = T("2020-01-07T03:19:11Z"),
			time3 = T("1995-03-19T00:00:00Z"), time4 = T("2020-01-07T22:47:00Z");
	private IMocksControl control;
	private MDStorage<Integer, String> basicMock;
	private CloseableIterator<String> itMock;
	private Lock cr_kit_lock;
	private Map<ID_CR_KIT<Integer>, WARMUP_ENTRY<String>> cr_kit_entries; 
	private MDStorageSimpleWarmer<Integer, String> service1, service2;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		basicMock = control.createMock(MDStorage.class);
		itMock = control.createMock(CloseableIterator.class);
		cr_kit_lock = new ReentrantLock();
		cr_kit_entries = new HashMap<>();
		service1 = new MDStorageSimpleWarmer<>(basicMock);
		service2 = new MDStorageSimpleWarmer<>(basicMock, 50, 10L, cr_kit_lock, cr_kit_entries);
	}
	
	@Test
	public void testCtor_Max() {
		assertEquals( 50, service2.getEntryMaxElements());
		assertEquals(10L, service2.getEntryMaxTTL());
	}
	
	@Test
	public void testCtor3() {
		service1 = new MDStorageSimpleWarmer<>(basicMock, 200, 15L);
		assertEquals(200, service1.getEntryMaxElements());
		assertEquals(15L, service1.getEntryMaxTTL());		
	}

	@Test
	public void testCtor1() {
		assertEquals(2048, service1.getEntryMaxElements());
		assertEquals( 64L, service1.getEntryMaxTTL());		
	}
	
	static class CR_3KIT_Test implements Runnable {
		final MDStorage<Integer, String> service;
		final ID_CR_KIT<Integer> id;
		final CloseableIterator<String> expected;
		final CountDownLatch finished;
		
		CR_3KIT_Test(MDStorage<Integer, String> service,
				ID_CR_KIT<Integer> id,
				CloseableIterator<String> expected,
				CountDownLatch finished)
		{
			this.service = service;
			this.id = id;
			this.expected = expected;
			this.finished = finished;
		}
		
		CR_3KIT_Test(MDStorage<Integer, String> service,
				int key, int count, Instant time,
				List<String> expected,
				CountDownLatch finished)
		{
			this(service, new ID_CR_KIT<>(key, count, time), new CloseableIteratorStub<>(expected), finished);
		}
		
		@Override
		public void run() {
			CloseableIterator<String> actual = null;
			try {
				actual = service.createReader(id.key, id.count, id.to);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			try {
				assertEquals(expected, actual);
			} finally {
				IOUtils.closeQuietly(actual);
			}
			finished.countDown();
		}
		
	}
	
	@Test
	public void testCreateReader_3KIT_ComplexWarmingTest() throws Exception {
		List<String> list1, list2, list3, list4;
		expect(basicMock.createReader(85, 50, time1)).andReturn(new CloseableIteratorStub<>(list1 = random_str_list(50)));
		expect(basicMock.createReader(30, 19, time2)).andReturn(new CloseableIteratorStub<>(list2 = random_str_list(19)));
		expect(basicMock.createReader(15, 20, time3)).andReturn(new CloseableIteratorStub<>(list3 = random_str_list(20)));
		expect(basicMock.createReader(12, 25, time4)).andStubReturn(new CloseableIteratorStub<>(list4 = random_str_list(25)));
		control.replay();
		
		service2.warmingUpReader(85, 50, time1);
		service2.warmingUpReader(85, 50, time1);
		service2.warmingUpReader(30, 19, time2);
		service2.warmingUpReader(15, 20, time3);
		
		CountDownLatch phase1 = new CountDownLatch(6);
		List<Thread> threads = new ArrayList<>();
		for ( int i = 0; i < 2; i ++ )
			threads.add(new Thread(new CR_3KIT_Test(service2, 85, 50, time1, list1, phase1), "T1#" + i));
		for ( int i = 0; i < 2; i ++ )
			threads.add(new Thread(new CR_3KIT_Test(service2, 30, 19, time2, list2, phase1), "T2#" + i));
		for ( int i = 0; i < 2; i ++ )
			threads.add(new Thread(new CR_3KIT_Test(service2, 15, 20, time3, list3, phase1), "T3#" + i));
		Collections.shuffle(threads);
		for ( Thread thread : threads ) thread.start();
		
		assertTrue(phase1.await(1, TimeUnit.SECONDS));
		service2.getCrKITCleanup().get(1, TimeUnit.SECONDS);
		assertTrue(service2.isCrKITEntryExists(85, 50, time1));
		assertTrue(service2.isCrKITEntryExists(30, 19, time2));
		assertTrue(service2.isCrKITEntryExists(15, 20, time3));
		
		// Emulate "time" shift to initiate cleanup of all entries.
		// They are in range of 10 because max TTL is 10 for service.
		// Actual update time for each entry is between 2 and 6
		// because total 6 entries and time was updated at least twice.
		// So to be sure we have to shift "time" for 1 + 6
		for ( int i = 0; i < 16; i ++ ) {
			assertEquals(new CloseableIteratorStub<>(list4), service2.createReader(12, 25, time4));
		}
		
		service2.getCrKITCleanup().get(1, TimeUnit.SECONDS);
		assertFalse(service2.isCrKITEntryExists(85, 50, time1));
		assertFalse(service2.isCrKITEntryExists(30, 19, time2));
		assertFalse(service2.isCrKITEntryExists(15, 20, time3));
		
		control.verify();
	}

	@Test
	public void testCreateReader_3KIT_NotWarmed() throws Exception {
		List<String> list1 = random_str_list(50); CloseableIterator<String> it1 = new CloseableIteratorStub<>(list1); 
		List<String> list2 = random_str_list(19); CloseableIterator<String> it2 = new CloseableIteratorStub<>(list2);
		expect(basicMock.createReader(85, 50, time1)).andReturn(it1);
		expect(basicMock.createReader(85, 50, time1)).andReturn(it2);
		control.replay();
		
		assertEquals(new CloseableIteratorStub<>(list1), service2.createReader(85, 50, time1));
		assertEquals(new CloseableIteratorStub<>(list2), service2.createReader(85, 50, time1));
		
		control.verify();
	}
	
	@Test
	public void testCreateReader_3KIT_WarmingEndedExceptionally() throws Exception {
		List<String> list1 = random_str_list(50);
		expect(basicMock.createReader(85, 50, time1)).andThrow(new DataStorageException("Test error"));
		expect(basicMock.createReader(85, 50, time1))
			.andReturn(new CloseableIteratorStub<>(list1))
			.andReturn(new CloseableIteratorStub<>(list1));
		control.replay();
		service2.warmingUpReader(85, 50, time1);
		
		try {
			service2.getCrKITEntry(85, 50, time1).result.get(1, TimeUnit.SECONDS);
			fail("Expected exception: " + ExecutionException.class.getSimpleName());
		} catch ( ExecutionException e ) { }
		
		assertEquals(new CloseableIteratorStub<>(list1), service2.createReader(85, 50, time1));
		assertFalse(service2.isCrKITEntryExists(85, 50, time1)); // Just single error message possible
		assertEquals(new CloseableIteratorStub<>(list1), service2.createReader(85, 50, time1));
		
		control.verify();
	}

	@Test
	public void testCreateReader_3KIT_CountMismatch() throws Exception {
		List<String> list1 = random_str_list(45); CloseableIterator<String> it1 = new CloseableIteratorStub<>(list1); 
		List<String> list2 = random_str_list(19); CloseableIterator<String> it2 = new CloseableIteratorStub<>(list2);
		expect(basicMock.createReader(85, 50, time1)).andReturn(it1).andReturn(it2);
		control.replay();
		service2.warmingUpReader(85, 50, time1);
		service2.getCrKITEntry(85, 50, time1).result.get(1, TimeUnit.SECONDS); // Wait until complete
		
		assertEquals(new CloseableIteratorStub<>(list2), service2.createReader(85, 50, time1));

		control.verify();
		assertFalse(service2.isCrKITEntryExists(85, 50, time1)); // Should removed
	}

	@Test
	public void testGetKeys() throws Exception {
		@SuppressWarnings("unchecked")
		Set<Integer> keysMock = control.createMock(Set.class);
		expect(basicMock.getKeys()).andReturn(keysMock);
		control.replay();
		
		assertSame(keysMock, service1.getKeys());
		
		control.verify();
	}
	
	@Test
	public void testCreateReader_1K() throws Exception {
		expect(basicMock.createReader(26)).andReturn(itMock);
		control.replay();
		
		assertSame(itMock, service1.createReader(26));
		
		control.verify();
	}
	
	@Test
	public void testCreateReaderFrom_2KT() throws Exception {
		expect(basicMock.createReaderFrom(64, time1)).andReturn(itMock);
		control.replay();
		
		assertSame(itMock, service1.createReaderFrom(64, time1));
		
		control.verify();
	}
	
	@Test
	public void testCreateReader_3KTI() throws Exception {
		expect(basicMock.createReader(55, time1, 100)).andReturn(itMock);
		control.replay();
		
		assertSame(itMock, service1.createReader(55, time1, 100));
		
		control.verify();
	}
	
	@Test
	public void testCreateReader_3KTT() throws Exception {
		expect(basicMock.createReader(24, time1, time2)).andReturn(itMock);
		control.replay();
		
		assertSame(itMock, service1.createReader(24, time1, time2));
		
		control.verify();
	}
	
	@Test
	public void testCreateReaderTo_2KT() throws Exception {
		expect(basicMock.createReaderTo(85, time1)).andReturn(itMock);
		control.replay();
		
		assertSame(itMock, service1.createReaderTo(85, time1));
		
		control.verify();
	}

}
