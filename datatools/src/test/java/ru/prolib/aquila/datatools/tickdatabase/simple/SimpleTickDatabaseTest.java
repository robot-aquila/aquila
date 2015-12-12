package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.SimpleIterator;
import ru.prolib.aquila.core.data.Tick;

public class SimpleTickDatabaseTest {
	private static final Symbol symbol1, symbol2, symbol3;
	private static final Tick tick1, tick2;
	
	static {
		symbol1 = new Symbol("SBRF", "EQBR", "RUR");
		symbol2 = new Symbol("GAZP", "EQBR", "RUR");
		symbol3 = new Symbol("LKOH", "EQBR", "RUR");
		tick1 = new Tick(new DateTime(2015, 5, 12, 0, 0, 0, 0), 100d, 10);
		tick2 = new Tick(new DateTime(2015, 5, 12, 9, 0, 0, 0), 105d, 15);
	}
	
	private IMocksControl control;
	private DataSegmentManager manager;
	private DataSegment segment1, segment2, segment3;
	private Map<Symbol, DataSegment> segments;
	private SimpleTickDatabase database;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		manager = control.createMock(DataSegmentManager.class);
		segment1 = control.createMock(DataSegment.class);
		segment2 = control.createMock(DataSegment.class);
		segment3 = control.createMock(DataSegment.class);
		segments = new LinkedHashMap<Symbol, DataSegment>();
		database = new SimpleTickDatabase(manager, segments);
	}
	
	@Test
	public void testCtor1() throws Exception {
		LocalDate date = new LocalDate(2015, 5, 12);
		database = new SimpleTickDatabase(manager);
		expect(manager.openSegment(symbol1, date)).andReturn(segment1);
		expect(segment1.getDate()).andStubReturn(date);
		segment1.write(tick1);
		segment1.write(tick2);
		control.replay();
		
		database.write(symbol1, tick1);
		database.write(symbol1, tick2);
		
		control.verify();
	}
	
	@Test
	public void testWrite_NewWriter() throws Exception {
		LocalDate date = new LocalDate(2015, 5, 12);
		segments.put(symbol1, segment1);
		segments.put(symbol3, segment3);
		expect(manager.openSegment(symbol2, date)).andReturn(segment2);
		segment2.write(tick1);
		control.replay();
		
		database.write(symbol2, tick1);
		
		control.verify();
		assertSame(segment2, segments.get(symbol2));
	}
	
	@Test
	public void testWrite_ExistingWriter() throws Exception {
		LocalDate date = new LocalDate(2015, 5, 12);
		expect(segment1.getDate()).andStubReturn(date);
		segments.put(symbol1, segment1);
		segment1.write(tick1);
		control.replay();
		
		database.write(symbol1, tick1);
		
		control.verify();
	}
	
	@Test
	public void testGetTicksSD_IfDataNotAvailable() throws Exception {
		DateTime time = new DateTime(2015, 7, 8, 0, 0, 0);
		expect(manager.isDataAvailable(symbol1)).andReturn(false);
		control.replay();
		
		SimpleIterator<Tick> it =
				(SimpleIterator<Tick>) database.getTicks(symbol1, time);
		
		assertNotNull(it);
		assertFalse(it.next()); // empty iterator
		control.verify();
	}
	
	@Test
	public void testGetTicksSD_IfDataAvailable() throws Exception {
		DateTime time = new DateTime(2015, 7, 8, 0, 0, 0);
		expect(manager.isDataAvailable(symbol1)).andReturn(true);
		control.replay();
		
		SeamlessTickReader it =
				(SeamlessTickReader) database.getTicks(symbol1, time);
		
		assertNotNull(it);
		assertEquals(time, it.getCurrentTime());
		assertEquals(symbol1, it.getSymbol());
		assertSame(manager, it.getDataSegmentManager());
		control.verify();
	}

	@Test
	public void testClose() throws Exception {
		segments.put(symbol1, segment1);
		segments.put(symbol2, segment2);
		segments.put(symbol3, segment3);
		manager.closeSegment(segment1);
		manager.closeSegment(segment2);
		manager.closeSegment(segment3);
		control.replay();
		
		database.close();
		
		control.verify();
		assertEquals(0, segments.size());
	}
	
	@Test
	public void testSendMarker() throws Exception {
		segments.put(symbol1, segment1);
		segments.put(symbol2, segment2);
		segments.put(symbol3, segment3);
		LocalDate base = new LocalDate(2015, 6, 1);
		expect(segment1.getDate()).andStubReturn(base);
		expect(segment2.getDate()).andStubReturn(base.plusDays(1));
		expect(segment3.getDate()).andStubReturn(base.minusDays(1));
		manager.closeSegment(segment1);
		manager.closeSegment(segment3);
		control.replay();
		
		database.sendMarker(new DateTime(2015, 6, 1, 0, 0, 0));
		
		control.verify();
		assertFalse(segments.containsKey(symbol1));
		assertTrue(segments.containsKey(symbol2));
		assertFalse(segments.containsKey(symbol3));
	}
	
	@Test
	public void testGetTicksSI_IfDataNotAvailable() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		expect(manager.getSegmentList(symbol1)).andReturn(list);
		control.replay();
		
		SimpleIterator<Tick> it =
				(SimpleIterator<Tick>) database.getTicks(symbol1, 5);
		
		assertNotNull(it);
		assertFalse(it.next()); // empty iterator
		
		control.verify();
	}

	@Test
	public void testGetTicksSI_IfNoEnoughSegments() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(1998, 6, 1));
		list.add(new LocalDate(1998, 7, 1));
		list.add(new LocalDate(1998, 8, 1));
		expect(manager.getSegmentList(symbol1)).andReturn(list);
		control.replay();
		
		SeamlessTickReader it =
				(SeamlessTickReader) database.getTicks(symbol1, 5);
		
		assertNotNull(it);
		assertEquals(new DateTime(1998, 6, 1, 0, 0, 0), it.getCurrentTime());
		assertEquals(symbol1, it.getSymbol());
		assertSame(manager, it.getDataSegmentManager());
		control.verify();
	}
	
	@Test
	public void testGetTicksSI_OK() throws Exception {
		List<LocalDate> list = new Vector<LocalDate>();
		list.add(new LocalDate(1998, 1, 1));
		list.add(new LocalDate(1998, 2, 1));
		list.add(new LocalDate(1998, 3, 1));
		list.add(new LocalDate(1998, 4, 1));
		list.add(new LocalDate(1998, 5, 1));
		list.add(new LocalDate(1998, 6, 1));
		list.add(new LocalDate(1998, 7, 1));
		list.add(new LocalDate(1998, 8, 1));
		expect(manager.getSegmentList(symbol1)).andReturn(list);
		control.replay();
		
		SeamlessTickReader it =
				(SeamlessTickReader) database.getTicks(symbol1, 5);
		
		assertNotNull(it);
		assertEquals(new DateTime(1998, 4, 1, 0, 0, 0), it.getCurrentTime());
		assertEquals(symbol1, it.getSymbol());
		assertSame(manager, it.getDataSegmentManager());
		control.verify();
	}

}
