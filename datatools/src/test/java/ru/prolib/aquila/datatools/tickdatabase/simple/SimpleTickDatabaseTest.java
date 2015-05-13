package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;

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
	private DataWriterFactory factory;
	private DataWriter writer1, writer2, writer3;
	private Map<SecurityDescriptor, DataWriter> segments;
	private SimpleTickDatabase database;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		factory = control.createMock(DataWriterFactory.class);
		writer1 = control.createMock(DataWriter.class);
		writer2 = control.createMock(DataWriter.class);
		writer3 = control.createMock(DataWriter.class);
		segments = new LinkedHashMap<SecurityDescriptor, DataWriter>();
		database = new SimpleTickDatabase(factory, segments);
	}
	
	@Test
	public void testCtor1() throws Exception {
		database = new SimpleTickDatabase(factory);
		expect(factory.createWriter(descr1)).andReturn(writer1);
		writer1.write(tick1);
		writer1.write(tick2);
		control.replay();
		
		database.write(descr1, tick1);
		database.write(descr1, tick2);
		
		control.verify();
	}
	
	@Test
	public void testWrite_NewWriter() throws Exception {
		segments.put(descr1, writer1);
		segments.put(descr3, writer3);
		expect(factory.createWriter(descr2)).andReturn(writer2);
		writer2.write(tick1);
		control.replay();
		
		database.write(descr2, tick1);
		
		control.verify();
		assertSame(writer2, segments.get(descr2));
	}
	
	@Test
	public void testWrite_ExistingWriter() throws Exception {
		segments.put(descr1, writer1);
		writer1.write(tick1);
		control.replay();
		
		database.write(descr1, tick1);
		
		control.verify();
	}
	
	@Test (expected=GeneralException.class)
	public void testGetIterator_ShouldThrows() throws Exception {
		database.getIterator(descr1, new LocalDateTime());
	}

	@Test
	public void testClose() throws Exception {
		segments.put(descr1, writer1);
		segments.put(descr2, writer2);
		segments.put(descr3, writer3);
		writer1.close();
		writer2.close();
		writer3.close();
		control.replay();
		
		database.close();
		
		control.verify();
		assertEquals(0, segments.size());
	}

}
