package ru.prolib.aquila.ChaosTheory;


import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class AssetImplTest {
	IMocksControl control;
	Observer observer;
	AssetImpl asset;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		asset = new AssetImpl("RTS", "SPBFUT", 5, 3);
	}
	
	@Test
	public void testPrimaryAttrs() throws Exception {
		assertEquals("RTS", asset.getAssetCode());
		assertEquals("SPBFUT", asset.getClassCode());
		assertEquals(5.00d, asset.getPriceStep(), 0.01d);
		assertEquals(3, asset.getPriceScale());
	}
	
	@Test (expected=AssetValueNotAvailableException.class)
	public void testGetPrice_ThrowsIfNotAvailable() throws Exception {
		asset.getPrice();
	}
	
	@Test (expected=AssetValueNotAvailableException.class)
	public void testGetInitialMarginMoney_ThrowsIfNotAvailable()
		throws Exception
	{
		asset.getInitialMarginMoney();
	}
	
	@Test (expected=AssetValueNotAvailableException.class)
	public void testGetProceStepMoney_ThrowsIfNotAvailable() throws Exception {
		asset.getPriceStepMoney();
	}
	
	@Test
	public void testUpdatePrice() throws Exception {
		asset.updatePrice(1234.56d);
		assertTrue(asset.hasChanged());
		assertEquals(1234.56d, asset.getPrice(), 0.01d);
		asset.notifyObservers(Asset.EVENT_PRICE);
		
		asset.updatePrice(222.33d);
		assertTrue(asset.hasChanged());
		assertEquals(222.33d, asset.getPrice(), 0.01d);
		asset.notifyObservers(Asset.EVENT_PRICE);
	}
	
	@Test
	public void testUpdatePriceStepMoney() throws Exception {
		asset.updatePriceStepMoney(3.2222d);
		assertTrue(asset.hasChanged());
		assertEquals(3.2222d, asset.getPriceStepMoney(), 0.0001d);
		asset.notifyObservers(Asset.EVENT_CLEARING);
		assertFalse(asset.hasChanged());
		
		asset.updatePriceStepMoney(3.2222d);
		assertFalse(asset.hasChanged());
	}
	
	@Test
	public void testUpdateInitialMargin() throws Exception {
		asset.updateInitialMarginMoney(10123.23d);
		assertTrue(asset.hasChanged());
		assertEquals(10123.23d, asset.getInitialMarginMoney(), 0.001d);
		asset.notifyObservers(Asset.EVENT_CLEARING);
		assertFalse(asset.hasChanged());
		
		asset.updateInitialMarginMoney(10123.23d);
		assertFalse(asset.hasChanged());
	}
	
	@Test
	public void testUpdateEstimatedPrice() throws Exception {
		asset.updateEstimatedPrice(111222.0d);
		assertTrue(asset.hasChanged());
		assertEquals(111222.0d, asset.getEstimatedPrice(), 0.01d);
		asset.notifyObservers(Asset.EVENT_CLEARING);
		assertFalse(asset.hasChanged());
		
		asset.updateEstimatedPrice(111222.0d);
		assertFalse(asset.hasChanged());
	}
	
	@Test
	public void testFormatPrice_NonZeroPriceScale() throws Exception {
		asset = new AssetImpl("CODE", "SEC", 0.05d, 2);
		assertEquals("123.00", asset.formatPrice(123.00153123123d));
		assertEquals("222.15", asset.formatPrice(222.1345d));
	}
	
	@Test
	public void testFormatPrice_ZeroPriceScale() throws Exception {
		asset = new AssetImpl("CODE", "SEC", 5.0d, 0);
		assertEquals("14555", asset.formatPrice(14553.15));
		assertEquals("14550", asset.formatPrice(14551.95));
	}
	
	@Test
	public void testRoundPrice_NonZeroPriceScale() throws Exception {
		asset = new AssetImpl("CODE", "SEC", 0.05d, 2);
		assertEquals(123.00d, asset.roundPrice(123.00153123123d), 0.00000001d);
		assertEquals(222.15d, asset.roundPrice(222.1345d), 		  0.00000001d);
	}
	
	@Test
	public void testRoundPrice_ZeroPriceScale() throws Exception {
		asset = new AssetImpl("CODE", "SEC", 5.0d, 0);
		assertEquals(14555.0d, asset.roundPrice(14553.15), 0.01d);
		assertEquals(14550.0d, asset.roundPrice(14551.95), 0.01d);
	}
	
	@Test (expected=AssetValueNotAvailableException.class)
	public void testPriceToMoney_ThrowsIfPriceStepMoneyNotAvailable()
		throws Exception
	{
		asset.priceToMoney(12300.00d);
	}
	
	@Test
	public void testPriceToMoney_Ok() throws Exception {
		asset.updatePriceStepMoney(0.2d);
		assertEquals(492.00d, asset.priceToMoney(12300.00d), 0.0001d);
	}
	
	@Test (expected=AssetValueNotAvailableException.class)
	public void testGetEstimatedPrice_ThrowsNotAvailable() throws Exception {
		asset.getEstimatedPrice();
	}
	
	@Test
	public void testGetEstimatedPrice_Ok() throws Exception {
		asset.updateEstimatedPrice(123.45d);
		assertEquals(123.45d, asset.getEstimatedPrice(), 0.01d);
	}
	
	@Test
	public void testObserve() throws Exception {
		observer.update(asset, null);
		asset.addObserver(observer);
		control.replay();
		
		asset.updatePrice(123d);
		asset.notifyObservers();
		
		control.verify();
	}

}
