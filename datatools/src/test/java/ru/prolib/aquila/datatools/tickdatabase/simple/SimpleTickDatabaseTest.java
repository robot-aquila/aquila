package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Tick;

public class SimpleTickDatabaseTest {
	private static final SecurityDescriptor descr1, descr2, descr3;
	private static final Tick tick1, tick2;
	
	static {
		descr1 = new SecurityDescriptor("SBRF", "EQBR", "RUR");
		descr2 = new SecurityDescriptor("GAZP", "EQBR", "RUR");
		descr3 = new SecurityDescriptor("LKOH", "EQBR", "RUR");
		tick1 = new Tick(new DateTime(2015, 5, 12, 0, 0, 0, 0), 100d, 10);
		tick2 = new Tick(new DateTime(2015, 5, 12, 9, 0, 0, 0), 105d, 15);
	}
	
	private IMocksControl control;
	private DataSegmentManager manager;
	private DataSegment segment1, segment2, segment3;
	private Map<SecurityDescriptor, DataSegment> segments;
	private SimpleTickDatabase database;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		manager = control.createMock(DataSegmentManager.class);
		segment1 = control.createMock(DataSegment.class);
		segment2 = control.createMock(DataSegment.class);
		segment3 = control.createMock(DataSegment.class);
		segments = new LinkedHashMap<SecurityDescriptor, DataSegment>();
		database = new SimpleTickDatabase(manager, segments);
	}
	
	@Test
	public void testCtor1() throws Exception {
		LocalDate date = new LocalDate(2015, 5, 12);
		database = new SimpleTickDatabase(manager);
		expect(manager.openSegment(descr1, date)).andReturn(segment1);
		expect(segment1.getDate()).andStubReturn(date);
		segment1.write(tick1);
		segment1.write(tick2);
		control.replay();
		
		database.write(descr1, tick1);
		database.write(descr1, tick2);
		
		control.verify();
	}
	
	@Test
	public void testWrite_NewWriter() throws Exception {
		LocalDate date = new LocalDate(2015, 5, 12);
		segments.put(descr1, segment1);
		segments.put(descr3, segment3);
		expect(manager.openSegment(descr2, date)).andReturn(segment2);
		segment2.write(tick1);
		control.replay();
		
		database.write(descr2, tick1);
		
		control.verify();
		assertSame(segment2, segments.get(descr2));
	}
	
	@Test
	public void testWrite_ExistingWriter() throws Exception {
		LocalDate date = new LocalDate(2015, 5, 12);
		expect(segment1.getDate()).andStubReturn(date);
		segments.put(descr1, segment1);
		segment1.write(tick1);
		control.replay();
		
		database.write(descr1, tick1);
		
		control.verify();
	}
	
	@Test (expected=IOException.class)
	public void testGetIterator_ShouldThrows() throws Exception {
		database.getIterator(descr1, new DateTime());
	}

	@Test
	public void testClose() throws Exception {
		segments.put(descr1, segment1);
		segments.put(descr2, segment2);
		segments.put(descr3, segment3);
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
		segments.put(descr1, segment1);
		segments.put(descr2, segment2);
		segments.put(descr3, segment3);
		LocalDate base = new LocalDate(2015, 6, 1);
		expect(segment1.getDate()).andStubReturn(base);
		expect(segment2.getDate()).andStubReturn(base.plusDays(1));
		expect(segment3.getDate()).andStubReturn(base.minusDays(1));
		manager.closeSegment(segment1);
		manager.closeSegment(segment3);
		control.replay();
		
		database.sendMarker(new DateTime(2015, 6, 1, 0, 0, 0));
		
		control.verify();
		assertFalse(segments.containsKey(descr1));
		assertTrue(segments.containsKey(descr2));
		assertFalse(segments.containsKey(descr3));
	}

}
