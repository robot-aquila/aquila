package ru.prolib.aquila.ui;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;

import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class StatusBarTest {
	private static IMocksControl control;
	
	private UiTexts uiText = new UiTexts();
	private Terminal terminal;
	
	private EventType onConn, onDisconn, onStarted, onStopped; 
	
	private StatusBar bar;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		Properties labels = new Properties();
		labels.setProperty("LABEL_DISCONNECTED","disconnected");
		labels.setProperty("LABEL_CONNECTED","connected");
		labels.setProperty("LABEL_STARTED","started");
		labels.setProperty("LABEL_STOPPED","stopped");
		uiText.setClassLabels("StatusBar",new ClassLabels("Status", labels));
		
		terminal = control.createMock(Terminal.class);
		bar = new StatusBar(terminal, uiText);
		
		onConn = control.createMock(EventType.class);
		onDisconn = control.createMock(EventType.class);
		onStarted = control.createMock(EventType.class);
		onStopped = control.createMock(EventType.class);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testOnEvent_OnConnected() {
		Event event = new EventImpl(onConn);
		expect(terminal.OnConnected()).andStubReturn(onConn);
		control.replay();
		bar.onEvent(event);
		control.verify();
		assertEquals(bar.getTConn().getIcon(), bar.getGreenIcon());
		assertEquals("connected", bar.getTConn().getToolTipText());
	}
	
	@Test
	public void testOnEvent_OnStarted() {
		Event event = new EventImpl(onStarted);
		expect(terminal.OnConnected()).andStubReturn(onConn);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconn);
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		control.replay();
		bar.onEvent(event);
		control.verify();
		assertEquals(bar.getTStart().getIcon(), bar.getGreenIcon());
		assertEquals("started", bar.getTStart().getToolTipText());
	}
	
	@Test
	public void testOnEvent_OnStopped() {
		bar.getTStart().setIcon(bar.getGreenIcon());
		bar.getTStart().setToolTipText("started");
		
		Event event = new EventImpl(onStopped);
		expect(terminal.OnConnected()).andStubReturn(onConn);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconn);
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		expect(terminal.OnStopped()).andStubReturn(onStopped);
		control.replay();
		bar.onEvent(event);
		control.verify();
		assertEquals(bar.getTStart().getIcon(), bar.getRedIcon());
		assertEquals("stopped", bar.getTStart().getToolTipText());
	}
	
	@Test
	public void testOnEvent_OnDisconnected() {
		bar.getTConn().setIcon(bar.getGreenIcon());
		bar.getTConn().setToolTipText("connected");
		
		Event event = new EventImpl(onDisconn);
		expect(terminal.OnConnected()).andStubReturn(onConn);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconn);
		control.replay();
		bar.onEvent(event);
		control.verify();
		assertEquals(bar.getTConn().getIcon(), bar.getRedIcon());
		assertEquals("disconnected", bar.getTConn().getToolTipText());
	}
	
	@Test
	public void testStop() throws Exception {
		expect(terminal.OnConnected()).andStubReturn(onConn);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconn);
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		expect(terminal.OnStopped()).andStubReturn(onStopped);
		
		onConn.removeListener(same(bar));
		onDisconn.removeListener(same(bar));
		onStarted.removeListener(same(bar));
		onStopped.removeListener(same(bar));
		
		control.replay();
		bar.stop();
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		expect(terminal.OnConnected()).andStubReturn(onConn);
		expect(terminal.OnDisconnected()).andStubReturn(onDisconn);
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		expect(terminal.OnStopped()).andStubReturn(onStopped);
		
		onConn.addListener(same(bar));
		onDisconn.addListener(same(bar));
		onStarted.addListener(same(bar));
		onStopped.addListener(same(bar));
		
		control.replay();
		bar.start();
		control.verify();
		
	}
	@Test
	public void testConstructor() {
		assertEquals(terminal, bar.getTerminal());
		assertEquals(uiText.get("StatusBar"), bar.getUiLabels());
		IsInstanceOf.instanceOf(EmptyBorder.class).matches(bar.getBorder());
		IsInstanceOf.instanceOf(JLabel.class).matches(bar.getTStart());
		IsInstanceOf.instanceOf(JLabel.class).matches(bar.getTConn());
		assertEquals(bar.getTStart(), bar.getComponent(0));
		assertEquals(bar.getTConn(), bar.getComponent(1));
		assertEquals(bar.getTConn().getIcon(), bar.getRedIcon());
		assertEquals("disconnected", bar.getTConn().getToolTipText());
		assertEquals(bar.getTStart().getIcon(), bar.getRedIcon());
		assertEquals("stopped", bar.getTStart().getToolTipText());
	}

}
