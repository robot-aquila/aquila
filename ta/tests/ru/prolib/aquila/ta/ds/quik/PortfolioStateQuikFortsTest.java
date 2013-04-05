package ru.prolib.aquila.ta.ds.quik;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.PortfolioState;
import ru.prolib.aquila.ChaosTheory.PortfolioTimeoutException;
import ru.prolib.aquila.rxltdde.Xlt.Table;
import ru.prolib.aquila.util.LateNotify;
import ru.prolib.aquila.util.LateNotifyAction;

public class PortfolioStateQuikFortsTest {
	IMocksControl control;
	PortfolioStateQuikForts state;
	AssetImpl asset;
	Observer observer;
	
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
		//Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		asset = new AssetImpl("RIZ1", "SPBFUT", 5, 0);
		state = new PortfolioStateQuikForts("JZZ000",
				asset, "position", "limits");
	}

	/**
	 * Установить текущий размер позиции и вариационку
	 * @param expected
	 */
	final private void setState(int expected, double margin) {
		Object[] cells = { "JZZ000", "RIZ1", new Double(expected), margin };
		final Table table = new Table(cells, "position", "RXCX", 4);
		state.onTable(table);
		assertEquals(expected, state.getPosition());
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertEquals("JZZ000", state.getAccount());
		assertSame(asset, state.getAsset());
		assertEquals("position", state.getPositionTableName());
		assertEquals("limits", state.getLimitsTableName());
		assertEquals(0.0d, state.getVariationMargin(), 0.01d);
	}
	
	@Test
	public void testGetTotalMoney() throws Exception {
		asset.updateInitialMarginMoney(100.00d);
		Object[] cells1 = { "JZZ000", "Ден.средства", 1024.00d };
		state.onTable(new Table(cells1, "limits", "N/U", 3));
		Object[] cells2 = { "JZZ000", "RIZ1", -1.0d, -10.12d };
		state.onTable(new Table(cells2, "position", "R1C1:R1C1", 3));
		
		assertEquals(1024.00d, state.getMoney(), 0.01d);
		assertEquals(-10.12d, state.getVariationMargin(), 0.01d);
		assertEquals(100.00d, state.getInitialMargin(), 0.01d);
		assertEquals(1113.88d, state.getTotalMoney(), 0.01d);
	}

	@Test
	public void testOnTable_LimitsTable() throws Exception {
		Object[] cells = {
			// Торговый счет, Тип лимита, Планируемая чистая позиция
			"KKKKKK", "Ден.средства", new Double(100500.00d),
			"JZZ000", "Клиринговые",  new Double(123456.00d),
			"JZZ000", "Ден.средства", new Double(654321.00d),
			"ZZZZZZ", "Ден.средства", new Double(111555.00d),
		};
		observer.update(state, null);
		expectLastCall().andDelegateTo(new Observer(){
			@Override
			public void update(Observable o, Object arg) {
				PortfolioState state = (PortfolioState) o;
				try {
					assertEquals(654321.00d, state.getMoney(), 0.01d);
				} catch ( PortfolioException e ) {
					System.err.println("Unhandled error: " + e);
				}
			}
		});
		state.addObserver(observer);
		control.replay();
		
		Table table = new Table(cells, "limits", "R1C1:R1C1", 3);
		state.onTable(table);
		
		control.verify();
		assertEquals(654321.00d, state.getMoney(), 0.01d);
	}
	
	@Test
	public void testOnTable_PositionTable() throws Exception {
		Object[] cells = {
			// Торговый счет, Код инструмента, Тек. чистая позиция, Вар.марж
			"KKKKKK", "RIZ1", new Double(   1.0d),  10.10d,
			"JZZ000", "SBER", new Double(-100.0d), 100.20d,
			"KKKKKK", "GAZP", new Double(   0.0d),  20.35d,
			"JZZ000", "RIZ1", new Double(  -1.0d), -10.12d,
			"JZZ000", "RIZ2", new Double(   0.0d), 800.15d,
		};
		observer.update(state, null);
		expectLastCall().andDelegateTo(new Observer(){
			@Override
			public void update(Observable o, Object arg) {
				PortfolioState state = (PortfolioState) o;
				try {
					assertEquals(-1, state.getPosition());
					assertEquals(-10.12d, state.getVariationMargin(), 0.001d);
				} catch ( PortfolioException e ) {
					fail("Unhandled error: " + e);
				}
			}
		});
		state.addObserver(observer);
		control.replay();
		
		Table table = new Table(cells, "position", "R1C1:R1C1", 3);
		state.onTable(table);

		control.verify();
		assertEquals(-1, state.getPosition());
	}
	
	@Test
	public void testRegisterHandler() throws Exception {
		RXltDdeDispatcher dispatcher = control.createMock(RXltDdeDispatcher.class);

		dispatcher.add("limits", state);
		dispatcher.add("position", state);
		replay(dispatcher);
		
		state.registerHandler(dispatcher);
		
		verify(dispatcher);
	}
	
	@Test
	public void testUnregisterHandler() throws Exception {
		RXltDdeDispatcher dispatcher = control.createMock(RXltDdeDispatcher.class);
		
		dispatcher.remove("limits", state);
		dispatcher.remove("position", state);
		replay(dispatcher);
		
		state.unregisterHandler(dispatcher);
		
		verify(dispatcher);
	}

	@Test
	public void testWaitForNeutralPosition2_Ok() throws Exception {
		setState(-1, 100.0d);
		new LateNotify(state, 5, 100, new LateNotifyAction(){
			@Override
			public void execute() throws Exception {
				setState(0, 0.0d);
			}}).start();

		state.waitForNeutralPosition(750);
		
		assertEquals(0, state.getPosition());
	}

	@Test
	public void testWaitForNeutralPosition2_Timeout() throws Exception {
		setState(-1, 0.0d);

		try {
			state.waitForNeutralPosition(500);
			fail("Expected exception: " + PortfolioTimeoutException.class);
		} catch ( PortfolioTimeoutException e ) {
			
		}
	}
	
	@Test
	public void testGetInitialMargin() throws Exception {
		asset.updateInitialMarginMoney(100.00d);
		setState(0, 0.0d);
		assertEquals(0.0d, state.getInitialMargin(), 0.01d);
		setState(-5, 0.0d);
		assertEquals(500.00d, state.getInitialMargin(), 0.01d);
		setState(10, 0.0d);
		assertEquals(1000.0d, state.getInitialMargin(), 0.01d);
	}
	
}
