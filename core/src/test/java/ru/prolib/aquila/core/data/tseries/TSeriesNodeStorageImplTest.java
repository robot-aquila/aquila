package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;

public class TSeriesNodeStorageImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static Interval IM1(String timeString) {
		return Interval.of(T(timeString), Duration.ofMinutes(1));
	}
	
	private Interval interval1, interval2, interval3;
	private TimeFrame timeFrame;
	private List<TSeriesNode> nodeList, expectedNodeList;
	private Map<Instant, TSeriesNode> nodeMap, expectedNodeMap;
	private TSeriesNode node1, node2, node3;
	private TSeriesNodeStorageImpl storage;

	@Before
	public void setUp() throws Exception {
		interval1 = Interval.of(T("2017-08-21T22:50:00Z"), T("2017-08-21T22:55:00Z"));
		interval2 = Interval.of(T("2017-08-21T22:55:00Z"), T("2017-08-21T23:00:00Z"));
		interval3 = Interval.of(T("2017-08-21T23:00:00Z"), T("2017-08-21T23:05:00Z"));
		node1 = new TSeriesNode(interval1);
		node2 = new TSeriesNode(interval2);
		node3 = new TSeriesNode(interval3);
		timeFrame = TimeFrame.M5;
		nodeList = new ArrayList<>();
		expectedNodeList = new ArrayList<>();
		nodeMap = new LinkedHashMap<>();
		expectedNodeMap = new LinkedHashMap<>();
		storage = new TSeriesNodeStorageImpl(timeFrame, nodeList, nodeMap);
	}
	
	@Test
	public void testCtor() {
		assertSame(TimeFrame.M5, storage.getTimeFrame());
		assertTrue(LID.isLastCreatedLID(storage.getLID()));
		assertEquals(0, storage.registerSeries());
		assertEquals(1, storage.registerSeries());
	}
	
	@Test
	public void testSet_ExistingNode() {
		node1.setNodeIndex(0);
		node1.set(0, 240);
		nodeList.add(node1);
		nodeMap.put(interval1.getStart(), node1);
		storage.setLastNode(node1);
		
		TSeriesUpdate actual = storage.setValue(T("2017-08-21T22:53:17Z"), 0, 860);
		
		TSeriesUpdate expected = new TSeriesUpdateImpl(interval1)
				.setNewNode(false)
				.setNodeIndex(0)
				.setOldValue(240)
				.setNewValue(860);
		assertEquals(expected, actual);
		assertEquals(860, node1.get(0));
		expectedNodeList.add(node1);
		assertEquals(expectedNodeList, nodeList);
		expectedNodeMap.put(node1.getIntervalStart(), node1);
		assertEquals(expectedNodeMap, nodeMap);
		assertEquals(node1, storage.getLastNode());
	}
	
	@Test
	public void testSet_NewNode_FirstNode() {
		TSeriesUpdate actual = storage.setValue(T("2017-08-21T22:53:17Z"), 0, 860);
		
		TSeriesUpdate expected = new TSeriesUpdateImpl(interval1)
				.setNewNode()
				.setNodeIndex(0)
				.setOldValue(null)
				.setNewValue(860);
		assertEquals(expected, actual);
		node1.setNodeIndex(0);
		node1.set(0, 860);
		expectedNodeList.add(node1);
		assertEquals(expectedNodeList, nodeList);
		expectedNodeMap.put(node1.getIntervalStart(), node1);
		assertEquals(expectedNodeMap, nodeMap);
		assertEquals(node1, storage.getLastNode());
	}
	
	@Test
	public void testSet_NewNode_NewLastNode() {
		storage.setValue(node1.getIntervalStart(), 0, 500);
		storage.setValue(node2.getIntervalStart(), 0, 510);
		
		TSeriesUpdate actual = storage.setValue(T("2017-08-21T23:01:58Z"), 0, 206);
		
		TSeriesUpdate expected = new TSeriesUpdateImpl(interval3)
				.setNewNode()
				.setNodeIndex(2)
				.setOldValue(null)
				.setNewValue(206);
		assertEquals(expected, actual);
		node1.setNodeIndex(0);
		node1.set(0, 500);
		node2.setNodeIndex(1);
		node2.set(0, 510);
		node3.setNodeIndex(2);
		node3.set(0, 206);
		expectedNodeList.add(node1);
		expectedNodeList.add(node2);
		expectedNodeList.add(node3);
		assertEquals(expectedNodeList, nodeList);
		expectedNodeMap.put(node1.getIntervalStart(), node1);
		expectedNodeMap.put(node2.getIntervalStart(), node2);
		expectedNodeMap.put(node3.getIntervalStart(), node3);
		assertEquals(expectedNodeMap, nodeMap);
		assertEquals(node3, storage.getLastNode());
	}
	
	@Test
	public void testSet_NewNode_InsertNode() {
		storage.setValue(node1.getIntervalStart(), 5, 15);
		storage.setValue(node3.getIntervalStart(), 5, 35);
		
		TSeriesUpdate actual = storage.setValue(T("2017-08-21T22:57:17Z"), 5, 25);
		
		TSeriesUpdate expected = new TSeriesUpdateImpl(interval2)
				.setNewNode()
				.setNodeIndex(1)
				.setOldValue(null)
				.setNewValue(25);
		assertEquals(expected, actual);
		node1.setNodeIndex(0);
		node1.set(5, 15);
		node2.setNodeIndex(1);
		node2.set(5, 25);
		node3.setNodeIndex(2);
		node3.set(5, 35);
		expectedNodeList.add(node1);
		expectedNodeList.add(node2);
		expectedNodeList.add(node3);
		assertEquals(expectedNodeList, nodeList);
		expectedNodeMap.put(node1.getIntervalStart(), node1);
		expectedNodeMap.put(node2.getIntervalStart(), node2);
		expectedNodeMap.put(node3.getIntervalStart(), node3);
		assertEquals(expectedNodeMap, nodeMap);
		assertEquals(node3, storage.getLastNode());
	}
	
	@Test
	public void testSet_NewNode_InsertNodeBugFix_LostBreakOnInsert() throws Exception {
		storage = new TSeriesNodeStorageImpl(TimeFrame.M1, nodeList, nodeMap);
		storage.setValue(T("2017-12-31T10:00:00Z"), 3, 100);
		storage.setValue(T("2017-10-01T12:01:00Z"), 3, 101);
		storage.setValue(T("2017-10-01T12:02:00Z"), 3, 102);
		storage.setValue(T("2017-10-01T12:03:00Z"), 3, 103);
		storage.setValue(T("2017-09-15T18:01:00Z"), 3, 104);
		storage.setValue(T("2017-09-15T18:02:00Z"), 3, 105);
		storage.setValue(T("2017-09-15T18:03:00Z"), 3, 106);
		
		assertEquals(7, nodeList.size());
		assertEquals(7, nodeMap.size());
		TSeriesNode node;
		List<TSeriesNode> expected = new ArrayList<>();
		expected.add(node = new TSeriesNode(IM1("2017-09-15T18:01:00Z")));
		node.setNodeIndex(0);
		node.set(3, 104);
		expected.add(node = new TSeriesNode(IM1("2017-09-15T18:02:00Z")));
		node.setNodeIndex(1);
		node.set(3, 105);
		expected.add(node = new TSeriesNode(IM1("2017-09-15T18:03:00Z")));
		node.setNodeIndex(2);
		node.set(3, 106);
		expected.add(node = new TSeriesNode(IM1("2017-10-01T12:01:00Z")));
		node.setNodeIndex(3);
		node.set(3, 101);
		expected.add(node = new TSeriesNode(IM1("2017-10-01T12:02:00Z")));
		node.setNodeIndex(4);
		node.set(3, 102);
		expected.add(node = new TSeriesNode(IM1("2017-10-01T12:03:00Z")));
		node.setNodeIndex(5);
		node.set(3, 103);
		expected.add(node = new TSeriesNode(IM1("2017-12-31T10:00:00Z")));
		node.setNodeIndex(6);
		node.set(3, 100);
		assertEquals(expected, nodeList);
		assertEquals(expected.get(6), storage.getLastNode());
	}
	
	@Test
	public void testSet_NewNode_InsertFirstNode() {
		storage.setValue(node2.getIntervalStart(), 8, 200);
		storage.setValue(node3.getIntervalStart(), 8, 300);
		
		TSeriesUpdate actual = storage.setValue(T("2017-08-21T22:51:00Z"), 8, 100);
		
		TSeriesUpdate expected = new TSeriesUpdateImpl(interval1)
				.setNewNode()
				.setNodeIndex(0)
				.setOldValue(null)
				.setNewValue(100);
		assertEquals(expected, actual);
		node1.setNodeIndex(0);
		node1.set(8,  100);
		node2.setNodeIndex(1);
		node2.set(8,  200);
		node3.setNodeIndex(2);
		node3.set(8,  300);
		expectedNodeList.add(node1);
		expectedNodeList.add(node2);
		expectedNodeList.add(node3);
		assertEquals(expectedNodeList, nodeList);
		expectedNodeMap.put(node1.getIntervalStart(), node1);
		expectedNodeMap.put(node2.getIntervalStart(), node2);
		expectedNodeMap.put(node3.getIntervalStart(), node3);
		assertEquals(expectedNodeMap, nodeMap);
		assertEquals(node3, storage.getLastNode());
	}
	
	@Test
	public void testGet2_TI_ExistingNode() {
		storage.setValue(T("2017-08-21T22:52:00Z"), 8, 255);
		
		assertEquals(255, storage.getValue(T("2017-08-21T22:52:00Z"), 8));
		assertEquals(255, storage.getValue(T("2017-08-21T22:50:00Z"), 8));
		assertEquals(255, storage.getValue(T("2017-08-21T22:54:59Z"), 8));
	}
	
	@Test
	public void testGet2_TI_NonExistingNode() {
		storage.setValue(interval1.getStart(), 8, 12);
		storage.setValue(interval3.getStart(), 8, 19);
		
		assertNull(storage.getValue(T("2017-08-21T22:57:00Z"), 8));
		assertNull(storage.getValue(T("2017-08-21T22:55:00Z"), 8));
		assertNull(storage.getValue(T("2017-08-21T22:59:59Z"), 8));
	}
	
	@Test
	public void testGet2_II_Positive_Ok() throws Exception {
		storage.setValue(interval1.getStart(), 8, 12);
		storage.setValue(interval2.getStart(), 5, 24);
		storage.setValue(interval3.getStart(), 8, 19);
		
		assertEquals(12, storage.getValue(0, 8));
		assertEquals(24, storage.getValue(1, 5));
		assertEquals(19, storage.getValue(2, 8));
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet2_II_Positive_ThrowsOutOfRange() throws Exception {
		storage.setValue(interval1.getStart(), 8, 12);
		storage.setValue(interval2.getStart(), 5, 24);
		storage.setValue(interval3.getStart(), 8, 19);

		storage.getValue(3, 8);
	}
	
	@Test
	public void testGet2_II_Negative_Ok() throws Exception {
		storage.setValue(interval1.getStart(), 8, 12);
		storage.setValue(interval2.getStart(), 8, 24);
		storage.setValue(interval3.getStart(), 8, 19);

		assertEquals(24, storage.getValue(-1, 8));
		assertEquals(12, storage.getValue(-2, 8));
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet2_II_Negative_ThrowsOutOfRange() throws Exception {
		storage.setValue(interval1.getStart(), 8, 12);
		storage.setValue(interval2.getStart(), 8, 24);
		storage.setValue(interval3.getStart(), 8, 19);

		storage.getValue(-3, 8);
	}
	
	@Test
	public void testGet1_Ok() throws Exception {
		storage.setValue(interval1.getStart(), 8, 12);
		
		assertEquals(12, storage.getValue(8));
	}
	
	@Test
	public void testGet1_NullIfNoValue() throws Exception {
		assertNull(storage.getValue(0));
		assertNull(storage.getValue(8));
	}
	
	@Test
	public void testClear() {
		storage.setValue(interval1.getStart(), 1, 10);
		storage.setValue(interval2.getStart(), 1, 20);
		storage.setValue(interval3.getStart(), 1, 30);
		
		storage.clear();
		
		assertEquals(0, nodeList.size());
		assertEquals(0, nodeMap.size());
		assertNull(storage.getLastNode());
	}
	
	@Test
	public void testGetLength() {
		assertEquals(0, storage.getLength());
		storage.setValue(interval1.getStart(), 1, 10);
		assertEquals(1, storage.getLength());
		storage.setValue(interval2.getStart(), 1, 20);
		assertEquals(2, storage.getLength());
		storage.setValue(interval3.getStart(), 1, 30);
		assertEquals(3, storage.getLength());
	}
	
	@Test
	public void testGetIntervalStart_Negative_Ok() throws Exception {
		storage.setValue(T("2017-08-21T22:50:18Z"), 8, 12);
		storage.setValue(T("2017-08-21T22:57:28Z"), 5, 24);
		storage.setValue(T("2017-08-21T23:02:35Z"), 8, 19);

		assertEquals(T("2017-08-21T22:50:00Z"), storage.getIntervalStart(-2));
		assertEquals(T("2017-08-21T22:55:00Z"), storage.getIntervalStart(-1));		
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGetIntervalStart_Negative_ThrowsOutOfRange() throws Exception {
		storage.setValue(T("2017-08-21T22:50:18Z"), 8, 12);
		storage.setValue(T("2017-08-21T22:57:28Z"), 5, 24);
		storage.setValue(T("2017-08-21T23:02:35Z"), 8, 19);

		storage.getIntervalStart(-3);
	}
	
	@Test
	public void testGetIntervalStart_Positive_Ok() throws Exception {
		storage.setValue(T("2017-08-21T22:50:18Z"), 8, 12);
		storage.setValue(T("2017-08-21T22:57:28Z"), 5, 24);
		storage.setValue(T("2017-08-21T23:02:35Z"), 8, 19);

		assertEquals(T("2017-08-21T22:50:00Z"), storage.getIntervalStart(0));
		assertEquals(T("2017-08-21T22:55:00Z"), storage.getIntervalStart(1));
		assertEquals(T("2017-08-21T23:00:00Z"), storage.getIntervalStart(2));
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGetIntervalStart_Positive_ThrowsOutOfRange() throws Exception {
		storage.setValue(T("2017-08-21T22:50:18Z"), 8, 12);
		storage.setValue(T("2017-08-21T22:57:28Z"), 5, 24);
		storage.setValue(T("2017-08-21T23:02:35Z"), 8, 19);

		storage.getIntervalStart(3);
	}
	
	@Test
	public void testGetIntervalIndex() {
		storage.setValue(T("2017-08-21T22:50:18Z"), 8, 12);
		storage.setValue(T("2017-08-21T22:57:28Z"), 5, 24);
		storage.setValue(T("2017-08-21T23:02:35Z"), 8, 19);

		assertEquals(-1, storage.getIntervalIndex(T("2017-08-21T22:45:00Z")));
		assertEquals( 0, storage.getIntervalIndex(T("2017-08-21T22:50:18Z")));
		assertEquals( 0, storage.getIntervalIndex(T("2017-08-21T22:50:00Z")));
		assertEquals( 0, storage.getIntervalIndex(T("2017-08-21T22:54:59Z")));
		assertEquals( 1, storage.getIntervalIndex(T("2017-08-21T22:57:28Z")));
		assertEquals( 1, storage.getIntervalIndex(T("2017-08-21T22:55:00Z")));
		assertEquals( 1, storage.getIntervalIndex(T("2017-08-21T22:59:59.999Z")));
		assertEquals( 2, storage.getIntervalIndex(T("2017-08-21T23:02:35Z")));
		assertEquals( 2, storage.getIntervalIndex(T("2017-08-21T23:00:00Z")));
		assertEquals( 2, storage.getIntervalIndex(T("2017-08-21T23:04:59Z")));
		assertEquals(-1, storage.getIntervalIndex(T("2017-08-21T23:05:00Z")));
	}
	
}
