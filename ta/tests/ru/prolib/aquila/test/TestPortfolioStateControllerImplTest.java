package ru.prolib.aquila.test;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.AssetImpl;

public class TestPortfolioStateControllerImplTest {
	AssetImpl asset;
	TestPortfolioStateControllerImpl ctrl;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		asset = new AssetImpl("RTS", "SPBFUT", 5.0d, 0);
		asset.updateEstimatedPrice(140000.0d);
		asset.updatePrice(141000.0d);
		asset.updatePriceStepMoney(3.17324d);
		asset.updateInitialMarginMoney(12206.85d);
		ctrl = new TestPortfolioStateControllerImpl();
	}
	
	@Test
	public void testSetMoney_Ok() throws Exception {
		ctrl.setMoney(1000000.0d);
		assertEquals(1000000.0d, ctrl.getMoney(), 0.01d);
	}
	
	@Test (expected=TestPortfolioStatePeriodOpenedException.class)
	public void testSetMoney_ThrowsIfOpened() throws Exception {
		ctrl.asset = asset;
		ctrl.setMoney(1000000.0d);
	}
	
	@Test
	public void testSetPosition_Ok() throws Exception {
		ctrl.setPosition(-10);
		assertEquals(-10, ctrl.getPosition());
	}
	
	@Test (expected=TestPortfolioStatePeriodOpenedException.class)
	public void testSetPosition_ThrowsIfOpened() throws Exception {
		ctrl.asset = asset;
		ctrl.setPosition(-10);
	}
	
	@Test
	public void testClosePeriod_NotOpened() throws Exception {
		ctrl.closePeriod();
	}
	
	@Test
	public void testClosePeriod_OpenedNotInPosition() throws Exception {
		ctrl.asset = asset;
		ctrl.money = 1000000.0d;
		ctrl.position = 0;
		ctrl.cachePriceStepMoney = asset.getPriceStepMoney();
		ctrl.cacheEstimatedPrice = asset.getEstimatedPrice();
		ctrl.cacheInitialMarginMoney = asset.getInitialMarginMoney();
		ctrl.variationMarginPoints = 20; // 4 шага = 3.17324 * 4 = 12.69
		ctrl.initialMarginMoney = 0.0d;
		
		ctrl.closePeriod();
		
		assertNull(ctrl.asset);
		assertEquals(1000012.69d, ctrl.money, 0.01d);
		assertEquals(0.0d, ctrl.variationMarginPoints, 0.01d);
		assertEquals(0.0d, ctrl.initialMarginMoney, 0.01d);
		assertEquals(0, ctrl.position);
	}
	
	@Test
	public void testClosePeriod_OpenedInPosition() throws Exception {
		ctrl.asset = asset;
		ctrl.money = 1000000.0d;
		ctrl.position = 2;
		ctrl.cachePriceStepMoney = asset.getPriceStepMoney();
		ctrl.cacheEstimatedPrice = asset.getEstimatedPrice();
		ctrl.cacheInitialMarginMoney = asset.getInitialMarginMoney();
		ctrl.variationMarginPoints = -280020; // -20 пунктов потеряно
		ctrl.initialMarginMoney = 24413.7d;
		
		ctrl.closePeriod();
		
		assertNull(ctrl.asset);
		// 1980 пунктов получено по позиции = +  1256.60
		// 2 ГО вернуть 	   				= + 24413.70
		// начальный баланс	   				= 1000000.00
		// Итого:			   				: 1025670.30
		assertEquals(1025670.3d, ctrl.money, 0.01d);
		assertEquals(0.0d, ctrl.variationMarginPoints, 0.01d);
		assertEquals(0.0d, ctrl.initialMarginMoney, 0.01d);
		assertEquals(2, ctrl.position);
	}
	
	@Test
	public void testGetVariationMargin_IfOpenedV1() throws Exception {
		ctrl.cachePriceStepMoney = asset.getPriceStepMoney();
		ctrl.cacheEstimatedPrice = asset.getEstimatedPrice();
		ctrl.cacheInitialMarginMoney = asset.getInitialMarginMoney();
		// Открыт -5 шорт по расчетной -> с каждой позы -1000pts = -5000pts
		// 5000pts = 1000 шагов = -3173.24 руб
		ctrl.position = -5;
		ctrl.variationMarginPoints = 700000.0d;
		ctrl.asset = asset;
		
		assertEquals(-3173.24d, ctrl.getVariationMargin(), 0.01d);
	}
	
	@Test
	public void testGetVariationMargin_IfOpenedV2() throws Exception {
		ctrl.cachePriceStepMoney = asset.getPriceStepMoney();
		ctrl.cacheEstimatedPrice = asset.getEstimatedPrice();
		ctrl.cacheInitialMarginMoney = asset.getInitialMarginMoney();
		// Открыт 2 лонг по расчетной -> с каждой позы +1000pts = +2000pts
		// 2000pts = 400 шагов = +1269.30 руб
		ctrl.position = 2;
		ctrl.variationMarginPoints = -280000.0d;
		ctrl.asset = asset;
		
		assertEquals(1269.30, ctrl.getVariationMargin(), 0.01d);
	}
	
	@Test
	public void testGetVariationMargin_IfNotOpened() throws Exception {
		assertEquals(0.0d, ctrl.getVariationMargin(), 0.01d);
	}
	
	@Test
	public void testGetInitialMargin_IfOpened() throws Exception {
		ctrl.initialMarginMoney = 100500d;
		ctrl.asset = asset;
		
		assertEquals(100500d, ctrl.getInitialMargin(), 0.01d);
	}
	
	@Test
	public void testGetInitialMargin_IfNotOpened() throws Exception {
		ctrl.initialMarginMoney = 100500d;
		
		assertEquals(0.0d, ctrl.getInitialMargin(), 0.01d);
	}
	
	@Test (expected=TestPortfolioStatePeriodOpenedException.class)
	public void testOpenPeriod_ThrowsIfAlreadyOpened() throws Exception {
		ctrl.asset = asset;
		ctrl.openPeriod(asset);
	}
	
	@Test
	public void testOpenPeriod_NotInPosition() throws Exception {
		ctrl.setMoney(1000000.0d);
		ctrl.setPosition(0);
		ctrl.openPeriod(asset);
		
		assertSame(asset, ctrl.asset);
		assertEquals(140000d, ctrl.cacheEstimatedPrice, 0.1d);
		assertEquals(12206.85d, ctrl.cacheInitialMarginMoney, 0.001d);
		assertEquals(3.17324d, ctrl.cachePriceStepMoney, 0.000001d);
		assertEquals(0d, ctrl.initialMarginMoney, 0.001d);
		assertEquals(0d, ctrl.variationMarginPoints, 0.001d);
		assertEquals(1000000.0d, ctrl.money, 0.001d);
		assertEquals(0, ctrl.position);
	}

	@Test
	public void testOpenPeriod_InPosition() throws Exception {
		ctrl.setMoney(1000000.0d);
		ctrl.setPosition(-3);
		ctrl.openPeriod(asset);
		
		assertSame(asset, ctrl.asset);
		assertEquals(140000d, ctrl.cacheEstimatedPrice, 0.1d);
		assertEquals(12206.85d, ctrl.cacheInitialMarginMoney, 0.001d);
		assertEquals(3.17324d, ctrl.cachePriceStepMoney, 0.000001d);
		assertEquals(-36620.55d, ctrl.initialMarginMoney, 0.001d);
		assertEquals(36620.55d, ctrl.getInitialMargin(), 0.001d);
		assertEquals(420000d, ctrl.variationMarginPoints, 0.001d);
		assertEquals(-1903.94d, ctrl.getVariationMargin(), 0.001d);
		assertEquals(963379.45d, ctrl.money, 0.001d);
		assertEquals(963379.45d, ctrl.getMoney(), 0.001d);
		assertEquals(-3, ctrl.position);
		assertEquals(-3, ctrl.getPosition());
	}
	
	@Test (expected=TestPortfolioStatePeriodNotOpenedException.class)
	public void testChangePosition_ThrowsIfNotOpened() throws Exception {
		ctrl.changePosition(1, 142000d);
	}
	
	@Test
	public void testChangePosition2_V1() throws Exception {
		ctrl.setMoney(1000000.0d);
		ctrl.setPosition(-1);
		ctrl.openPeriod(asset);
		
		ctrl.changePosition(-2, 140000d);
		
		assertEquals(-3, ctrl.getPosition());
		assertEquals(36620.55d, ctrl.getInitialMargin(), 0.001d);
		assertEquals(-1903.94d, ctrl.getVariationMargin(), 0.001d);
		assertEquals(963379.45d, ctrl.getMoney(), 0.001d);
		
		ctrl.changePosition(4, 139000d); // + 3000pts + 2000pts 
		
		assertEquals(1, ctrl.getPosition());
		assertEquals(12206.85d, ctrl.getInitialMargin(), 0.001d);
		assertEquals(3173.24d, ctrl.getVariationMargin(), 0.001d);
		assertEquals(987793.15d, ctrl.getMoney(), 0.001d);
		
		ctrl.closePeriod();
		assertEquals(1003173.24d, ctrl.getMoney(), 0.001d);
	}
	
	@Test
	public void testChangePosition2_V2() throws Exception {
		ctrl.setMoney(1000000.0d);
		ctrl.openPeriod(asset);
		
		ctrl.changePosition(-5, 139000); // -2000*5 = 10000pts=
		ctrl.closePeriod();
		
		assertEquals(993653.52d, ctrl.getMoney(), 0.001d);
	}
	
	@Test
	public void testChangePosition1_Ok() throws Exception {
		asset.updatePrice(129000.0d);
		IMocksControl control = createStrictControl();
		ctrl = createMockBuilder(TestPortfolioStateControllerImpl.class)
			.withConstructor()
			.addMockedMethod("changePosition", int.class, double.class)
			.createMock(control);
		ctrl.changePosition(15, 129000.0d);
		control.replay();
		
		ctrl.openPeriod(asset);
		ctrl.changePosition(15);
		
		control.verify();
	}
	
	@Test (expected=TestPortfolioStatePeriodNotOpenedException.class)
	public void testChangePosition1_ThrowsIfNotOpened() throws Exception {
		ctrl.changePosition(15);
	}

}
