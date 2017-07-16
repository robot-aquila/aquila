package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;

/**
 * $Id$
 */
public class MenuTest {
	
	private static IMocksControl control;
	private EventSystem eventSystem;
	private EventQueue queue;
	
	private JMenu underlying;
	private Menu menu;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		
		underlying = new JMenu();
		menu = new Menu(underlying, eventSystem);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.wrapper.Menu#addItem(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testAddItem_StringString() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		
		menu.addBottomSeparator();
		final MenuItem item = menu.addItem("Foo", "Bar");
		JMenuItem udr = item.getUnderlyingObject();
		assertEquals("Bar", udr.getText());
		assertEquals(udr, underlying.getItem(0));
		assertTrue(menu.isItemExists("Foo"));
		
		item.OnCommand().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				assertTrue(e.isType(item.OnCommand()));
				finished.countDown();
			}
			
		});
		ActionListener[] a = udr.getActionListeners();
		assertEquals(1, a.length);
		a[0].actionPerformed(control.createMock(ActionEvent.class));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.wrapper.Menu#addItem(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testAddItem_StringStringJMenuItem() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		
		menu.addBottomSeparator();
		final MenuItem item = menu.addItem("Foo", "Bar", new JRadioButtonMenuItem());
		JMenuItem udr = item.getUnderlyingObject();
		IsInstanceOf.instanceOf(JRadioButtonMenuItem.class).matches(udr);
		assertEquals("Bar", udr.getText());
		assertEquals(udr, underlying.getItem(0));
		assertTrue(menu.isItemExists("Foo"));
		
		item.OnCommand().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				assertTrue(e.isType(item.OnCommand()));
				finished.countDown();
			}
			
		});
		ActionListener[] a = udr.getActionListeners();
		assertEquals(1, a.length);
		a[0].actionPerformed(control.createMock(ActionEvent.class));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.wrapper.Menu#addBottomItem(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testAddBottomItemStringString() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final MenuItem item = menu.addBottomItem("Foo", "Bar");
		menu.addSeparator();
		
		JMenuItem udr = item.getUnderlyingObject();
		assertEquals("Bar", udr.getText());
		assertEquals(udr, underlying.getItem(1));
		assertTrue(menu.isItemExists("Foo"));
		
		item.OnCommand().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				assertTrue(e.isType(item.OnCommand()));
				finished.countDown();
			}
			
		});
		ActionListener[] a = udr.getActionListeners();
		assertEquals(1, a.length);
		a[0].actionPerformed(control.createMock(ActionEvent.class));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

	/**
	 * Test method for {@link ru.prolib.aquila.ui.wrapper.Menu#addBottomItem(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testAddBottomItemStringStringString() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final MenuItem item = menu.addBottomItem("Foo", "Bar", new JRadioButtonMenuItem());
		menu.addSeparator();
		
		JMenuItem udr = item.getUnderlyingObject();
		IsInstanceOf.instanceOf(JRadioButtonMenuItem.class).matches(udr);
		assertEquals("Bar", udr.getText());
		assertEquals(udr, underlying.getItem(1));
		assertTrue(menu.isItemExists("Foo"));
		
		item.OnCommand().addListener(new EventListener() {

			@Override
			public void onEvent(Event e) {
				assertTrue(e.isType(item.OnCommand()));
				finished.countDown();
			}
			
		});
		ActionListener[] a = udr.getActionListeners();
		assertEquals(1, a.length);
		a[0].actionPerformed(control.createMock(ActionEvent.class));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

}
