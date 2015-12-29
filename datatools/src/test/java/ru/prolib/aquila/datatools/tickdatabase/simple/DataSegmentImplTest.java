package ru.prolib.aquila.datatools.tickdatabase.simple;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

public class DataSegmentImplTest {
	private static final Symbol symbol;
	
	static {
		symbol = new Symbol("SBRF", "EQBR", "RUR", SymbolType.STOCK);
	}
	
	private IMocksControl control;
	private TickWriter writer;
	private DataSegmentImpl segment;
	private LocalDate date1;
	private Tick tick1, tick2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		date1 = LocalDate.of(2015, 5, 12);
		writer = control.createMock(TickWriter.class);
		segment = new DataSegmentImpl(symbol, date1, writer,
				LocalTime.of(15, 0, 0), 5);
	}
	
	@Test
	public void testCtor3() throws Exception {
		segment = new DataSegmentImpl(symbol, date1, writer);
		assertEquals(symbol, segment.getSymbol());
		assertEquals(date1, segment.getDate());
		assertEquals(writer, segment.getWriter());
		assertEquals(LocalTime.of(0, 0, 0), segment.getTimeOfLastTick());
		assertEquals(0, segment.getNumberOfLastTick());
	}
	
	@Test
	public void testCtor5() throws Exception {
		assertEquals(symbol, segment.getSymbol());
		assertEquals(date1, segment.getDate());
		assertEquals(writer, segment.getWriter());
		assertEquals(LocalTime.of(15, 0, 0), segment.getTimeOfLastTick());
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
		tick1 = new Tick(LocalDateTime.of(2015, 5, 12, 15, 0, 0), 129.0d, 10d);
		tick2 = new Tick(LocalDateTime.of(2015, 5, 12, 15, 2, 5), 122.0d, 20d);
		writer.write(tick1);
		writer.write(tick2);
		control.replay();
		
		segment.write(tick1);
		assertEquals(LocalTime.of(15, 0, 0), segment.getTimeOfLastTick());
		assertEquals(6, segment.getNumberOfLastTick());
		segment.write(tick2);
		assertEquals(LocalTime.of(15, 2, 5), segment.getTimeOfLastTick());
		assertEquals(7, segment.getNumberOfLastTick());
		
		control.verify();
	}
	
	@Test (expected=IOException.class)
	public void testWrite_ThrowsIfDateMismatch() throws Exception {
		tick1 = new Tick(LocalDateTime.of(2015, 5, 11, 23, 59, 59, 999), 1d, 1d); 
		segment.write(tick1);
	}
	
	@Test (expected=IOException.class)
	public void testWrite_ThrowsIfTimeLessThanTheLastTime() throws Exception {
		tick1 = new Tick(LocalDateTime.of(2015, 5, 12, 14, 59, 59, 999), 1d, 1d);
		segment.write(tick1);
	}
	
	@Test
	public void testFlush() throws Exception {
		writer.flush();
		control.replay();
		
		segment.flush();
		
		control.verify();
	}

}
