package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class OEEntrySetImplTest {
	private List<OEEntry> entries;
	private OEEntrySetImpl service;

	@Before
	public void setUp() throws Exception {
		entries = new ArrayList<>();
		service = new OEEntrySetImpl(entries);
	}
	
	@Test
	public void testGetMinPrice() {
		entries.add(new OEEntryImpl(true,  of("34.92")));
		entries.add(new OEEntryImpl(false, of("15.55")));
		entries.add(new OEEntryImpl(true,  of("52.64")));
		entries.add(new OEEntryImpl(false, of( "8.27")));
		
		assertEquals(of("8.27"), service.getMinPrice());
	}
	
	@Test
	public void testGetMaxPrice() {
		entries.add(new OEEntryImpl(true,  of("34.92")));
		entries.add(new OEEntryImpl(false, of("15.55")));
		entries.add(new OEEntryImpl(false, of("52.64")));
		entries.add(new OEEntryImpl(true,  of( "8.27")));

		assertEquals(of("52.64"), service.getMaxPrice());
	}
	
	@Test
	public void testGetEntries() {
		entries.add(new OEEntryImpl(false, of("34.92")));
		entries.add(new OEEntryImpl(false, of("15.55")));
		entries.add(new OEEntryImpl(true,  of("52.64")));
		entries.add(new OEEntryImpl(true,  of( "8.27")));

		List<OEEntry> expected = new ArrayList<>();
		expected.add(new OEEntryImpl(false, of("34.92")));
		expected.add(new OEEntryImpl(false, of("15.55")));
		expected.add(new OEEntryImpl(true,  of("52.64")));
		expected.add(new OEEntryImpl(true,  of( "8.27")));
		assertEquals(expected, service.getEntries());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		entries.add(new OEEntryImpl(false, of("34.92")));
		entries.add(new OEEntryImpl(false, of("15.55")));
		entries.add(new OEEntryImpl(true,  of("52.64")));
		entries.add(new OEEntryImpl(true,  of( "8.27")));
		List<OEEntry> entries1 = new ArrayList<>();
		entries1.add(new OEEntryImpl(false, of("34.92")));
		entries1.add(new OEEntryImpl(false, of("15.55")));
		entries1.add(new OEEntryImpl(true,  of("52.64")));
		entries1.add(new OEEntryImpl(true,  of( "8.27")));
		List<OEEntry> entries2 = new ArrayList<>();
		entries2.add(new OEEntryImpl(true,  of("97.11")));
		entries2.add(new OEEntryImpl(false, of("12.37")));
		entries2.add(new OEEntryImpl(true,  of("83.42")));
		Variant<List<OEEntry>> vEntries = new Variant<>();
		vEntries.add(entries1).add(entries2);
		Variant<?> iterator = vEntries;
		int foundCnt = 0;
		OEEntrySetImpl x, found = null;
		do {
			x = new OEEntrySetImpl(vEntries.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(entries1, found.getEntries());
	}
	
	@Test
	public void testAddEntry() {
		assertSame(service, service.addEntry(new OEEntryImpl(false, of("34.92"))));
		assertSame(service, service.addEntry(new OEEntryImpl(false, of("15.55"))));
		assertSame(service, service.addEntry(new OEEntryImpl(true,  of("52.64"))));
		assertSame(service, service.addEntry(new OEEntryImpl(true,  of( "8.27"))));
		
		List<OEEntry> expected = new ArrayList<>();
		expected.add(new OEEntryImpl(false, of("34.92")));
		expected.add(new OEEntryImpl(false, of("15.55")));
		expected.add(new OEEntryImpl(true,  of("52.64")));
		expected.add(new OEEntryImpl(true,  of( "8.27")));
		assertEquals(expected, service.getEntries());
	}
	
	@Test
	public void testAddEntry2() {
		assertSame(service, service.addEntry(false, of("34.92")));
		assertSame(service, service.addEntry(false, of("15.55")));
		assertSame(service, service.addEntry(true,  of("52.64")));
		assertSame(service, service.addEntry(true,  of( "8.27")));
		
		List<OEEntry> expected = new ArrayList<>();
		expected.add(new OEEntryImpl(false, of("34.92")));
		expected.add(new OEEntryImpl(false, of("15.55")));
		expected.add(new OEEntryImpl(true,  of("52.64")));
		expected.add(new OEEntryImpl(true,  of( "8.27")));
		assertEquals(expected, service.getEntries());
	}

}
