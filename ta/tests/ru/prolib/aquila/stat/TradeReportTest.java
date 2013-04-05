package ru.prolib.aquila.stat;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

/**
 * 2012-02-02
 * $Id: TradeReportTest.java 196 2012-02-02 20:24:38Z whirlwind $
 */
public class TradeReportTest {
	TradeReport trade;
	PositionChange change;

	@Before
	public void setUp() throws Exception {
		trade = new TradeReport();
		change = null;
	}
	
	@Test (expected=TradeReportException.class)
	public void testGetFirstChange_ThrowsIfNoAnyChanges() throws Exception {
		trade.getFirstChange();
	}
	
	@Test
	public void testGetFirstChange_Ok() throws Exception {
		change = new PositionChange(123, 1, 200d);
		trade.addChange(change);
		
		assertSame(change, trade.getFirstChange());
	}
	
	@Test (expected=TradeReportException.class)
	public void testGetLastChange_ThrowsIfNoAnyChanges() throws Exception {
		trade.getLastChange();
	}
	
	@Test
	public void testGetLastChange_Ok() throws Exception {
		trade.addChange(new PositionChange(123, -1, 200d));
		trade.addChange(new PositionChange(124, -3, 210d));
		change = new PositionChange(125, 4, 180d);
		trade.addChange(change);
		
		assertSame(change, trade.getLastChange());
	}
	
	@Test
	public void testAddChange_Ok() throws Exception {
		change = new PositionChange(1, 1, 200d);
		trade.addChange(change);
		List<PositionChange> changes = trade.getChanges();
		assertTrue(changes.contains(change));
	}
	
	@Test
	public void testGetChanges_Ok() throws Exception {
		PositionChange changes[] = {
			new PositionChange(1, 2, 3d),
			new PositionChange(2, 2, 2d),
			new PositionChange(3, 2, 3d),
		}; 
		for ( int i = 0; i < changes.length ; i ++ ) {
			trade.addChange(changes[i]);
		}
		
		List<PositionChange> list = trade.getChanges();
		assertNotNull(list);
		assertEquals(changes.length, list.size());
		for ( int i = 0; i < list.size(); i ++ ) {
			assertSame(changes[i], list.get(i));
		}
	}
	
	@Test
	public void testGetChangeCount_Ok() throws Exception {
		assertEquals(0, trade.getChangeCount());
		trade.addChange(new PositionChange(1, 1, 200d));
		assertEquals(1, trade.getChangeCount());
		trade.addChange(new PositionChange(2, -1, 200d));
		assertEquals(2, trade.getChangeCount());
	}
	
	@Test (expected=TradeReportException.class)
	public void testIsShort_ThrowsIfNoAnyChanges() throws Exception {
		trade.isShort();
	}
	
	@Test
	public void testIsShort_OkForLong() throws Exception {
		trade.addChange(new PositionChange(1, 1, 200d));
		assertFalse(trade.isShort());
	}

	@Test
	public void testIsShort_OkForShort() throws Exception {
		trade.addChange(new PositionChange(1, -1, 200d));
		assertFalse(trade.isLong());
	}

	@Test (expected=TradeReportException.class)
	public void testIsLong_ThrowsIfNoAnyChanges() throws Exception {
		trade.isLong();
	}
	
	@Test
	public void testIsLong_OkForLong() throws Exception {
		trade.addChange(new PositionChange(1, 1, 200d));
		assertTrue(trade.isLong());
	}
	
	@Test
	public void testIsLong_OkForShort() throws Exception {
		trade.addChange(new PositionChange(1, -1, 200d));
		assertFalse(trade.isLong());
	}
	
	@Test
	public void testEquals_EqSameInstance() throws Exception {
		assertTrue(trade.equals(trade));
	}
	
	@Test
	public void testEquals_NeNull() throws Exception {
		assertFalse(trade.equals(null));
	}
	
	@Test
	public void testEquals_NeDifferentClass() throws Exception {
		assertFalse(trade.equals(this));
	}

	@Test
	public void testEquals_NeDiffLength() throws Exception {
		TradeReport other = new TradeReport();
		other.addChange(new PositionChange(1, 2, 3d));
		other.addChange(new PositionChange(2, 2, 3d));
		trade.addChange(new PositionChange(1, 2, 3d));
		assertFalse(trade.equals(other));
	}
	
	@Test
	public void testEquals_NeNotAllChangesEq() throws Exception {
		TradeReport other = new TradeReport();
		other.addChange(new PositionChange(1, 2, 3d));
		other.addChange(new PositionChange(2, 2, 3d));
		trade.addChange(new PositionChange(1, 2, 3d));
		trade.addChange(new PositionChange(2, 2, 3d, "foobar"));
		assertFalse(trade.equals(other));
	}
	
	@Test
	public void testEquals_Eq() throws Exception {
		TradeReport other = new TradeReport();
		other.addChange(new PositionChange(1, 2, 3d));
		other.addChange(new PositionChange(2, 2, 3d, "foobar"));
		trade.addChange(new PositionChange(1, 2, 3d));
		trade.addChange(new PositionChange(2, 2, 3d, "foobar"));
		assertTrue(trade.equals(other));
	}

}
