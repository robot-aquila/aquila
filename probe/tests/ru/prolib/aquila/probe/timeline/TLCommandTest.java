package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;
import org.joda.time.*;
import org.junit.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.probe.timeline.TLCommand;

public class TLCommandTest {
	private static DateTime time = new DateTime(2014, 2, 12, 15, 4, 31, 0);
	private TLCommand command;

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testConstants() throws Exception {
		assertNotNull(TLCommand.FINISH);
		assertNotNull(TLCommand.PAUSE);
	}
	
	@Test
	public void testConstruct1Time() throws Exception {
		command = new TLCommand(time);
		assertEquals(time, command.getTime());
	}
	
	@Test
	public void testEquals() throws Exception {
		command = new TLCommand(time);
		Variant<DateTime> vTime = new Variant<DateTime>()
			.add(time)
			.add(new DateTime(2005, 1, 1, 10, 15, 30, 1))
			.add(null);
		Variant<?> iterator = vTime;
		int foundCnt = 0;
		TLCommand x, found = null;
		do {
			x = new TLCommand(vTime.get());
			if ( command.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(time, found.getTime());
	}
	
	@Test
	public void testIsRun() throws Exception {
		assertTrue(new TLCommand().isRun());
		assertTrue(new TLCommand(time).isRun());
		assertFalse(TLCommand.FINISH.isRun());
		assertFalse(TLCommand.PAUSE.isRun());
	}
	
	@Test
	public void testIsRunContinuously() throws Exception {
		assertTrue(new TLCommand().isRunContinuosly());
		assertFalse(new TLCommand(time).isRunContinuosly());
		assertFalse(TLCommand.FINISH.isRunContinuosly());
		assertFalse(TLCommand.PAUSE.isRunContinuosly());
	}
	
	@Test
	public void testIsApplicableForPOA() throws Exception {
		assertTrue(TLCommand.FINISH.isApplicableTo(time));
		assertTrue(TLCommand.PAUSE.isApplicableTo(time));
		assertTrue(new TLCommand().isApplicableTo(time));
		assertTrue(new TLCommand(time.minus(1)).isApplicableTo(time));
		assertFalse(new TLCommand(time).isApplicableTo(time));
		assertFalse(new TLCommand(time.plus(1)).isApplicableTo(time));
	}

}
