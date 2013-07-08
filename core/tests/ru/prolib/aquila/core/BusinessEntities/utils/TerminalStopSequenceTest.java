package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.TerminalState;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalStopSequence;
import ru.prolib.aquila.core.utils.Variant;

public class TerminalStopSequenceTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private CountDownLatch started;
	private Starter starter;
	private TerminalStopSequence sequence;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		started = control.createMock(CountDownLatch.class);
		starter = control.createMock(Starter.class);
		sequence = new TerminalStopSequence(terminal, started);
		expect(terminal.getStarter()).andStubReturn(starter);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, sequence.getTerminal());
		assertSame(started, sequence.getStartedIndicator());
	}
	
	@Test
	public void testRun_Ok() throws Exception {
		started.countDown();
		terminal.fireTerminalDisconnectedEvent();
		terminal.fireTerminalStoppedEvent();
		starter.stop();
		terminal.setTerminalState(eq(TerminalState.STOPPED));
		control.replay();
		
		sequence.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_ExceptionFromStarter() throws Exception {
		started.countDown();
		terminal.fireTerminalDisconnectedEvent();
		terminal.fireTerminalStoppedEvent();
		starter.stop();
		expectLastCall().andThrow(new StarterException("Test exception"));
		terminal.setTerminalState(eq(TerminalState.STOPPED));
		control.replay();
		
		sequence.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(sequence.equals(sequence));
		assertFalse(sequence.equals(this));
		assertFalse(sequence.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(terminal)
			.add(control.createMock(EditableTerminal.class));
		Variant<CountDownLatch> vSig = new Variant<CountDownLatch>(vTerm)
			.add(started)
			.add(control.createMock(CountDownLatch.class));
		Variant<?> iterator = vSig;
		int foundCnt = 0;
		TerminalStopSequence x = null, found = null;
		do {
			x = new TerminalStopSequence(vTerm.get(), vSig.get());
			if ( sequence.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(started, found.getStartedIndicator());
	}

}
