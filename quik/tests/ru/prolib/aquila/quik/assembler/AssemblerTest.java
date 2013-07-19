package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.quik.QUIKEditableTerminal;

public class AssemblerTest {
	private IMocksControl control;
	private QUIKEditableTerminal terminal;
	private Assembler assembler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKEditableTerminal.class);
		assembler = new Assembler(terminal);
	}
	
	@Test
	public void testStart() throws Exception {
		control.replay();
		
		assembler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		control.replay();

		assembler.stop();
		
		control.verify();
	}
	
	@Test
	public void testX_() throws Exception {
		fail("TODO: incomplete");
	}
	
}
