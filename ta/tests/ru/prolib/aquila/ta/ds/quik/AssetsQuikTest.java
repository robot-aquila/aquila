package ru.prolib.aquila.ta.ds.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.rxltdde.Xlt.Table;

public class AssetsQuikTest {
	IMocksControl control;
	AssetsQuik assets;
	RXltDdeDispatcher dispatcher;
	Observer observer,observer2;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		//Logger.getRootLogger().setLevel(Level.ALL);
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(RXltDdeDispatcher.class);
		observer = control.createMock(Observer.class);
		observer2 = control.createMock(Observer.class);
		assets = new AssetsQuik("foo", "bar");
	}
	
	private void assertAsset(String assetCode, String classCode,
			double step, int scale, double price, double estPrice,
			double stepMoney, double initMoney) throws Exception
	{
		Asset asset = assets.getByCode(assetCode);
		assertEquals(assetCode, asset.getAssetCode());
		assertEquals(classCode, asset.getClassCode());
		assertEquals(step, asset.getPriceStep(), 0.0001d);
		assertEquals(scale, asset.getPriceScale());
		assertEquals(price, asset.getPrice(), 0.0001d);
		assertEquals(estPrice, asset.getEstimatedPrice(), 0.0001d);
		assertEquals(stepMoney, asset.getPriceStepMoney(), 0.0001d);
		assertEquals(initMoney, asset.getInitialMarginMoney(), 0.0001d);
	}
	
	@Test
	public void testOnTable_IgnoreUnknownTable() throws Exception {
		Object cells[] = { "one", "two" };
		assets.onTable(new Table(cells, "unknown", "RXCX", 2));
	}
	
	@Test
	public void testOnTable_Assets_IgnoreTableWithNe7Cols() throws Exception {
		Object cells[] = { "FOO", "BAR", 5.0d, 2, null };
		assets.onTable(new Table(cells, "foo", "RXCX", 5));
	}
	
	@Test
	public void testOnTable_Assets_AddAndIgnoreRowExceptions()
		throws Exception
	{
		Object cells[] = {
			// code, class, step, scale, est. price, step money, init. money
			"SBER","EQBR",	"err", 5.0d,    130d, 1.00d,   130.00d,
			"RTS", "SPBFUT", 5.0d, 0.0d, 140000d, 3.22d, 11200.00d,
		};
		observer.update(same(assets), isA(AssetImpl.class));
		expectLastCall().andDelegateTo(new Observer(){
			@Override
			public void update(Observable o, Object arg) {
				assertTrue(assets.exists("RTS"));
				Asset asset = (Asset) arg;
				asset.addObserver(observer2);
			}
		});
		observer2.update(isA(AssetImpl.class), eq(Asset.EVENT_CLEARING));
		expectLastCall().andDelegateTo(new Observer(){
			@Override
			public void update(Observable o, Object arg) {
				try {
					assertSame(o, assets.getByCode("RTS"));
					assertAsset("RTS", "SPBFUT", 5.0d, 0, 140000d, 140000d,
							3.22d, 11200d);
				} catch ( Exception e ) {
					fail("Unexpected exception: " + e);
				}
			}
		});
		assets.addObserver(observer);
		control.replay();
		
		assets.onTable(new Table(cells, "foo", "RXCX", 7));
		
		control.verify();
	}
	
	@Test
	public void testOnTable_Assets_UpdateExsisting() throws Exception {
		Object cells1[] = {
			// code, class, step, scale, est. price, step money, init. money
			"RTS", "SPBFUT", 0.01d, 2.0d, 200d, 1d, 10d, 
		};
		assets.onTable(new Table(cells1, "foo", "RXCX", 7));
		AssetImpl asset = (AssetImpl) assets.getByCode("RTS");
		observer.update(asset, Asset.EVENT_CLEARING);
		expectLastCall().andDelegateTo(new Observer(){
			@Override
			public void update(Observable o, Object arg) {
				try {
					assertAsset("RTS", "SPBFUT", 0.01d, 2, 200d, 100d, 2d, 20d);
				} catch ( Exception e ) {
					fail("Unexpected exception: " + e);
				}
			}
		});
		asset.addObserver(observer);
		control.replay();
		
		Object cells2[] = {
			"RTS", "KJZ000", 5.0d, 0.0d, 100d, 2d, 20d, 
		};
		assets.onTable(new Table(cells2, "foo", "RXCX", 7));
		
		control.verify();
	}
	
	@Test
	public void testOnTable_Deals_IgnoreTableWithNe6Cols() throws Exception {
		Object cells[] = { "one", "two" };
		assets.onTable(new Table(cells, "bar", "RXCX", 2));
	}
	
	@Test
	public void testOnTable_Deals_UpdateExistingAssets() throws Exception {
		assets.onTable(new Table(new Object[]{
			"RTS", "S", 0.01d, 2.0d, 100d, 1.5d, 15d
			}, "foo", "RXCX", 7));
		AssetImpl asset = (AssetImpl) assets.getByCode("RTS");
		observer.update(asset, Asset.EVENT_PRICE);
		expectLastCall().andDelegateTo(new Observer(){
			@Override
			public void update(Observable o, Object arg) {
				try {
					assertAsset("RTS", "S", 0.01d, 2, 108.20d, 100d, 1.5d, 15d);
				} catch ( Exception e ) {
					fail("Unexpected exception: " + e);
				}
			}
		});
		asset.addObserver(observer);
		control.replay();
		
		Object cells[] = {
			null, null, null, "RTS",  100.00d, 1.0d,
			null, null, null, "SBER", "error", 1.0d,
			null, null, null, "RTS",  109.15d, 5.0d,
			null, null, null, "RTS",  108.20d, 1.0d,
		};
		
		assets.onTable(new Table(cells, "bar", "RXCX", 6));
		
		control.verify();
	}
	
	@Test
	public void testRegisterHandler() throws Exception {
		dispatcher.add("foo", assets);
		dispatcher.add("bar", assets);
		replay(dispatcher);
		
		assets.registerHandler(dispatcher);
		
		verify(dispatcher);
	}
	
	@Test
	public void testUnregisterException() throws Exception {
		dispatcher.remove("foo", assets);
		dispatcher.remove("bar", assets);
		replay(dispatcher);
		
		assets.unregisterHandler(dispatcher);
		
		verify(dispatcher);
	}

}
