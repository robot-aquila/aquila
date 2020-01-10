package ru.prolib.aquila.utils.experimental.chart.swing;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.time.Instant;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.ObservableTSeriesImpl;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.TSeriesUpdate;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.JCompAutoScrollButton;
import ru.prolib.aquila.utils.experimental.chart.swing.ScrollBarControllerV2.OnLengthUpdateProxy;
import ru.prolib.aquila.utils.experimental.chart.swing.ScrollBarControllerV2.OnUpdateProxy;
import ru.prolib.aquila.utils.experimental.chart.swing.ScrollBarControllerV2.Viewport;
import ru.prolib.aquila.utils.experimental.chart.swing.ScrollBarControllerV2.ViewportTracker;

public class ScrollBarControllerV2Test {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private EventQueue queue;
	private IMocksControl control;
	private Timer timerMock;
	private JCompAutoScrollButton autoScrollButtonMock;
	private JScrollBar scrollBar;
	private JPanel rpanelMock;
	private ObservableTSeriesImpl<Integer> cats;
	private ScrollBarControllerV2 service, serviceMock;
	private ViewportTracker vptracker, vptrackerMock;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueFactory().createDefault();
		control = createStrictControl();
		timerMock = control.createMock(Timer.class);
		autoScrollButtonMock = control.createMock(JCompAutoScrollButton.class);
		rpanelMock = control.createMock(JPanel.class);
		cats = new ObservableTSeriesImpl<>(queue, new TSeriesImpl<>(ZTFrame.M5));
		scrollBar = new JScrollBar();
		vptracker = new ViewportTracker(true);
		vptrackerMock = control.createMock(ViewportTracker.class);
		service = new ScrollBarControllerV2(timerMock, false, vptrackerMock);
		serviceMock = control.createMock(ScrollBarControllerV2.class);
	}
	
	@After
	public void tearDown() throws Exception {
		queue.shutdown();
		control.resetToNice();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				service.close();
			}
		});
	}
	
	private void quietlySetRootPanel() {
		service.setRootPanel(rpanelMock);
	}
	
	private void quietlySetScrollBar() {
		service.setScrollBar(scrollBar);
	}
	
	private void quietlySetAutoScrollButton() {
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		autoScrollButtonMock.setAutoScroll(true);
		autoScrollButtonMock.addActionListener(service);
		control.replay();
		service.setAutoScrollButton(autoScrollButtonMock);
		control.resetToStrict();		
	}
	
	private void quietlySetAutoScroll(boolean auto_scroll) {
		scrollBar.setValues(9, 5, 0, 14);
		quietlySetScrollBar();
		quietlySetAutoScrollButton();
		expect(vptrackerMock.isAutoScroll()).andReturn(!auto_scroll);
		vptrackerMock.setAutoScroll(auto_scroll);
		expect(vptrackerMock.isAutoScroll()).andReturn(auto_scroll);
		autoScrollButtonMock.setAutoScroll(auto_scroll);
		expect(vptrackerMock.getViewport()).andReturn(new Viewport(14, 9, 5, 9, 5, !auto_scroll));
		control.replay();
		service.setAutoScroll(auto_scroll);
		//control.verify();
		control.resetToStrict();
	}
	
	private void quietlyEnableAutoScroll() {
		quietlySetAutoScroll(true);
	}
	
	private void quietlyDisableAutoScroll() {
		quietlySetAutoScroll(false);
	}
	
	private void quietlyEnableAutoRepaint() {
		timerMock.start();
		control.replay();
		service.setAutoRepaint(true);
		control.resetToStrict();
	}
	
	void quietlyDisableAutoRepaint() {
		if ( service.isAutoRepaintEnabled() ) {
			timerMock.stop();
		}
		control.replay();
		service.setAutoRepaint(false);
		control.resetToStrict();
	}
	
	private void quietlySetCategories() {
		service.setCategories(cats);
	}
	
	private void fillCategories(int number_elements) {
		Instant start_time = Instant.EPOCH;
		Random rnd = new Random(System.currentTimeMillis());
		for ( int i = 0; i < number_elements; i ++ ) {
			TSeriesUpdate update = cats.set(start_time, rnd.nextInt());
			start_time = update.getInterval().getEnd();
		}
		assertEquals(number_elements, cats.getLength());
	}
	
	/**
	 * Fill categories series with random values and set it for controller.
	 * <p>
	 * @param number_elements - number of elements to generate
	 */
	private void quietlySetCategories(int number_elements) {
		fillCategories(number_elements);
		quietlySetCategories();
	}
	
	@Test
	public void testViewport_Ctor6() {
		System.err.println("testViewport_Ctor6");
		Viewport viewport = new Viewport(200, 5, 50, 10, 45, true);
		
		assertEquals(200, viewport.getTotalLen());
		assertEquals(  5, viewport.getWindowPos());
		assertEquals( 50, viewport.getWindowLen());
		assertEquals( 10, viewport.getKnobPos());
		assertEquals( 45, viewport.getKnobLen());
		assertTrue(viewport.isKnobEnabled());
		
		viewport = new Viewport(200, 0, 200, 0, 200, false);
		
		assertEquals(200, viewport.getTotalLen());
		assertEquals(  0, viewport.getWindowPos());
		assertEquals(200, viewport.getWindowLen());
		assertEquals(  0, viewport.getKnobPos());
		assertEquals(200, viewport.getKnobLen());
		assertFalse(viewport.isKnobEnabled());
	}
	
	@Test
	public void testViewport_AllZeros() {
		System.err.println("testViewport_AllZeros");
		Viewport viewport = new Viewport(0, 0, 0, 0, 0, true);
		
		assertEquals(0, viewport.getTotalLen());
		assertEquals(0, viewport.getWindowPos());
		assertEquals(0, viewport.getWindowLen());
		assertEquals(0, viewport.getKnobPos());
		assertEquals(0, viewport.getKnobLen());
		assertTrue(viewport.isKnobEnabled());
	}
	
	@Test
	public void testViewport_Ctor6_AllowsWindowLenGtTotalLen() {
		System.err.println("testViewport_Ctor6_AllowsWindowLenGtTotalLen");
		Viewport viewport = new Viewport(200, 0, 201, 0, 45, true);
		
		assertEquals(200, viewport.getTotalLen());
		assertEquals(  0, viewport.getWindowPos());
		assertEquals(201, viewport.getWindowLen());
		assertEquals(  0, viewport.getKnobPos());
		assertEquals( 45, viewport.getKnobLen());
		assertTrue(viewport.isKnobEnabled());
	}
	
	@Test
	public void testViewport_Ctor6_ThrowsIfKnobLenGtWindowLen() {
		System.err.println("testViewport_Ctor6_ThrowsIfKnobLenGtWindowLen");
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Expected knob len <= 50 but 51");
		
		new Viewport(200, 0, 50, 0, 51, true);
	}
	
	@Test
	public void testViewport_Ctor6_AllowsWindowPosPlusLenGtTotalLen() {
		System.err.println("testViewport_Ctor6_AllowsWindowPosPlusLenGtTotalLen");
		Viewport viewport = new Viewport(200, 205, 50, 100, 45, true);
		
		assertEquals(200, viewport.getTotalLen());
		assertEquals(205, viewport.getWindowPos());
		assertEquals( 50, viewport.getWindowLen());
		assertEquals(100, viewport.getKnobPos());
		assertEquals( 45, viewport.getKnobLen());
		assertTrue(viewport.isKnobEnabled());
	}
	
	@Test
	public void testViewport_Ctor6_ThrowsIfKnobPosGtZero() {
		System.err.println("testViewport_Ctor6_ThrowsIfKnobPosGtZero");
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Expected knob pos >= 0 but -5");
		
		new Viewport(200, -5, 190, -5, 45, true);
	}
	
	@Test
	public void testViewport_Ctor6_ThrowsIfKnobPosPlusLenGtTotalLen() {
		System.err.println("testViewport_Ctor6_ThrowsIfKnobPosPlusLenGtTotalLen");
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Expected knob pos + len <= 150 but 151");
		
		new Viewport(150, -5, 150, 1, 150, true);
	}
	
	@Test
	public void testViewport_Equals() {
		System.err.println("testViewport_Equals");
		Viewport viewport = new Viewport(200, 0, 50, 5, 45, false);
		assertTrue(viewport.equals(viewport));
		assertFalse(viewport.equals(this));
		assertFalse(viewport.equals(null));
		Variant<Integer>
			vTL = new Variant<>(200, 500),
			vWP = new Variant<>(vTL,  0,  3),
			vWL = new Variant<>(vWP, 50, 55),
			vKP = new Variant<>(vWL,  5,  7),
			vKL = new Variant<>(vKP, 45, 47);
		Variant<Boolean> vKE = new Variant<>(vKL, false, true);
		Variant<?> iterator = vKE;
		int foundCnt = 0;
		Viewport x, found = null;
		do {
			x = new Viewport(vTL.get(), vWP.get(), vWL.get(), vKP.get(), vKL.get(), vKE.get());
			if ( viewport.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(200, found.getTotalLen());
		assertEquals(  0, found.getWindowPos());
		assertEquals( 50, found.getWindowLen());
		assertEquals(  5, found.getKnobPos());
		assertEquals( 45, found.getKnobLen());
		assertFalse(found.isKnobEnabled());
	}
	
	@Test
	public void testViewportTracker_WithAScroll() {
		System.err.println("testViewportTracker_WithAScroll");
		// Test with no additional limits
		// Case 1
		// autoscroll=true, preferred=null, visible=null
		//   ====== <- total
		//   |    | <- window
		//   [    ] <- knob
		//   unused <- visible
		vptracker.setAutoScroll(true);
		vptracker.setPreferredLen(null);
		vptracker.setVisibleLen(null);
		vptracker.setTotalLen(200);
		
		Viewport actual = vptracker.getViewport();
		
		Viewport expected = new Viewport(200, 0, 200, 0, 200, false);
		assertEquals(expected, actual);
		
		// Test with limit window len where window > total
		// Case 2
		// autoscroll=true, preferred=X, visible=null
		//   ====== <- total 
		// |      | <- window
		//   [    ] <- knob
		//   unused <- visible
		vptracker.setPreferredLen(220);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(200, -20, 220, 0, 200, false);
		assertEquals(expected, actual);
		
		// Test with limit window len where window < total
		// Case 3
		// autoscroll=true, preferred=X, visible=null
		//  ======= <- total
		//    |   | <- window
		//    [   ] <- knob
		//   unused <- visible
		vptracker.setTotalLen(300);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(300, 80, 220, 80, 220, false);
		assertEquals(expected, actual);
		
		// Test with limit window len and visible len where visible < window
		// Case 4
		// autoscroll=true, preferred=X, visible=Y
		//  ======= <- total
		//    |   | <- window
		//      [ ] <- knob
		//      vvv <- visible
		vptracker.setVisibleLen(80);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(300, 80, 220, 220, 80, false);
		assertEquals(expected, actual);
		
		// Test with limit window len and visible len where visible > window
		// Case 5
		// autoscroll=true, preferred=X, visible=Y
		//   ======= <- total
		//     |   | <- window
		//     [   ] <- knob
		//   vvvvvvv <- visible
		vptracker.setVisibleLen(440);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(300, 80, 220, 80, 220, false);
		assertEquals(expected, actual);
		
		// Test when tolal len reduced
		// Test where knob len still less than window len and visible len
		// Case 5
		// autoscroll=true, preferred=X, visible=Y
		//     ===== <- total
		//   |     | <- window
		//     [   ] <- knob
		//    vvvvvv <- visible
		vptracker.setTotalLen(120);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(120, -100, 220, 0, 120, false);
		assertEquals(expected, actual);
		
		// Test when visible len reduced
		// Case 5
		// autoscroll=true, preferred=X, visible=Y
		//     ===== <- total
		//   |     | <- window
		//      [  ] <- knob
		//      vvvv <- visible
		vptracker.setVisibleLen(100);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(120, -100, 220, 20, 100, false);
		assertEquals(expected, actual);
		
		// Test that knob pos change has no effect while autoscroll
		vptracker.setKnobPos(15);
		
		expected = new Viewport(120, -100, 220, 20, 100, false);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testViewportTracker_WoAScroll() {
		System.err.println("testViewportTracker_WoAScroll");
		// Without preferred len it works like with auto scroll
		// autoscroll=true, preferred=X, visible=Y
		//   ====== <- total
		//   |    | <- window
		//   [    ] <- knob
		//   unused <- visible
		vptracker.setAutoScroll(false);
		vptracker.setPreferredLen(null);
		vptracker.setVisibleLen(null);
		vptracker.setTotalLen(50);
		
		Viewport actual = vptracker.getViewport();
		
		Viewport expected = new Viewport(50, 0, 50, 0, 50, false);
		assertEquals(expected, actual);
		
		// Knob position change does not work while preferred len is not set
		vptracker.setKnobPos(10);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(50, 0, 50, 0, 50, false);
		assertEquals(expected, actual);
		
		vptracker.setKnobPos(-1);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(50, 0, 50, 0, 50, false);
		assertEquals(expected, actual);

		// Cases with preferred length
		// Case 2.1
		// autoscroll=false, preferred=X, visible=null
		//   ====== <- total
		// |      | <- window
		//   [    ] <- knob
		//   unused <- visible
		vptracker.setPreferredLen(100);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(50, -50, 100, 0, 50, false);
		assertEquals(expected, actual);
		
		// Case 2.2
		// autoscroll=false, preferred=X, visible=null
		//   ====== <- total
		//   |    | <- window
		//   [    ] <- knob
		//   unused <- visible
		vptracker.setTotalLen(100);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(100, 0, 100, 0, 100, false);
		assertEquals(expected, actual);
		
		// Case 2.3
		// autoscroll=false, preferred=X, visible=null
		// ======== <- total
		// |    |   <- window
		// [    ]   <- knob
		// unused   <- visible
		vptracker.setTotalLen(120);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(120, 0, 100, 0, 100, true);
		assertEquals(expected, actual);
		
		// Case 2.4
		// autoscroll=false, preferred=X, visible=null
		// ======== <- total
		//  |    |  <- window
		//  [    ]  <- knob
		//  unused  <- visible
		vptracker.setKnobPos(10);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(120, 10, 100, 10, 100, true);
		assertEquals(expected, actual);
		
		// Case 2.5 - Try to set negative knob pos
		// autoscroll=false, preferred=X, visible=null
		// ======== <- total
		// |    |   <- window
		// [    ]   <- knob
		// unused   <- visible
		vptracker.setKnobPos(-25);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(120, 0, 100, 0, 100, true);
		assertEquals(expected, actual);
		
		// Case 2.6 - Try to set knob pos too far at right
		// autoscroll=false, preferred=X, visible=null
		// ======== <- total
		//   |    | <- window
		//   [    ] <- knob
		//   unused <- visible
		vptracker.setKnobPos(150);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(120, 20, 100, 20, 100, true);
		assertEquals(expected, actual);
		
		// Cases with preferred & visible length
		// Case 3.1
		// autoscroll=false, preferred=X, visible=Y
		//  ======= <- total
		//|      |  <- window
		//  [    ]  <- knob
		//  ------  <- visible
		vptracker.setKnobPos(0);
		vptracker.setTotalLen(110);
		vptracker.setPreferredLen(120);
		vptracker.setVisibleLen(100);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(110, -20, 120, 0, 100, true);
		assertEquals(expected, actual);

		// Case 3.2
		// autoscroll=false, preferred=X, visible=Y
		//  ======= <- total
		// |      | <- window
		//   [    ] <- knob
		//   ------ <- visible
		vptracker.setKnobPos(10);
		
		actual = vptracker.getViewport();
		
		expected = new Viewport(110, -10, 120, 10, 100, true);
		assertEquals(expected, actual);

		// Case 3.3
		// autoscroll=false, preferred=X, visible=Y where preferred < visible
		//  ======= <- total
		//   |    | <- window
		//   [    ] <- knob
		// -------- <- visible
		vptracker.setTotalLen(120);
		vptracker.setPreferredLen(110);
		vptracker.setVisibleLen(130);

		actual = vptracker.getViewport();
		
		expected = new Viewport(120, 10, 110, 10, 110, true);
		assertEquals(expected, actual);
		
		// Cases with visible length only?
	}
	
	@Test
	public void testViewportTracker_IsAutoScroll() {
		System.err.println("testViewportTracker_IsAutoScroll");
		vptracker.setAutoScroll(false);
		assertFalse(vptracker.isAutoScroll());
		vptracker.setAutoScroll(true);
		assertTrue(vptracker.isAutoScroll());
	}
	
	@Test
	public void testViewportTracker_GetPreferredLen() {
		System.err.println("testViewportTracker_GetPreferredLen");
		assertNull(vptracker.getPreferredLen());
		vptracker.setPreferredLen(800);
		assertEquals(Integer.valueOf(800), vptracker.getPreferredLen());
		vptracker.setPreferredLen(null);
		assertNull(vptracker.getPreferredLen());
	}
	
	@Test
	public void testViewportTracker_SwitchAutoScroll() {
		System.err.println("testViewportTracker_SwitchAutoScroll");
		vptracker.setAutoScroll(true);
		assertFalse(vptracker.switchAutoScroll());
		assertFalse(vptracker.isAutoScroll());
		assertTrue(vptracker.switchAutoScroll());
		assertTrue(vptracker.isAutoScroll());
	}
	
	@Test
	public void testOnUpdateProxy_onEvent() {
		System.err.println("testOnUpdateProxy_onEvent");
		OnUpdateProxy proxy = new OnUpdateProxy(serviceMock);
		expect(serviceMock.hasCategories()).andReturn(true);
		expect(serviceMock.isAutoRepaintEnabled()).andReturn(true);
		serviceMock.setAutoRepaintChange(true);
		control.replay();
		
		proxy.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnUpdateProxy_onEvent_AutoRepaintDisabled() {
		System.err.println("testOnUpdateProxy_onEvent_AutoRepaintDisabled");
		OnUpdateProxy proxy = new OnUpdateProxy(serviceMock);
		expect(serviceMock.hasCategories()).andReturn(true);
		expect(serviceMock.isAutoRepaintEnabled()).andReturn(false);
		control.replay();
		
		proxy.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnUpdateProxy_onEvent_HasNoCats() {
		System.err.println("testOnUpdateProxy_onEvent_HasNoCats");
		OnUpdateProxy proxy = new OnUpdateProxy(serviceMock);
		expect(serviceMock.hasCategories()).andReturn(false);
		control.replay();
		
		proxy.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnUpdateProxy_equals() {
		System.err.println("testOnUpdateProxy_equals");
		OnUpdateProxy proxy = new OnUpdateProxy(serviceMock);
		assertTrue(proxy.equals(proxy));
		assertTrue(proxy.equals(new OnUpdateProxy(serviceMock)));
		assertFalse(proxy.equals(new OnUpdateProxy(service)));
		assertFalse(proxy.equals(null));
		assertFalse(proxy.equals(this));
	}
	
	@Test
	public void testOnLengthUpdateProxy_onEvent() {
		System.err.println("testOnLengthUpdateProxy_onEvent");
		OnLengthUpdateProxy proxy = new OnLengthUpdateProxy(serviceMock);
		expect(serviceMock.hasCategories()).andReturn(true);
		serviceMock.adjustAll();
		control.replay();
		
		proxy.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnLengthUpdateProxy_onEvent_HasNoCats() {
		System.err.println("testOnLengthUpdateProxy_onEvent_HasNoCats");
		OnLengthUpdateProxy proxy = new OnLengthUpdateProxy(serviceMock);
		expect(serviceMock.hasCategories()).andReturn(false);
		control.replay();
		
		proxy.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testOnLengthUpdateProxy_equals() {
		System.err.println("testOnLengthUpdateProxy_equals");
		OnLengthUpdateProxy proxy = new OnLengthUpdateProxy(serviceMock);
		assertTrue(proxy.equals(proxy));
		assertTrue(proxy.equals(new OnLengthUpdateProxy(serviceMock)));
		assertFalse(proxy.equals(new OnLengthUpdateProxy(service)));
		assertFalse(proxy.equals(null));
		assertFalse(proxy.equals(this));
	}
	
	@Test
	public void testSetAutoRepaintChange() {
		System.err.println("testSetAutoRepaintChange");
		assertFalse(service.isAutoRepaintChange());
		service.setAutoRepaintChange(true);
		assertTrue(service.isAutoRepaintChange());
		service.setAutoRepaintChange(false);
		assertFalse(service.isAutoRepaintChange());
	}
	
	@Test
	public void testSetAutoScrollButton() {
		System.err.println("testSetAutoScrollButton");
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		JCompAutoScrollButton btnMock = control.createMock(JCompAutoScrollButton.class);
		btnMock.setAutoScroll(true);
		btnMock.addActionListener(service);
		control.replay();
		
		service.setAutoScrollButton(btnMock);
		
		control.verify();
	}
	
	@Test
	public void testSetScrollBar() {
		System.err.println("testSetScrollBar");
		JScrollBar sbMock = control.createMock(JScrollBar.class);
		sbMock.addAdjustmentListener(service);
		control.replay();
		
		service.setScrollBar(sbMock);
		
		control.verify();
	}
	
	@Test
	public void testSetCategories() {
		System.err.println("testSetCategories");
		fillCategories(150);
		control.replay();
		
		service.setCategories(cats);
		
		control.verify();
		assertTrue(cats.onUpdate().isListener(service.getOnUpdateProxy()));
		assertTrue(cats.onLengthUpdate().isListener(service.getOnLengthUpdateProxy()));
		assertTrue(service.hasCategories());
	}
	
	@Test
	public void testSetRootPanel() {
		System.err.println("testSetRootPanel");
		quietlyDisableAutoScroll();
		
		service.setRootPanel(rpanelMock);
		
		vptrackerMock.setKnobPos(9);
		expect(vptrackerMock.getViewport()).andReturn(new Viewport(
				scrollBar.getMaximum(),
				scrollBar.getValue(),
				scrollBar.getVisibleAmount(),
				scrollBar.getValue(),
				scrollBar.getVisibleAmount(),
				false
			));
		rpanelMock.repaint();
		control.replay();
		service.adjustmentValueChanged(null);
		control.verify();
	}
	
	@Test
	public void testSetAutoRepaint_EnableWhenEnabled_HasNoEffect() {
		System.err.println("testSetAutoRepaint_EnableWhenEnabled_HasNoEffect");
		quietlyEnableAutoRepaint();
		control.replay();
		assertTrue(service.isAutoRepaintEnabled());
		
		service.setAutoRepaint(true);
		
		control.verify();
		assertTrue(service.isAutoRepaintEnabled());
	}
	
	@Test
	public void testSetAutoRepaint_EnableWhenDisabled() {
		System.err.println("testSetAutoRepaint_EnableWhenDisabled");
		timerMock.start();
		control.replay();
		assertFalse(service.isAutoRepaintEnabled());
		
		service.setAutoRepaint(true);
		
		control.verify();
		assertTrue(service.isAutoRepaintEnabled());
	}
	
	@Test
	public void testSetAutoRepaint_DisableWhenDisabled_HasNoEffect() {
		System.err.println("testSetAutoRepaint_DisableWhenDisabled_HasNoEffect");
		control.replay();
		assertFalse(service.isAutoRepaintEnabled());
		
		service.setAutoRepaint(false);
		
		control.verify();
		assertFalse(service.isAutoRepaintEnabled());
	}
	
	@Test
	public void testSetAutoRepaint_DisableWhenEnabled() {
		System.err.println("testSetAutoRepaint_DisableWhenEnabled");
		quietlyEnableAutoRepaint();
		timerMock.stop();
		control.replay();
		assertTrue(service.isAutoRepaintEnabled());
		
		service.setAutoRepaint(false);
		
		control.verify();
		assertFalse(service.isAutoRepaintEnabled());
	}
	
	@Test
	public void testSetAutoScroll_EnableWhenDisabled() {
		System.err.println("testSetAutoScroll_EnableWhenDisabled");
		quietlyDisableAutoScroll();
		expect(vptrackerMock.isAutoScroll()).andReturn(false);
		vptrackerMock.setAutoScroll(true);
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		autoScrollButtonMock.setAutoScroll(true);
		expect(vptrackerMock.getViewport()).andReturn(new Viewport(14, 9, 5, 9, 5, false));
		control.replay();
		
		service.setAutoScroll(true);
		
		control.verify();
		assertEquals( 9, scrollBar.getValue());
		assertEquals( 5, scrollBar.getVisibleAmount());
		assertEquals( 0, scrollBar.getMinimum());
		assertEquals(14, scrollBar.getMaximum());
	}
	
	@Test
	public void testSetAutoScroll_EnableWhenEnabled() {
		System.err.println("testSetAutoScroll_EnableWhenEnabled");
		quietlyEnableAutoScroll(); // scrollBar.setValues(9, 5, 0, 14);
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		control.replay();

		service.setAutoScroll(true);
		
		control.verify();
		assertEquals( 9, scrollBar.getValue());
		assertEquals( 5, scrollBar.getVisibleAmount());
		assertEquals( 0, scrollBar.getMinimum());
		assertEquals(14, scrollBar.getMaximum());
	}
	
	@Test
	public void testSetAutoScroll_DisableWhenEnabled() {
		System.err.println("testSetAutoScroll_DisableWhenEnabled");
		quietlyDisableAutoScroll();
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		vptrackerMock.setAutoScroll(false);
		expect(vptrackerMock.isAutoScroll()).andReturn(false);
		autoScrollButtonMock.setAutoScroll(false);
		expect(vptrackerMock.getViewport()).andStubReturn(new Viewport(100, 0, 100, 0, 100, false));
		vptrackerMock.setKnobPos(0);
		control.replay();
		
		service.setAutoScroll(false);
		
		control.verify();
		assertEquals(  0, scrollBar.getValue());
		assertEquals(100, scrollBar.getVisibleAmount());
		assertEquals(  0, scrollBar.getMinimum());
		assertEquals(100, scrollBar.getMaximum());
	}
	
	@Test
	public void testSetAutoScroll_DisableWhenDisabled() {
		System.err.println("testSetAutoScroll_DisableWhenDisabled");
		quietlyDisableAutoScroll();
		expect(vptrackerMock.isAutoScroll()).andReturn(false);
		control.replay();
		
		service.setAutoScroll(false);
		
		control.verify();
		assertEquals( 9, scrollBar.getValue());
		assertEquals( 5, scrollBar.getVisibleAmount());
		assertEquals( 0, scrollBar.getMinimum());
		assertEquals(14, scrollBar.getMaximum());
	}
	
	@Test
	public void testSetPrefferedNumberOfBars() {
		System.err.println("testSetPrefferedNumberOfBars");
		vptrackerMock.setPreferredLen(200);
		control.replay();
		
		service.setPreferredNumberOfBars(200);
		
		control.verify();
	}
	
	@Test
	public void testUpdateNumberOfVisibleBars() {
		System.err.println("testUpdateNumberOfVisibleBars");
		vptrackerMock.setVisibleLen(25);
		control.replay();
		
		service.updateNumberOfVisibleBars(25);
		
		control.verify();
	}
	
	@Test
	public void testAdjustmentValueChanged() {
		System.err.println("testAdjustmentValueChanged");
		quietlyDisableAutoScroll();
		quietlySetRootPanel();
		vptrackerMock.setKnobPos(9);
		expect(vptrackerMock.getViewport()).andReturn(new Viewport(14, 9, 5, 9, 5, false));
		rpanelMock.repaint();
		control.replay();
		service.setAutoRepaintChange(true);
		
		service.adjustmentValueChanged(null);
		
		control.verify();
		assertFalse(service.isAutoRepaintChange());
	}
	
	@Test
	public void testActionPerformed_AutoScrollButton_Enable() {
		System.err.println("testActionPerformed_AutoScrollButton_Enable");
		quietlyDisableAutoScroll();
		quietlySetCategories();
		quietlySetRootPanel();
		expect(vptrackerMock.switchAutoScroll()).andReturn(true);
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		autoScrollButtonMock.setAutoScroll(true);
		expect(vptrackerMock.getViewport()).andStubReturn(new Viewport(20, 14, 5, 14, 5, false));
		vptrackerMock.setKnobPos(14);
		rpanelMock.repaint();
		control.replay();
		
		service.actionPerformed(new ActionEvent(autoScrollButtonMock, 0, ""));

		control.verify();
		assertEquals(20, scrollBar.getMaximum());
		assertEquals( 0, scrollBar.getMinimum());
		assertEquals( 5, scrollBar.getVisibleAmount());
		assertEquals(14, scrollBar.getValue());
	}

	@Test
	public void testActionPerformed_AutoScrollButton_Disable() {
		System.err.println("testActionPerformed_AutoScrollButton_Disable");
		quietlyEnableAutoScroll();
		quietlySetCategories();
		quietlySetRootPanel();
		expect(vptrackerMock.switchAutoScroll()).andReturn(false);
		expect(vptrackerMock.isAutoScroll()).andReturn(false);
		autoScrollButtonMock.setAutoScroll(false);
		expect(vptrackerMock.getViewport()).andStubReturn(new Viewport(20, 14, 5, 14, 5, false));
		vptrackerMock.setKnobPos(14);
		rpanelMock.repaint();
		control.replay();
		
		service.actionPerformed(new ActionEvent(autoScrollButtonMock, 0, ""));
		
		control.verify();
		assertEquals(20, scrollBar.getMaximum());
		assertEquals( 0, scrollBar.getMinimum());
		assertEquals( 5, scrollBar.getVisibleAmount());
		assertEquals(14, scrollBar.getValue());
	}
	
	@Test
	public void testActionPerformed_Timer_HasChanges() {
		System.err.println("testActionPerformed_Timer_HasChanges");
		quietlyEnableAutoRepaint();
		quietlySetRootPanel();
		quietlySetCategories();
		service.setAutoRepaintChange(true);
		rpanelMock.repaint();
		control.replay();
		
		service.actionPerformed(new ActionEvent(timerMock, 0, ""));
		// The consecutive calls shouldn't cause repaint
		// Because "changes" flag was reset
		service.actionPerformed(new ActionEvent(timerMock, 0, ""));
		
		control.verify();
	}
	
	@Test
	public void testActionPerformed_Timer_HasNoChanges() {
		System.err.println("testActionPerformed_Timer_HasNoChanges");
		quietlyEnableAutoRepaint();
		quietlySetRootPanel();
		quietlySetCategories();
		service.setAutoRepaintChange(false);
		control.replay();
		
		service.actionPerformed(new ActionEvent(timerMock, 0, ""));
		
		control.verify();
	}
	
	@Test
	public void testAdjustAll_ScrollBarChanged() {
		System.err.println("testAdjustAll_ScrollBarChanged");
		quietlyDisableAutoScroll();
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		autoScrollButtonMock.setAutoScroll(true);
		expect(vptrackerMock.getViewport()).andStubReturn(new Viewport(120, 0, 120, 0, 120, false));
		vptrackerMock.setKnobPos(0); // reaction on value change 
		control.replay();
		
		service.adjustAll();
		
		control.verify();
		assertEquals(120, scrollBar.getMaximum());
		assertEquals(  0, scrollBar.getMinimum());
		assertEquals(120, scrollBar.getVisibleAmount());
		assertEquals(  0, scrollBar.getValue());
	}
	
	@Test
	public void testAdjustAll_ScrollBarNotChanged() {
		System.err.println("testAdjustAll_ScrollBarNotChanged");
		quietlyDisableAutoScroll();
		expect(vptrackerMock.isAutoScroll()).andReturn(true);
		autoScrollButtonMock.setAutoScroll(true);
		expect(vptrackerMock.getViewport()).andReturn(new Viewport(14, 9, 5, 9, 5, false));
		control.replay();
		
		service.adjustAll();
		
		control.verify();
		assertEquals(14, scrollBar.getMaximum());
		assertEquals( 0, scrollBar.getMinimum());
		assertEquals( 5, scrollBar.getVisibleAmount());
		assertEquals( 9, scrollBar.getValue());
	}
	
	@Test
	public void testGetViewport_HasCats() {
		System.err.println("testGetViewport_HasCats");
		quietlySetCategories(250);
		vptrackerMock.setTotalLen(250);
		expect(vptrackerMock.getViewport()).andReturn(new Viewport(250, 0, 120, 0, 120, false));
		expect(vptrackerMock.getPreferredLen()).andReturn(120);
		control.replay();
		
		CategoryAxisViewport actual = service.getViewport();
		
		control.verify();
		CategoryAxisViewportImpl expected = new CategoryAxisViewportImpl();
		expected.setCategoryRangeByFirstAndNumber(0, 120);
		expected.setPreferredNumberOfBars(120);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetViewport_HasNoCats() {
		System.err.println("testGetViewport_HasNoCats");
		vptrackerMock.setTotalLen(0);
		expect(vptrackerMock.getViewport()).andReturn(new Viewport(0, -100, 0, 0, 0, false));
		expect(vptrackerMock.getPreferredLen()).andReturn(100);
		control.replay();
		
		CategoryAxisViewport actual = service.getViewport();
		
		control.verify();
		CategoryAxisViewportImpl expected = new CategoryAxisViewportImpl();
		expected.setCategoryRangeByFirstAndNumber(-100, 0);
		expected.setPreferredNumberOfBars(100);
		assertEquals(expected, actual);
	}

}
