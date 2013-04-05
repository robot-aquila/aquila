package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

public class AssetLazyTest {
	IMocksControl control;
	Observer observer;
	AssetsImpl assets;
	AssetImpl asset;
	AssetLazy lazy;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		assets = new AssetsImpl();
		asset = new AssetImpl("RTS", "SPBFUT", 5, 0);
		lazy = new AssetLazy(assets, "RTS");
	}
	
	@Test
	public void testGetAssetCode() throws Exception {
		assertEquals("RTS", lazy.getAssetCode());
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testGetClassCode_ThrowsNoAsset() throws Exception {
		control.replay();
		
		lazy.getClassCode();
	}
	
	@Test
	public void testGetClassCode_Ok() throws Exception {
		assets.add(asset);
		control.replay();
		
		assertEquals("SPBFUT", lazy.getClassCode());
		assertEquals("SPBFUT", lazy.getClassCode());
		
		control.verify();
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testGetPriceStep_ThrowsNoAsset() throws Exception {
		control.replay();
		
		lazy.getPriceStep();
	}
	
	@Test
	public void testGetPriceStep_Ok() throws Exception {
		assets.add(asset);
		control.replay();
		
		assertEquals(5.0d, lazy.getPriceStep(), 0.01d);
		assertEquals(5.0d, lazy.getPriceStep(), 0.01d);
		
		control.verify();
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testGetPriceScale_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.getPriceScale();
	}
	
	@Test
	public void testGetPriceScale_Ok() throws Exception {
		assets.add(asset);
		control.replay();
		
		assertEquals(0, lazy.getPriceScale());
		assertEquals(0, lazy.getPriceScale());
		
		control.verify();
	}

	@Test (expected=AssetLazyLoadException.class)
	public void testGetPrice_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.getPrice();
	}
	
	@Test
	public void testGetPrice_Ok() throws Exception {
		asset.updatePrice(100.00d);
		assets.add(asset);
		control.replay();
		
		assertEquals(100.00d, lazy.getPrice(), 0.01d);
		assertEquals(100.00d, lazy.getPrice(), 0.01d);
		
		control.verify();
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testGetPriceStepMoney_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.getPriceStepMoney();
	}
	
	@Test
	public void testGetPriceStepMoney_Ok() throws Exception {
		asset.updatePriceStepMoney(110.00d);
		assets.add(asset);
		control.replay();

		assertEquals(110.00d, lazy.getPriceStepMoney(), 0.01d);
		assertEquals(110.00d, lazy.getPriceStepMoney(), 0.01d);
		
		control.verify();
	}

	@Test (expected=AssetLazyLoadException.class)
	public void testGetInitialMarginMoney_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.getInitialMarginMoney();
	}
	
	@Test
	public void testGetInitialMarginMoney_Ok() throws Exception {
		asset.updateInitialMarginMoney(120.00d);
		assets.add(asset);
		control.replay();
		
		assertEquals(120.00d, lazy.getInitialMarginMoney(), 0.01d);
		assertEquals(120.00d, lazy.getInitialMarginMoney(), 0.01d);
		
		control.verify();
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testGetEstimatedPrice_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.getEstimatedPrice();
	}
	
	@Test
	public void testGetEstimatedPrice_Ok() throws Exception {
		asset.updateEstimatedPrice(180.00d);
		assets.add(asset);
		control.replay();
		
		assertEquals(180.00d, lazy.getEstimatedPrice(), 0.01d);
		assertEquals(180.00d, lazy.getEstimatedPrice(), 0.01d);
		
		control.verify();
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testFormatPrice_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.formatPrice(12345.00d);
	}
	
	@Test
	public void testFormatPrice_Ok() throws Exception {
		assets.add(asset);
		control.replay();
		
		assertEquals("12345", lazy.formatPrice(12345d));
		assertEquals("12345", lazy.formatPrice(12345d));
		
		control.verify();
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testPriceToMoney_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.priceToMoney(12345.00d);
	}
	
	@Test
	public void testPriceToMoney_Ok() throws Exception {
		asset.updatePriceStepMoney(0.02d);
		assets.add(asset);
		control.replay();
		
		assertEquals(0.04d, lazy.priceToMoney(10d), 0.001d);
		assertEquals(0.04d, lazy.priceToMoney(10d), 0.001d);
		
		control.verify();
	}
	
	@Test (expected=AssetLazyLoadException.class)
	public void testRoundPrice_ThrowsNoAsset() throws Exception {
		control.replay();

		lazy.roundPrice(12345.00d);
	}
	
	@Test
	public void testRoundPrice_Ok() throws Exception {
		assets.add(asset);
		control.replay();
		
		assertEquals(12345d, lazy.roundPrice(12342.567d), 0.001d);
		assertEquals(12340d, lazy.roundPrice(12341.567d), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testForwardEventsFromLoadedAsset() throws Exception {
		assets.add(asset);
		observer.update(lazy, Asset.EVENT_PRICE);
		control.replay();
		
		lazy.addObserver(observer);
		asset.updatePrice(12345.67d);
		lazy.getPrice();
		asset.notifyObservers(Asset.EVENT_PRICE);
		
		control.verify();
	}
	
	@Test
	public void testListenAssetsUntilAssetLoaded() throws Exception {
		assertEquals(1, assets.countObservers()); // подписался
		assertEquals(0, asset.countObservers()); // никто не подписан
		assets.add(asset);
		assertEquals(0, assets.countObservers()); // отписался
		assertEquals(1, asset.countObservers()); // подписался
	}
	
	@Test
	public void testListenAssetsButIgnoreUninterestedAssets() throws Exception {
		AssetImpl asset2 = new AssetImpl("SBER", "EQBR", 0.01d, 2);
		assets.add(asset2);
		assertEquals(1, assets.countObservers()); // все осталось как прежде
		assertEquals(0, asset2.countObservers()); // никто не подписан
	}

}
