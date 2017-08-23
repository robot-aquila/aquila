package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.utils.Variant;

public class TSeriesNodeTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private Interval interval1, interval2;
	private ArrayList<Object> valuesStub1, valuesStub2, valuesStub3;
	private TSeriesNode node;

	@Before
	public void setUp() throws Exception {
		interval1 = Interval.of(T("2017-08-21T18:00:00Z"), T("2017-08-21T18:00:05Z"));
		interval2 = Interval.of(T("2017-08-21T18:00:05Z"), T("2017-08-21T18:00:10Z"));
		valuesStub1 = new ArrayList<>();
		valuesStub2 = new ArrayList<>();
		valuesStub3 = new ArrayList<>();
		node = new TSeriesNode(interval1, valuesStub1);
	}

	@Test
	public void testCtor() {
		assertEquals(interval1, node.getInterval());
		assertEquals(-1, node.getNodeIndex());
	}
	
	@Test
	public void testSetNodeIndex() {
		node.setNodeIndex(815);
		assertEquals(815, node.getNodeIndex());
	}
	
	@Test
	public void testNodeIndexIncrement() {
		node.setNodeIndex(0);
		node.nodeIndexIncrement();
		assertEquals(1, node.getNodeIndex());
		node.nodeIndexIncrement();
		assertEquals(2, node.getNodeIndex());
		node.setNodeIndex(800);
		node.nodeIndexIncrement();
		assertEquals(801, node.getNodeIndex());
	}
	
	@Test
	public void testNodeIndexDecrement() {
		node.setNodeIndex(700);
		node.nodeIndexDecrement();
		assertEquals(699, node.getNodeIndex());
		node.nodeIndexDecrement();
		assertEquals(698, node.getNodeIndex());
	}
	
	@Test
	public void testSet() {
		assertNull(node.set(2, new Integer(415)));
		assertEquals(415, node.set(2, 240));
		assertNull(node.set(4, false));
		assertEquals(false, node.set(4, true));
		
		ArrayList<Object> expected = new ArrayList<>();
		expected.add(null);
		expected.add(null);
		expected.add(new Integer(240));
		expected.add(null);
		expected.add(new Boolean(true));
		assertEquals(expected, valuesStub1);
	}
	
	@Test
	public void testGet() {
		valuesStub1.add(null);
		valuesStub1.add(new Long(245L));
		valuesStub1.add(null);
		valuesStub1.add(new Boolean(false));
		valuesStub1.add(null);
		
		assertNull(node.get(0));
		assertEquals(245L, node.get(1));
		assertNull(node.get(2));
		assertEquals(false, node.get(3));
		assertNull(node.get(4));
		assertNull(node.get(5));
	}
	
	@Test
	public void testGetIntervalStart() {
		assertEquals(T("2017-08-21T18:00:00Z"), node.getIntervalStart());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertFalse(node.equals(null));
		assertFalse(node.equals(this));
		assertTrue(node.equals(node));
	}
	
	@Test
	public void testEquals() {
		node.setNodeIndex(117);
		valuesStub1.add(null);
		valuesStub1.add(14);
		valuesStub1.add(null);
		valuesStub2.add(null);
		valuesStub2.add(14);
		valuesStub2.add(null);
		valuesStub3.add(813);
		valuesStub3.add(null);
		
		Variant<Interval> vInt = new Variant<>(interval1, interval2);
		Variant<ArrayList<Object>> vVal = new Variant<>(vInt, valuesStub2, valuesStub3);
		Variant<Integer> vInd = new Variant<>(vVal, 117, 256);
		Variant<?> iterator = vInd;
		int foundCnt = 0;
		TSeriesNode x, found = null;
		do {
			x = new TSeriesNode(vInt.get(), vVal.get());
			x.setNodeIndex(vInd.get());
			if ( node.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(interval1, found.getInterval());
		assertEquals(null, found.get(0));
		assertEquals(14, found.get(1));
		assertEquals(null, found.get(2));
		assertEquals(117, found.getNodeIndex());
	}

}
