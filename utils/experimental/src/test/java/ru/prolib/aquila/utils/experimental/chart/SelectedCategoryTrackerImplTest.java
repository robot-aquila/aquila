package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SelectedCategoryTrackerImplTest {
	private SelectedCategoryTrackerImpl service;

	@Before
	public void setUp() throws Exception {
		service = new SelectedCategoryTrackerImpl();
	}
	
	@Test
	public void testCtor() {
		assertFalse(service.isSelected());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetAbsoluteIndex_ThrowsIfDeselected() {
		service.getAbsoluteIndex();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetVisibleIndex_ThrowsIfDeselected() {
		service.getVisibleIndex();
	}
	
	@Test
	public void testMakeDeselected() {
		service.makeSelected(60, 25);
		service.makeDeselected();
		
		assertFalse(service.isSelected());
	}
	
	@Test
	public void testMakeSelected() {
		service.makeSelected(60,  25);
		
		assertTrue(service.isSelected());
		assertEquals(60, service.getAbsoluteIndex());
		assertEquals(25, service.getVisibleIndex());
	}

	@Test
	public void testToString() {
		service.makeSelected(50, 10);
		String expected = new StringBuilder()
				.append("SelectedCategoryTrackerImpl[")
				.append("selected=true,")
				.append("absoluteIndex=50,")
				.append("visibleIndex=10")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		service.makeSelected(50, 10);
		Variant<Boolean> vSEL = new Variant<>(true, false);
		Variant<Integer> vAI = new Variant<>(vSEL, 50, 25);
		Variant<Integer> vVI = new Variant<>(vAI, 10, 40);
		Variant<?> iterator = vVI;
		int foundCnt = 0;
		SelectedCategoryTrackerImpl x, found = null;
		do {
			x = new SelectedCategoryTrackerImpl();
			x.makeSelected(vAI.get(), vVI.get());
			if ( ! vSEL.get() ) {
				x.makeDeselected();
			}
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertTrue(found.isSelected());
		assertEquals(50, found.getAbsoluteIndex());
		assertEquals(10, found.getVisibleIndex());
	}

}
