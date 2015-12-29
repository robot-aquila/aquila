package ru.prolib.aquila.probe.timeline;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.*;

import ru.prolib.aquila.probe.timeline.TLCmd;

public class TLCmdTest {
	private static LocalDateTime time = LocalDateTime.of(2014, 2, 12, 15, 4, 31, 0);
	private TLCmd cmd1,cmd2,cmd3;

	@Before
	public void setUp() throws Exception {
		cmd1 = new TLCmd(time);
		cmd2 = new TLCmd(TLCmdType.FINISH);
		cmd3 = null;
	}
	
	@Test
	public void testConstruct1_Time() throws Exception {
		assertEquals(time, cmd1.getTime());
		assertEquals(TLCmdType.RUN, cmd1.getType());
	}
	
	@Test
	public void testConstruct1_Type() throws Exception {
		assertNull(cmd2.getTime());
		assertEquals(TLCmdType.FINISH, cmd2.getType());
	}
	
	@Test
	public void testIsType() throws Exception {
		assertFalse(cmd1.isType(TLCmdType.FINISH));
		assertTrue(cmd1.isType(TLCmdType.RUN));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cmd1.equals(cmd1));
		assertFalse(cmd1.equals(null));
		assertFalse(cmd1.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		cmd3 = new TLCmd(LocalDateTime.of(2014, 2, 12, 15, 4, 31, 0));
		assertTrue(cmd1.equals(cmd3));
		cmd3 = new TLCmd(LocalDateTime.of(2022, 2, 12, 15, 4, 31, 0));
		assertFalse(cmd1.equals(cmd3));

		assertFalse(cmd2.equals(cmd3));
		assertFalse(cmd2.equals(new TLCmd(TLCmdType.PAUSE)));
		assertTrue(cmd2.equals(new TLCmd(TLCmdType.FINISH)));
	}
	
	@Test
	public void testConstants() throws Exception {
		assertEquals(new TLCmd(TLCmdType.FINISH), TLCmd.FINISH);
		assertEquals(new TLCmd(TLCmdType.PAUSE), TLCmd.PAUSE);
	}

}
