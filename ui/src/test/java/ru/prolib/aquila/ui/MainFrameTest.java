package ru.prolib.aquila.ui;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.util.Properties;

import javax.swing.JTabbedPane;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.ini4j.Options;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.ui.msg.CommonMsg;
import ru.prolib.aquila.ui.wrapper.Menu;
import ru.prolib.aquila.ui.wrapper.MenuBar;

public class MainFrameTest {
	private static IMocksControl control;
	
	private ServiceLocator facade;
	private Messages texts = new Messages();
	private Runnable exitAction;
	private Terminal terminal;
	
	private MainFrame main;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		Options labels = new Options();
		labels.put("MENU_FILE", "MENU_FILE");
		labels.put("MENU_TERM", "MENU_TERM");
		labels.put("MENU_FILE_EXIT", "MENU_FILE_EXIT");
		labels.put("MENU_TERM_START", "MENU_TERM_START");
		labels.put("MENU_TERM_STOP", "MENU_TERM_STOP");
		texts.set(CommonMsg.SECTION_ID, labels);
		texts.set("PortfolioDataPanel", new Options());
		
		terminal = control.createMock(Terminal.class);		
		exitAction = control.createMock(Runnable.class);
		facade = new ServiceLocator(texts, exitAction);
		main = new MainFrame();
	}

	@After
	public void tearDown() throws Exception {
		main.dispose();
		main = null;
	}
	
	@Test
	@Ignore
	public void testCreateUI() throws Exception {		
		fail("TODO: ");
		EventType onStarted = control.createMock(EventType.class);
		EventType onStopped = control.createMock(EventType.class);
		expect(terminal.onTerminalReady()).andStubReturn(onStarted);
		expect(terminal.onTerminalUnready()).andStubReturn(onStopped);
		
		onStarted.addListener(same(main));
		onStopped.addListener(same(main));
		main.initialize(facade, terminal, null);
		
		control.replay();
		main.createUI(facade);
		control.verify();
		
		IsInstanceOf.instanceOf(CurrentPortfolioImpl.class).
			matches(main.getCurrPortfolio());
		IsInstanceOf.instanceOf(PortfolioStatusBar.class).
			matches(main.getContentPane().getComponent(0));
		assertEquals(main.getTabPanel(), main.getContentPane().getComponent(0));		
		//assertEquals(main.getStatus(), main.getContentPane().getComponent(1));
		//IsInstanceOf.instanceOf(TerminalStatusBar.class).matches(main.getStatus());
		//IsInstanceOf.instanceOf(PortfolioDataPanel.class).
		//	matches(main.getStatus().getPortfolioPanel());
		
		MenuBar menu = main.getMainMenu();
		assertEquals(menu.getUnderlyingObject(), main.getJMenuBar());
		validateMenu(menu);
		
		WindowListener[] wListeners = main.getWindowListeners();
		assertEquals(1, wListeners.length);
		IsInstanceOf.instanceOf(WindowAdapter.class).matches(wListeners[0]);
	}
	
	@Test
	@Ignore
	public void testStop_TerminalNotStarted() throws Exception {
		fail("TODO: ");
		TerminalStatusBar status = control.createMock(TerminalStatusBar.class);
		//main.setStatus(status);
		//main.setTerminal(terminal);
		
		expect(terminal.isStarted()).andReturn(false);
		//status.stop();		
		control.replay();
		main.stop();
		control.verify();
		assertFalse(main.isDisplayable());
	}
	
	@Test
	@Ignore
	public void testStop_TerminalStarted() throws Exception {
		fail("TODO: ");
		TerminalStatusBar status = control.createMock(TerminalStatusBar.class);
		//main.setStatus(status);
		//main.setTerminal(terminal);
		
		expect(terminal.isStarted()).andReturn(true);
		terminal.stop();
		//status.stop();		
		control.replay();
		main.stop();
		control.verify();
		assertFalse(main.isDisplayable());
	}
	
	@Test
	@Ignore
	public void testStart_TerminalNotStopped() throws Exception {
		fail("TODO: ");
		TerminalStatusBar status = control.createMock(TerminalStatusBar.class);
		//main.setStatus(status);
		//main.setTerminal(terminal);
		
		//status.start();
		expect(terminal.isStarted()).andReturn(true);
		control.replay();
		main.start();
		control.verify();
		assertTrue(main.isVisible());
	}
	
	@Test
	@Ignore
	public void testStart_TerminalStopped() throws Exception {
		fail("TODO: ");
		TerminalStatusBar status = control.createMock(TerminalStatusBar.class);
		//main.setStatus(status);
		//main.setTerminal(terminal);
		
		//status.start();
		expect(terminal.isStarted()).andReturn(false);
		terminal.start();
		control.replay();
		main.start();
		control.verify();
		assertTrue(main.isVisible());
	}

	@Test
	public void testMenuLabels() {
		assertEquals("MENU_FILE", MainFrame.MENU_FILE);
		assertEquals("MENU_TERM", MainFrame.MENU_TERM);
		assertEquals("MENU_FILE_EXIT", MainFrame.MENU_FILE_EXIT);
		assertEquals("MENU_TERM_START", MainFrame.MENU_TERM_START);
		assertEquals("MENU_TERM_STOP", MainFrame.MENU_TERM_STOP);
		IsInstanceOf.instanceOf(JTabbedPane.class).matches(main.getTabPanel());
	}
	
	private void validateMenu(MenuBar menu) throws Exception {
		assertTrue(menu.isMenuExists("MENU_FILE"));
		Menu m = menu.getMenu("MENU_FILE");
		assertTrue(m.isItemExists("MENU_FILE_EXIT"));
		assertTrue(m.getItem("MENU_FILE_EXIT").OnCommand().isListener(main));
		
		assertTrue(menu.isMenuExists("MENU_TERM"));
		m = menu.getMenu("MENU_TERM");
		assertTrue(m.isItemExists("MENU_TERM_START"));
		assertTrue(m.getItem("MENU_TERM_START").OnCommand().isListener(main));
		assertTrue(m.isItemExists("MENU_TERM_STOP"));
		assertTrue(m.getItem("MENU_TERM_STOP").OnCommand().isListener(main));
		
		m = menu.getMenu("MENU_VIEW");
		assertTrue(m.isItemExists("MENU_VIEW_PORTFOLIO_STATUS"));
	}

}
