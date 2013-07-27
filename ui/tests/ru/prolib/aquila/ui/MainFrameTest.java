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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.wrapper.Menu;
import ru.prolib.aquila.ui.wrapper.MenuBar;

public class MainFrameTest {
	private static IMocksControl control;
	
	private AquilaUI facade;
	private UiTexts texts = new UiTexts();
	private Runnable exitAction;
	private Terminal terminal;
	
	private MainFrame main;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		Properties labels = new Properties();
		labels.setProperty("MENU_FILE", "MENU_FILE");
		labels.setProperty("MENU_TERM", "MENU_TERM");
		labels.setProperty("MENU_FILE_EXIT", "MENU_FILE_EXIT");
		labels.setProperty("MENU_TERM_START", "MENU_TERM_START");
		labels.setProperty("MENU_TERM_STOP", "MENU_TERM_STOP");
		texts.setClassLabels("MenuBar",new ClassLabels("MenuBar", labels));
		texts.setClassLabels("PortfolioDataPanel",
				new ClassLabels("PortfolioDataPanel", new Properties()));
		texts.setClassLabels("StatusBar",
				new ClassLabels("StatusBar", new Properties()));
		
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
	public void testCreateUI() throws Exception {		
		
		EventType onStarted = control.createMock(EventType.class);
		EventType onStopped = control.createMock(EventType.class);
		expect(terminal.OnStarted()).andStubReturn(onStarted);
		expect(terminal.OnStopped()).andStubReturn(onStopped);
		
		onStarted.addListener(same(main));
		onStopped.addListener(same(main));
		main.setTerminal(terminal);
		
		control.replay();
		main.createUI(facade);
		control.verify();
		
		IsInstanceOf.instanceOf(CurrentPortfolioImpl.class).
			matches(main.getCurrPortfolio());
		IsInstanceOf.instanceOf(PortfolioDataPanel.class).
			matches(main.getContentPane().getComponent(0));
		assertEquals(main.getTabPanel(), main.getContentPane().getComponent(0));		
		assertEquals(main.getStatus(), main.getContentPane().getComponent(1));
		IsInstanceOf.instanceOf(StatusBar.class).matches(main.getStatus());
		IsInstanceOf.instanceOf(PortfolioDataPanel.class).
			matches(main.getStatus().getPortfolioPanel());
		
		MenuBar menu = main.getMainMenu();
		assertEquals(menu.getUnderlyingObject(), main.getJMenuBar());
		validateMenu(menu);
		
		WindowListener[] wListeners = main.getWindowListeners();
		assertEquals(1, wListeners.length);
		IsInstanceOf.instanceOf(WindowAdapter.class).matches(wListeners[0]);
	}
	
	@Test
	public void testStop_TerminalNotStarted() throws Exception {
		StatusBar status = control.createMock(StatusBar.class);
		main.setStatus(status);
		main.setTerminal(terminal);
		
		expect(terminal.started()).andReturn(false);
		status.stop();		
		control.replay();
		main.stop();
		control.verify();
		assertFalse(main.isDisplayable());
	}
	
	@Test
	public void testStop_TerminalStarted() throws Exception {
		StatusBar status = control.createMock(StatusBar.class);
		main.setStatus(status);
		main.setTerminal(terminal);
		
		expect(terminal.started()).andReturn(true);
		terminal.stop();
		status.stop();		
		control.replay();
		main.stop();
		control.verify();
		assertFalse(main.isDisplayable());
	}
	
	@Test
	public void testStart_TerminalNotStopped() throws Exception {
		StatusBar status = control.createMock(StatusBar.class);
		main.setStatus(status);
		main.setTerminal(terminal);
		
		status.start();
		expect(terminal.stopped()).andReturn(false);
		control.replay();
		main.start();
		control.verify();
		assertTrue(main.isVisible());
	}
	
	@Test
	public void testStart_TerminalStopped() throws Exception {
		StatusBar status = control.createMock(StatusBar.class);
		main.setStatus(status);
		main.setTerminal(terminal);
		
		status.start();
		expect(terminal.stopped()).andReturn(true);
		terminal.start();
		control.replay();
		main.start();
		control.verify();
		assertTrue(main.isVisible());
	}
	
	@Test
	public void testInitialize() {
		ServiceLocator locator = control.createMock(ServiceLocator.class);
		main.initialize(locator, terminal, null);
		assertEquals(terminal, main.getTerminal());
	}

	@Test
	public void testMenuLabels() {
		assertEquals("MenuBar", MainFrame.MENU_SECT);
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
