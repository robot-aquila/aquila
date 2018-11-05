package ru.prolib.aquila.ui.form;

import static org.junit.Assert.*;
import static ru.prolib.aquila.ui.form.SecurityListTableModel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.utils.Variant;

public class SecurityListTableModelTest {
	private IMocksControl control;
	private Security securityMock1, securityMock2, securityMock3,
		securityMock4, securityMock5, securityMock6;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		securityMock1 = control.createMock(Security.class);
		securityMock2 = control.createMock(Security.class);
		securityMock3 = control.createMock(Security.class);
		securityMock4 = control.createMock(Security.class);
		securityMock5 = control.createMock(Security.class);
		securityMock6 = control.createMock(Security.class);
	}

	@Test
	public void testUpdateRange_Ctor() {
		UpdateRange service = new UpdateRange(5, 10, true);
		assertEquals(5, service.getStartIndex());
		assertEquals(10, service.getEndIndex());
		assertTrue(service.isInserted());
		
		service = new UpdateRange(20, 50, false);
		assertEquals(20, service.getStartIndex());
		assertEquals(50, service.getEndIndex());
		assertFalse(service.isInserted());
	}
	
	@Test
	public void testUpdateRange_Equals() {
		UpdateRange service = new UpdateRange(7, 9, false);
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		
		Variant<Integer> vSI = new Variant<>(7, 12);
		Variant<Integer> vEI = new Variant<>(vSI, 9, 10);
		Variant<Boolean> vINS = new Variant<>(vEI, false, true);
		Variant<?> iterator = vINS;
		int foundCnt = 0;
		UpdateRange x, found = null;
		do {
			x = new UpdateRange(vSI.get(), vEI.get(), vINS.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(7, found.getStartIndex());
		assertEquals(9, found.getEndIndex());
		assertFalse(found.isInserted());
	}
	
	@Test
	public void testUpdateRange_ToString() {
		UpdateRange service = new UpdateRange(7, 9, false);
		
		String expected = "UpdateRange[startIndex=7,endIndex=9,inserted=false]";
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testCacheEntry_Ctor1() {
		CacheEntry service = new CacheEntry(securityMock1);
		
		assertSame(securityMock1, service.getSecurity());
		assertTrue(service.isInserted());
		assertTrue(service.isUpdated());
	}
	
	@Test
	public void testCacheEntry_Ctor3_Objects() {
		CacheEntry service = new CacheEntry(securityMock1,
				new AtomicInteger(5), new AtomicBoolean(false));
		
		assertSame(securityMock1, service.getSecurity());
		assertFalse(service.isInserted());
		assertTrue(service.isUpdated());
	}
	
	@Test
	public void testCacheEntry_Ctor3_Prim() {
		CacheEntry service = new CacheEntry(securityMock1, 10, true);
		
		assertSame(securityMock1, service.getSecurity());
		assertTrue(service.isInserted());
		assertTrue(service.isUpdated());
	}
	
	@Test
	public void testCacheEntry_Reset() {
		CacheEntry service = new CacheEntry(securityMock1);
		
		service.reset();
		
		assertFalse(service.isInserted());
		assertFalse(service.isUpdated());
	}
	
	@Test
	public void testCacheEntry_AddUpdate() {
		CacheEntry service = new CacheEntry(securityMock1);
		
		service.addUpdate();
		
		assertTrue(service.isInserted());
		assertTrue(service.isUpdated());
		
		service.reset();
		service.addUpdate();
		
		assertFalse(service.isInserted());
		assertTrue(service.isUpdated());
	}
	
	@Test
	public void testCacheEntry_Equals() {
		CacheEntry service = new CacheEntry(securityMock1,
				new AtomicInteger(5), new AtomicBoolean(true));
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		
		Variant<Security> vSEC = new Variant<>(securityMock1, securityMock2);
		Variant<Integer> vUC = new Variant<>(vSEC, 5, 15);
		Variant<Boolean> vINS = new Variant<>(vUC, true, false);
		Variant<?> iterator = vINS;
		int foundCnt = 0;
		CacheEntry x, found = null;
		do {
			x = new CacheEntry(vSEC.get(),
				new AtomicInteger(vUC.get()),
				new AtomicBoolean(vINS.get()));
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(securityMock1, found.getSecurity());
		assertTrue(service.isInserted());
		assertTrue(service.isUpdated());
	}
	
	@Test
	public void testCacheEntry_ToString() {
		CacheEntry service = new CacheEntry(securityMock1);
		service.reset();
		service.addUpdate();
		service.addUpdate();

		String expected = new StringBuilder()
				.append("CacheEntry[security=")
				.append(securityMock1)
				.append(",updateCount=2,inserted=false]")
				.toString();
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testCache_Clear() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		entries.add(new CacheEntry(securityMock1));
		entries.add(new CacheEntry(securityMock2));
		entries.add(new CacheEntry(securityMock3));
		indices.put(securityMock1, 0);
		indices.put(securityMock2, 1);
		indices.put(securityMock3, 2);
		
		service.clear();
		
		assertEquals(0, entries.size());
		assertEquals(0, indices.size());
	}
	
	@Test
	public void testCache_GetSecuritiesCount() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		entries.add(new CacheEntry(securityMock1));
		entries.add(new CacheEntry(securityMock2));
		entries.add(new CacheEntry(securityMock3));

		assertEquals(3, service.getSecuritiesCount());
	}
	
	@Test
	public void testCache_GetSecurity() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		entries.add(new CacheEntry(securityMock1));
		entries.add(new CacheEntry(securityMock2));
		entries.add(new CacheEntry(securityMock3));
		indices.put(securityMock1, 0);
		indices.put(securityMock2, 1);
		indices.put(securityMock3, 2);

		assertSame(securityMock1, service.getSecurity(0));
		assertSame(securityMock2, service.getSecurity(1));
		assertSame(securityMock3, service.getSecurity(2));
	}
	
	@Test
	public void testCache_AddUpdate_FirstTime() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);

		service.addUpdate(securityMock1);
		service.addUpdate(securityMock2);
		service.addUpdate(securityMock3);
		
		assertEquals(Integer.valueOf(0), indices.get(securityMock1));
		assertEquals(Integer.valueOf(1), indices.get(securityMock2));
		assertEquals(Integer.valueOf(2), indices.get(securityMock3));
		assertEquals(new CacheEntry(securityMock1), entries.get(0));
		assertEquals(new CacheEntry(securityMock2), entries.get(1));
		assertEquals(new CacheEntry(securityMock3), entries.get(2));
	}
	
	@Test
	public void testCache_AddUpdate_NextTimes() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		entries.add(new CacheEntry(securityMock1));
		entries.add(new CacheEntry(securityMock2));
		indices.put(securityMock1, 0);
		indices.put(securityMock2, 1);
		
		service.addUpdate(securityMock1);
		service.addUpdate(securityMock1);
		service.addUpdate(securityMock2);
		
		assertEquals(Integer.valueOf(0), indices.get(securityMock1));
		assertEquals(Integer.valueOf(1), indices.get(securityMock2));
		assertEquals(new CacheEntry(securityMock1, 3, true), entries.get(0));
		assertEquals(new CacheEntry(securityMock2, 2, true), entries.get(1));
	}
	
	@Test
	public void testCache_GetUpdatesAndReset_2ConsecutiveRanges() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		CacheEntry ce1, ce2, ce3, ce4;
		entries.add(ce1 = new CacheEntry(securityMock1));
		entries.add(ce2 = new CacheEntry(securityMock2));
		entries.add(ce3 = new CacheEntry(securityMock3));
		entries.add(ce4 = new CacheEntry(securityMock4));
		entries.add(new CacheEntry(securityMock5));
		entries.add(new CacheEntry(securityMock6));
		indices.put(securityMock1, 0);
		indices.put(securityMock2, 1);
		indices.put(securityMock3, 2);
		indices.put(securityMock4, 3);
		indices.put(securityMock5, 4);
		indices.put(securityMock6, 5);
		ce1.reset();
		ce2.reset();
		ce3.reset();
		ce3.addUpdate();
		ce4.reset();
		ce4.addUpdate();
		// The case:
		// * the first two aren't updated
		// * the next two updated
		// * and the last two are inserted
		
		List<UpdateRange> actual = service.getUpdatesAndReset();
		
		List<UpdateRange> expectedUpdates = new ArrayList<>();
		expectedUpdates.add(new UpdateRange(2, 3, false));
		expectedUpdates.add(new UpdateRange(4, 5, true));
		assertEquals(expectedUpdates, actual);
		List<CacheEntry> expectedEntries = new ArrayList<>();
		expectedEntries.add(new CacheEntry(securityMock1, 0, false));
		expectedEntries.add(new CacheEntry(securityMock2, 0, false));
		expectedEntries.add(new CacheEntry(securityMock3, 0, false));
		expectedEntries.add(new CacheEntry(securityMock4, 0, false));
		expectedEntries.add(new CacheEntry(securityMock5, 0, false));
		expectedEntries.add(new CacheEntry(securityMock6, 0, false));
		assertEquals(expectedEntries, entries);
	}
	
	@Test
	public void testCache_GetUpdatesAndReset_RangeInTheMiddle() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		CacheEntry ce1, ce2, ce3, ce4, ce5, ce6;
		entries.add(ce1 = new CacheEntry(securityMock1));
		entries.add(ce2 = new CacheEntry(securityMock2));
		entries.add(ce3 = new CacheEntry(securityMock3));
		entries.add(ce4 = new CacheEntry(securityMock4));
		entries.add(ce5 = new CacheEntry(securityMock5));
		entries.add(ce6 = new CacheEntry(securityMock6));
		indices.put(securityMock1, 0);
		indices.put(securityMock2, 1);
		indices.put(securityMock3, 2);
		indices.put(securityMock4, 3);
		indices.put(securityMock5, 4);
		indices.put(securityMock6, 5);
		ce1.reset();
		ce2.reset();
		ce3.reset();
		ce3.addUpdate();
		ce4.reset();
		ce4.addUpdate();
		ce5.reset();
		ce5.addUpdate();
		ce6.reset();
		// The case: three updates in the middle
		
		List<UpdateRange> actual = service.getUpdatesAndReset();
		
		List<UpdateRange> expectedUpdates = new ArrayList<>();
		expectedUpdates.add(new UpdateRange(2, 4, false));
		assertEquals(expectedUpdates, actual);
		List<CacheEntry> expectedEntries = new ArrayList<>();
		expectedEntries.add(new CacheEntry(securityMock1, 0, false));
		expectedEntries.add(new CacheEntry(securityMock2, 0, false));
		expectedEntries.add(new CacheEntry(securityMock3, 0, false));
		expectedEntries.add(new CacheEntry(securityMock4, 0, false));
		expectedEntries.add(new CacheEntry(securityMock5, 0, false));
		expectedEntries.add(new CacheEntry(securityMock6, 0, false));
		assertEquals(expectedEntries, entries);
	}
	
	@Test
	public void testCache_GetUpdatesAndReset_WithGapBetween() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		CacheEntry ce1, ce2, ce3, ce4, ce5, ce6;
		entries.add(ce1 = new CacheEntry(securityMock1));
		entries.add(ce2 = new CacheEntry(securityMock2));
		entries.add(ce3 = new CacheEntry(securityMock3));
		entries.add(ce4 = new CacheEntry(securityMock4));
		entries.add(ce5 = new CacheEntry(securityMock5));
		entries.add(ce6 = new CacheEntry(securityMock6));
		indices.put(securityMock1, 0);
		indices.put(securityMock2, 1);
		indices.put(securityMock3, 2);
		indices.put(securityMock4, 3);
		indices.put(securityMock5, 4);
		indices.put(securityMock6, 5);
		ce1.reset();
		ce1.addUpdate();
		ce2.reset();
		ce3.reset();
		ce3.addUpdate();
		ce4.reset();
		ce4.addUpdate();
		ce5.reset();
		ce6.reset();
		ce6.addUpdate();
		
		List<UpdateRange> actual = service.getUpdatesAndReset();
		
		List<UpdateRange> expectedUpdates = new ArrayList<>();
		expectedUpdates.add(new UpdateRange(0, 0, false));
		expectedUpdates.add(new UpdateRange(2, 3, false));
		expectedUpdates.add(new UpdateRange(5, 5, false));
		assertEquals(expectedUpdates, actual);
		List<CacheEntry> expectedEntries = new ArrayList<>();
		expectedEntries.add(new CacheEntry(securityMock1, 0, false));
		expectedEntries.add(new CacheEntry(securityMock2, 0, false));
		expectedEntries.add(new CacheEntry(securityMock3, 0, false));
		expectedEntries.add(new CacheEntry(securityMock4, 0, false));
		expectedEntries.add(new CacheEntry(securityMock5, 0, false));
		expectedEntries.add(new CacheEntry(securityMock6, 0, false));
		assertEquals(expectedEntries, entries);
	}
	
	@Test
	public void testCache_GetUpdatesAndReset_UpdatesAtHead() {
		List<CacheEntry> entries = new ArrayList<>();
		Map<Security, Integer> indices = new HashMap<>();
		Cache service = new Cache(entries, indices);
		CacheEntry ce1, ce2, ce3, ce4, ce5, ce6;
		entries.add(ce1 = new CacheEntry(securityMock1));
		entries.add(ce2 = new CacheEntry(securityMock2));
		entries.add(ce3 = new CacheEntry(securityMock3));
		entries.add(ce4 = new CacheEntry(securityMock4));
		entries.add(ce5 = new CacheEntry(securityMock5));
		entries.add(ce6 = new CacheEntry(securityMock6));
		indices.put(securityMock1, 0);
		indices.put(securityMock2, 1);
		indices.put(securityMock3, 2);
		indices.put(securityMock4, 3);
		indices.put(securityMock5, 4);
		indices.put(securityMock6, 5);
		ce1.reset();
		ce1.addUpdate();
		ce2.reset();
		ce2.addUpdate();
		ce3.reset();
		ce3.addUpdate();
		ce4.reset();
		ce5.reset();
		ce6.reset();
		
		List<UpdateRange> actual = service.getUpdatesAndReset();
		
		List<UpdateRange> expectedUpdates = new ArrayList<>();
		expectedUpdates.add(new UpdateRange(0, 2, false));
		assertEquals(expectedUpdates, actual);
		List<CacheEntry> expectedEntries = new ArrayList<>();
		expectedEntries.add(new CacheEntry(securityMock1, 0, false));
		expectedEntries.add(new CacheEntry(securityMock2, 0, false));
		expectedEntries.add(new CacheEntry(securityMock3, 0, false));
		expectedEntries.add(new CacheEntry(securityMock4, 0, false));
		expectedEntries.add(new CacheEntry(securityMock5, 0, false));
		expectedEntries.add(new CacheEntry(securityMock6, 0, false));
		assertEquals(expectedEntries, entries);
	}

}
