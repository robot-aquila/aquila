package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

public class DataSegmentWriterImplTest {
	private static final SecurityDescriptor descr;
	
	static {
		descr = new SecurityDescriptor("SBRF", "EQBR", "RUR", SecurityType.STK);
	}
	
	private IMocksControl control;
	private TickWriter writer;
	private DataSegmentWriterImpl segment;
	private LocalDate date1;
	private Tick tick1, tick2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		date1 = new LocalDate(2015, 5, 12);
		writer = control.createMock(TickWriter.class);
		segment = new DataSegmentWriterImpl(descr, date1, writer,
				new LocalTime(15, 0, 0), 5);
	}
	
	@Test
	public void testCtor3() throws Exception {
		segment = new DataSegmentWriterImpl(descr, date1, writer);
		assertEquals(descr, segment.getSecurityDescriptor());
		assertEquals(date1, segment.getDate());
		assertEquals(writer, segment.getWriter());
		assertEquals(new LocalTime(0, 0, 0), segment.getTimeOfLastTick());
		assertEquals(0, segment.getNumberOfLastTick());
	}
	
	@Test
	public void testCtor5() throws Exception {
		assertEquals(descr, segment.getSecurityDescriptor());
		assertEquals(date1, segment.getDate());
		assertEquals(writer, segment.getWriter());
		assertEquals(new LocalTime(15, 0, 0), segment.getTimeOfLastTick());
		assertEquals(5, segment.getNumberOfLastTick());
	}
	
	@Test
	public void testClose() throws Exception {
		writer.close();
		control.replay();
		
		segment.close();
		
		control.verify();
	}
	
	@Test
	public void testWrite_Ok() throws Exception {
		tick1 = new Tick(new DateTime(2015, 5, 12, 15, 0, 0), 129.0d, 10d);
		tick2 = new Tick(new DateTime(2015, 5, 12, 15, 2, 5), 122.0d, 20d);
		writer.write(tick1);
		writer.write(tick2);
		control.replay();
		
		segment.write(tick1);
		assertEquals(new LocalTime(15, 0, 0), segment.getTimeOfLastTick());
		assertEquals(6, segment.getNumberOfLastTick());
		segment.write(tick2);
		assertEquals(new LocalTime(15, 2, 5), segment.getTimeOfLastTick());
		assertEquals(7, segment.getNumberOfLastTick());
		
		control.verify();
	}
	
	@Test (expected=GeneralException.class)
	public void testWrite_ThrowsIfDateMismatch() throws Exception {
		tick1 = new Tick(new DateTime(2015, 5, 11, 23, 59, 59, 999), 1d, 1d); 
		segment.write(tick1);
	}
	
	@Test (expected=GeneralException.class)
	public void testWrite_ThrowsIfTimeLessThanTheLastTime() throws Exception {
		tick1 = new Tick(new DateTime(2015, 5, 12, 14, 59, 59, 999), 1d, 1d);
		segment.write(tick1);
	}

}
