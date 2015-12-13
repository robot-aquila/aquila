package ru.prolib.aquila.datatools.storage.moex;

import static org.junit.Assert.*;

import java.util.Currency;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public class MOEXUtilsTest {
	private static final Symbol symbol1, symbol2, symbol3;
	
	static {
		symbol1 = new Symbol("RTS", "SPBFUT", "USD", SymbolType.FUTURE);
		symbol2 = new Symbol("XXX", "SPBFUT", "RUR", SymbolType.OPTION);
		symbol3 = new Symbol("SPY", "SPBFUT", "USD", SymbolType.STOCK);
	}
	
	private EditableTerminal terminal;
	private EditableSecurity security;
	private MOEXUtils moexUtils;

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder().buildTerminal();
		security = terminal.getEditableSecurity(symbol1);
		moexUtils = new MOEXUtils();
	}

	@Test
	public void testFillProperties() throws Exception {
		SecurityPropertiesEntity p = new SecurityPropertiesEntity();
		security.setDisplayName("RTS-12.15");
		
		moexUtils.fillProperties(security, p);
		
		assertEquals(Currency.getInstance("USD"), p.getCurrencyOfCost());
		assertEquals("RTS-12.15", p.getDisplayName());
		assertNull(p.getExpirationTime());
		assertNull(p.getId());
		assertNull(p.getStartingTime());
		assertNull(p.getSymbol());
	}
	
	@Test
	public void testFillSessionProperties() throws Exception {
		SecuritySessionPropertiesEntity p = new SecuritySessionPropertiesEntity();
		security.setInitialMargin(1215.34d);
		security.setInitialPrice(86.90d);
		security.setLotSize(10);
		security.setMaxPrice(95.0d);
		security.setMinPrice(75.0d);
		security.setPrecision(2);
		security.setMinStepPrice(2.75d);
		security.setMinStepSize(0.01d);
		
		moexUtils.fillSessionProperties(security, p);

		assertNull(p.getId());
		assertNull(p.getClearingTime());
		assertEquals(1215.34d, p.getInitialMarginCost(), 0.01d);
		assertEquals(86.90d, p.getInitialPrice(), 0.01d);
		assertEquals(10, (int)p.getLotSize());
		assertEquals(95.0d, p.getUpperPriceLimit(), 0.01d);
		assertEquals(75.0d, p.getLowerPriceLimit(), 0.01d);
		assertEquals(2, (int)p.getScale());
		assertNull(p.getSnapshotTime());
		assertEquals(2.75d, p.getTickCost(), 0.01d);
		assertEquals(0.01d, p.getTickSize(), 0.01d);
	}
	
	@Test
	public void testIsPropertiesEquals() throws Exception {
		SecuritySessionPropertiesEntity p1 = new SecuritySessionPropertiesEntity();
		p1.setInitialMarginCost(475.12d);
		p1.setInitialPrice(86.15d);
		p1.setLotSize(5);
		p1.setUpperPriceLimit(90.12d);
		p1.setLowerPriceLimit(82.72d);
		p1.setScale(2);
		p1.setTickCost(2.75d);
		p1.setTickSize(0.01d);
		// unused in comparison
		p1.setClearingTime(new DateTime(2013, 9, 11, 15, 34, 0));
		p1.setId(800L);
		p1.setSnapshotTime(new DateTime(2015, 10, 6, 12, 35, 0));
		p1.setSymbol(new SymbolEntity());
		
		Variant<Double> initMargin = new Variant<Double>()
				.add(475.12d).add(299.76d);
		Variant<Double> initPrice = new Variant<Double>(initMargin)
				.add(86.15d).add(82.14d);
		Variant<Integer> lotSize = new Variant<Integer>(initPrice)
				.add(5).add(10);
		Variant<Double> upperPrice = new Variant<Double>(lotSize)
				.add(90.12d).add(100.14d);
		Variant<Double> lowerPrice = new Variant<Double>(upperPrice)
				.add(82.72d).add(80.13d);
		Variant<Integer> scale = new Variant<Integer>(lowerPrice)
				.add(2).add(4);
		Variant<Double> tickCost = new Variant<Double>(scale)
				.add(2.75d).add(5.15d);
		Variant<Double> tickSize = new Variant<Double>(tickCost)
				.add(0.01d).add(0.5d);
		Variant<?> iterator = tickSize;
		SecuritySessionPropertiesEntity x, found = null;
		int foundCnt = 0;
		do {
			x = new SecuritySessionPropertiesEntity();
			x.setInitialMarginCost(initMargin.get());
			x.setInitialPrice(initPrice.get());
			x.setLotSize(lotSize.get());
			x.setUpperPriceLimit(upperPrice.get());
			x.setLowerPriceLimit(lowerPrice.get());
			x.setScale(scale.get());
			x.setTickCost(tickCost.get());
			x.setTickSize(tickSize.get());
			if ( moexUtils.isPropertiesEquals(p1, x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(475.12d, found.getInitialMarginCost(), 0.01d);
		assertEquals(86.15d, found.getInitialPrice(), 0.01d);
		assertEquals(5, (int)found.getLotSize());
		assertEquals(90.12d, found.getUpperPriceLimit(), 0.01d);
		assertEquals(82.72d, found.getLowerPriceLimit(), 0.01d);
		assertEquals(2.75d, found.getTickCost(), 0.01d);
		assertEquals(0.01d, found.getTickSize(), 0.01d);
		assertNull(found.getClearingTime());
		assertNull(found.getId());
		assertNull(found.getSnapshotTime());
		assertNull(found.getSymbol());
	}
	
	@Test
	public void testGetClearingTime() throws Exception {
		DateTime initial = new DateTime(2014, 10, 1, 10, 15, 0);
		DateTime expected = new DateTime(2014, 10, 1, 18, 45, 0);
		assertEquals(expected, moexUtils.getClearingTime(symbol1, initial));
		assertEquals(expected, moexUtils.getClearingTime(symbol2, initial));
		assertEquals(expected, moexUtils.getClearingTime(symbol3, initial));
		
		initial = new DateTime(2009, 11, 15, 19, 10, 0);
		expected = new DateTime(2009, 11, 15, 23, 50, 0);
		assertEquals(expected, moexUtils.getClearingTime(symbol1, initial));
		assertEquals(expected, moexUtils.getClearingTime(symbol2, initial));
		
		expected = new DateTime(2009, 11, 16, 18, 45, 0);
		assertEquals(expected, moexUtils.getClearingTime(symbol3, initial));
	}

}
