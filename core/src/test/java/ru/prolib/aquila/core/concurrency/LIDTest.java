package ru.prolib.aquila.core.concurrency;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class LIDTest {

	@Test
	public void testSorting() {
		long iid = LID.getCurrentIID();
		LID lid1 = LID.createInstance(),
			lid2 = LID.createInstance(),
			lid3 = LID.createInstance(),
			lid4 = LID.createInstance();
		assertEquals(iid, lid1.getIID());
		assertEquals(iid + 1, lid2.getIID());
		assertEquals(iid + 2, lid3.getIID());
		assertEquals(iid + 3, lid4.getIID());
		
		List<LID> actual = new ArrayList<>();
		actual.add(lid4);
		actual.add(lid1);
		actual.add(lid2);
		actual.add(lid3);
		Collections.sort(actual);
		
		List<LID> expected = new ArrayList<>();
		expected.add(lid1);
		expected.add(lid2);
		expected.add(lid3);
		expected.add(lid4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIsLastCreatedLID() {
		LID lid1 = LID.createInstance(),
			lid2 = LID.createInstance(),
			lid3 = null;
		
		assertFalse(LID.isLastCreatedLID(lid1));
		assertTrue(LID.isLastCreatedLID(lid2));
		
		lid3 = LID.createInstance();
		
		assertFalse(LID.isLastCreatedLID(lid1));
		assertFalse(LID.isLastCreatedLID(lid2));
		assertTrue(LID.isLastCreatedLID(lid3));
	}

}
