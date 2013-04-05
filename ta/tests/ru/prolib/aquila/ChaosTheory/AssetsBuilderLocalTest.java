package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ta.ds.MarketData;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class AssetsBuilderLocalTest {
	IMocksControl control;
	AssetsBuilderLocal builder;
	MarketData data;
	ServiceLocator locator;
	HierarchicalStreamReader reader;
	ServiceBuilderHelper helper;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = new ServiceLocatorImpl();
		data = control.createMock(MarketData.class);
		reader = control.createMock(HierarchicalStreamReader.class);
		helper = control.createMock(ServiceBuilderHelper.class);
		locator.setMarketData(data);
		builder = new AssetsBuilderLocal(helper);
	}
	
	@Test
	public void testCreate() throws Exception {
		expect(helper.getString("code", reader)).andReturn("RTS");
		expect(helper.getString("class", reader)).andReturn("SPBFUT");
		expect(helper.getDouble("step", reader)).andReturn(5.0d);
		expect(helper.getInt("scale", reader)).andReturn(0);
		expect(helper.getDouble("initialMarginFactor", reader)).andReturn(0.1d);
		expect(helper.getDouble("priceStepMoney", reader)).andReturn(0.15d);
		data.addObserver(isA(AssetLocal.class));
		control.replay();
		
		AssetsImpl assets = (AssetsImpl) builder.create(locator, reader);
		AssetLocal asset = (AssetLocal) assets.getByCode("RTS");
		
		assertEquals("RTS", asset.getAssetCode());
		assertEquals("SPBFUT", asset.getClassCode());
		assertEquals(5.0d, asset.getPriceStep(), 0.01d);
		assertEquals(0, asset.getPriceScale());
		assertEquals(0.1d, asset.getInitialMarginFactor(), 0.01d);
		assertEquals(0.15d, asset.getPriceStepMoney(), 0.01d);
	}

}
