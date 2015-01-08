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

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioEvent;

/**
 * $Id$
 */
public class PortfolioStatusBarTest {
	private static IMocksControl control;
	private UiTexts texts = new UiTexts();
	private PortfolioStatusBar panel;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		Properties l = new Properties();
		l.setProperty("LB_ACCOUNT" , "LB_ACCOUNT");
		l.setProperty("LB_CASH" , "LB_CASH");
		l.setProperty("LB_BALANCE", "LB_BALANCE");
		l.setProperty("LB_VAR_MARGIN", "LB_VAR_MARGIN");
		texts.setClassLabels("PortfolioDataPanel", new ClassLabels("PortfolioDataPanel", l));
		
		panel = new PortfolioStatusBar(texts);
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
		assertEquals(val, panel.getComponent(2));
		
		val = panel.getCashVal();
		assertEquals("LB_CASH", val.getLabel());
		assertEquals("", val.getValue());
		assertEquals(val, panel.getComponent(1));
		
		val = panel.getVarMargin();
		assertEquals("LB_VAR_MARGIN", val.getLabel());
		assertEquals("", val.getValue());
		assertEquals(val, panel.getComponent(3));
		
		val = panel.getAccount();
		assertEquals("LB_ACCOUNT", val.getLabel());
		assertEquals("", val.getValue());
		assertEquals(val, panel.getComponent(0));
	}
	
	@Test
	public void testOnEvent_CurrPortfolioChanged() {
		EventType onCurrPrtChanged = control.createMock(EventType.class);
		EventType onPrtChanged = control.createMock(EventType.class);
		Portfolio prt = control.createMock(Portfolio.class);
		PortfolioEvent evt = new PortfolioEvent(onCurrPrtChanged, prt);
		Account acc = control.createMock(Account.class);
		
		expect(currPortfolio.OnCurrentPortfolioChanged()).andStubReturn(onCurrPrtChanged);
		
		expect(prt.getAccount()).andStubReturn(acc);
		expect(prt.getCash()).andReturn(98.6453);
		expect(prt.getBalance()).andReturn(361.842);
		expect(prt.getVariationMargin()).andReturn(23.671);
		
		expect(prt.OnChanged()).andStubReturn(onPrtChanged);
		onPrtChanged.addListener(panel);
		
		control.replay();
		panel.onEvent(evt);
		control.verify();
		
		assertEquals(String.format("%-40s",acc.toString()), panel.getAccount().getValue());
		assertEquals(String.format("%20.2f", 361.842), panel.getBalanceVal().getValue());
		assertEquals(String.format("%20.2f", 98.6453),  panel.getCashVal().getValue());
		assertEquals(String.format("%5.2f", 23.671), panel.getVarMargin().getValue());
	}
	
	@Test
	public void testOnEvent_PortfolioChangedMarginIsNull() {
		EventType onCurrPrtChanged = control.createMock(EventType.class);
		EventType onPrtChanged = control.createMock(EventType.class);
		Portfolio prt = control.createMock(Portfolio.class);
		Account acc = control.createMock(Account.class);
		
		expect(currPortfolio.OnCurrentPortfolioChanged()).andStubReturn(onCurrPrtChanged);
		expect(currPortfolio.getCurrentPortfolio()).andStubReturn(prt);
		expect(prt.OnChanged()).andReturn(onPrtChanged);
		expect(currPortfolio.getCurrentPortfolio()).andStubReturn(prt);
		expect(prt.getAccount()).andStubReturn(acc);
		expect(prt.getCash()).andReturn(98.6453);
		expect(prt.getBalance()).andReturn(361.842);
		expect(prt.getVariationMargin()).andReturn(null);
		
		control.replay();
		panel.onEvent(new EventImpl(onPrtChanged));
		control.verify();
		
		assertEquals(String.format("%-40s",acc.toString()), panel.getAccount().getValue());
		assertEquals(String.format("%20.2f", 361.842), panel.getBalanceVal().getValue());
		assertEquals(String.format("%20.2f", 98.6453),  panel.getCashVal().getValue());
		assertEquals(String.format("%5.2f", 0.00), panel.getVarMargin().getValue());
	}

}
