package ru.prolib.aquila.ChaosTheory;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ta.TestValue;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReader;
import ru.prolib.aquila.ta.ds.MarketDataReaderFake;

public class AssetLocalTest {
	IMocksControl control;
	MarketData md;
	AssetLocal asset;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		md = control.createMock(MarketData.class);
		asset = new AssetLocal("RIH2", "SPBFUT", 5, 0, 0.25d, 0.15d);
	}
	
	@Test
	public void testImmutables() throws Exception {
		assertEquals("RIH2", asset.getAssetCode());
		assertEquals("SPBFUT", asset.getClassCode());
		assertEquals(5.0d, asset.getPriceStep(), 0.01d);
		assertEquals(0, asset.getPriceScale());
		assertEquals(0.25d, asset.getInitialMarginFactor(), 0.01d);
		assertEquals(0.15d, asset.getPriceStepMoney(), 0.01d);
	}
	
	@Test
	public void testStartService_Ok() throws Exception {
		md.addObserver(asset);
		control.replay();
		
		asset.startService(md);
		
		control.verify();
		assertSame(md, asset.data);
	}
	
	@Test (expected=AssetException.class)
	public void testStartService_ThrowsIfStarted() throws Exception {
		asset.data = md;
		control.replay();
		
		asset.startService(md);
	}
	
	@Test
	public void testStopService_Ok() throws Exception {
		asset.data = md;
		md.deleteObserver(asset);
		control.replay();
		
		asset.stopService();
		
		control.verify();
		assertNull(asset.data);
	}
	
	@Test
	public void testStopService_NoneIfNotStarted() throws Exception {
		control.replay();
		
		asset.stopService();
		
		control.verify();
	}
	
	public static class CheckClearing implements Observer {
		final double estimatedPrice;
		final double initialMarginMoney;
		final double priceStepMoney;
		
		public CheckClearing(double estimated, double margin, double step) {
			super();
			estimatedPrice = estimated;
			initialMarginMoney = margin;
			priceStepMoney = step;
		}

		@Override
		public void update(Observable o, Object arg) {
			Asset asset = (Asset)o;
			try {
				assertEquals(estimatedPrice, asset.getEstimatedPrice(), 0.0001d);
				assertEquals(initialMarginMoney,
						asset.getInitialMarginMoney(), 0.0001d);
				assertEquals(priceStepMoney, asset.getPriceStepMoney(), 0.0001d);
			} catch ( Exception e ) {
				fail("Unhandled exception: " + e);
			}			
		}
		
	}
	
	public static class CheckPrice implements Observer {
		final double price;
		
		public CheckPrice(double price) {
			super();
			this.price = price;
		}

		@Override
		public void update(Observable o, Object arg) {
			Asset asset = (Asset)o;
			try {
				assertEquals(price, asset.getPrice(), 0.0001d);
			} catch ( Exception e ) {
				fail("Unhandled exception: " + e);
			}
		}
		
	}
	
	@Test
	public void testUpdate() throws Exception {
		MarketDataReader reader = new MarketDataReaderFake(1);
		final MarketDataImpl data = new MarketDataImpl(reader);
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date[] = {
				df.parse("2011-01-01 00:00:00"),
				df.parse("2011-01-01 01:00:00"),
				df.parse("2011-01-02 00:00:01"),
				df.parse("2011-01-02 00:00:02"),
				df.parse("2011-01-02 23:59:59"),
				df.parse("2011-01-03 00:00:00"),
		};
		Double open[]  = { 100d, 105d, 110d, 115d, 105d, 108d };
		Double close[] = { 105d, 110d, 115d, 105d, 108d, 112d };
		data.addValue(new TestValue<Date>(MarketData.TIME, date));
		data.addValue(new TestValue<Double>(MarketData.OPEN, open));
		data.addValue(new TestValue<Double>(MarketData.CLOSE, close));
		asset.startService(data);

		Observer observer = control.createMock(Observer.class);
		asset.addObserver(observer);
		
		// 1. Первое обновление данных
		// Первое уведомление приводит к перерасчету клиринговых параметров
		observer.update(asset, Asset.EVENT_CLEARING);
		// ГО = 100 * 0.25 * 0.15 / 5 = 0.75$
		expectLastCall().andDelegateTo(new CheckClearing(100d, 0.75d, 0.15d));
		// Второе уведомление это обновление цены актива
		observer.update(asset, Asset.EVENT_PRICE);
		expectLastCall().andDelegateTo(new CheckPrice(105d));
		
		// 2. Второе обновление данных
		// Дата не изменилась -> клиринговые не пересчитываются
		// Единственное уведомление - обновление цены актива
		observer.update(asset, Asset.EVENT_PRICE);
		expectLastCall().andDelegateTo(new CheckPrice(110d));
		
		// 3. Третье обновление данных
		// Изменение даты -> первое уведомление это пересчет клиринговых
		observer.update(asset, Asset.EVENT_CLEARING);
		// ГО = 110 * 0.25 * 0.15 / 5 = 0.825
		expectLastCall().andDelegateTo(new CheckClearing(110d, 0.83d, 0.15d));
		// Второе уведомление обновление цены
		observer.update(asset, Asset.EVENT_PRICE);
		expectLastCall().andDelegateTo(new CheckPrice(115d));
		
		// 4. Четвертое обновление данных
		observer.update(asset, Asset.EVENT_PRICE);
		expectLastCall().andDelegateTo(new CheckPrice(105d));
		
		// 5.
		observer.update(asset, Asset.EVENT_PRICE);
		expectLastCall().andDelegateTo(new CheckPrice(108d));
		
		// 6.
		observer.update(asset, Asset.EVENT_CLEARING);
		// ГО = 108 * 0.25 * 0.15 / 5 = 
		expectLastCall().andDelegateTo(new CheckClearing(108d, 0.81d, 0.15d));
		observer.update(asset, Asset.EVENT_PRICE);
		expectLastCall().andDelegateTo(new CheckPrice(112d));
		
		control.replay();
		
		data.update();
		data.update();
		data.update();
		data.update();
		data.update();
		data.update();
		
		control.verify();
	}

}
