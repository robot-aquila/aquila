package ru.prolib.aquila.ui;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.Properties;

import javax.swing.JPanel;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioEvent;

/**
 * $Id$
 */
public class PortfolioDataPanelTest {

	private static IMocksControl control;
	
	private CurrentPortfolio currPortfolio;
	private UiTexts texts = new UiTexts();
	private PortfolioDataPanel panel;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		currPortfolio = control.createMock(CurrentPortfolio.class);
		Properties l = new Properties();
		l.setProperty("LB_CASH" , "LB_CASH");
		l.setProperty("LB_BALANCE", "LB_BALANCE");
		l.setProperty("LB_VAR_MARGIN", "LB_VAR_MARGIN");
		texts.setClassLabels("PortfolioDataPanel", new ClassLabels("PortfolioDataPanel", l));
		
		panel = new PortfolioDataPanel(currPortfolio, texts);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor() {
		IsInstanceOf.instanceOf(JPanel.class).matches(panel);
		IsInstanceOf.instanceOf(EventListener.class).matches(panel);
		IsInstanceOf.instanceOf(Starter.class).matches(panel);
		
		assertEquals(currPortfolio, panel.getCurrPortfolio());
		LabeledTextValue val = panel.getBalanceVal();
		assertEquals("LB_BALANCE", val.getLabel());
		assertEquals("", val.getValue());
		assertEquals(val, panel.getComponent(1));
		
		val = panel.getCashVal();
		assertEquals("LB_CASH", val.getLabel());
		assertEquals("", val.getValue());
		assertEquals(val, panel.getComponent(0));
		
		val = panel.getVarMargin();
		assertEquals("LB_VAR_MARGIN", val.getLabel());
		assertEquals("", val.getValue());
		assertEquals(val, panel.getComponent(2));
	}
	
	@Test
	public void testOnEvent() {
		EventType onPrtChanged = control.createMock(EventType.class);
		Portfolio prt = control.createMock(Portfolio.class);
		PortfolioEvent evt = new PortfolioEvent(onPrtChanged, prt);
		
		expect(currPortfolio.OnCurrentPortfolioChanged()).andStubReturn(onPrtChanged);
		expect(prt.getCash()).andReturn(98.6453);
		expect(prt.getBalance()).andReturn(361.842);
		expect(prt.getVariationMargin()).andReturn(23.671);
		
		control.replay();
		panel.onEvent(evt);
		control.verify();
		
		assertEquals("361,84", panel.getBalanceVal().getValue());
		assertEquals("98,65",  panel.getCashVal().getValue());
		assertEquals("23,67", panel.getVarMargin().getValue());
	}
	
	@Test
	public void testStop() throws Exception {
		EventType onPrtChanged = control.createMock(EventType.class);
		currPortfolio.stop();
		expect(currPortfolio.OnCurrentPortfolioChanged()).andStubReturn(onPrtChanged);
		onPrtChanged.removeListener(panel);
		
		
		control.replay();
		panel.stop();
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		EventType onPrtChanged = control.createMock(EventType.class);
		expect(currPortfolio.OnCurrentPortfolioChanged()).andStubReturn(onPrtChanged);
		onPrtChanged.addListener(panel);
		currPortfolio.start();
		
		control.replay();
		panel.start();
		control.verify();
	}

}
