package ru.prolib.aquila.datatools.storage.moex;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
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
		Map<Integer, Object> tokens = new HashMap<Integer, Object>();
		tokens.put(SecurityField.DISPLAY_NAME, "RTS-12.15");
		security.update(tokens);
		
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
		Map<Integer, Object> tokens = new HashMap<Integer, Object>();
		tokens.put(SecurityField.INITIAL_MARGIN, 1215.34d);
		tokens.put(SecurityField.SETTLEMENT_PRICE, 86.90d);
		tokens.put(SecurityField.LOT_SIZE, 10);
		tokens.put(SecurityField.UPPER_PRICE_LIMIT, 95.0d);
		tokens.put(SecurityField.LOWER_PRICE_LIMIT, 75.0d);
		tokens.put(SecurityField.TICK_VALUE, new FMoney("2.75", "RUB"));
		tokens.put(SecurityField.TICK_SIZE, new FDecimal("0.01"));
		security.update(tokens);
		System.out.println(security.getTickSize());
		System.out.println(security.getTickValue());
		
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
		p1.setClearingTime(LocalDateTime.of(2013, 9, 11, 15, 34, 0));
		p1.setId(800L);
		p1.setSnapshotTime(LocalDateTime.of(2015, 10, 6, 12, 35, 0));
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
		LocalDateTime initial = LocalDateTime.of(2014, 10, 1, 10, 15, 0);
		LocalDateTime expected = LocalDateTime.of(2014, 10, 1, 18, 45, 0);
		assertEquals(expected, moexUtils.getClearingTime(symbol1, initial));
		assertEquals(expected, moexUtils.getClearingTime(symbol2, initial));
		assertEquals(expected, moexUtils.getClearingTime(symbol3, initial));
		
		initial = LocalDateTime.of(2009, 11, 15, 19, 10, 0);
		expected = LocalDateTime.of(2009, 11, 15, 23, 50, 0);
		assertEquals(expected, moexUtils.getClearingTime(symbol1, initial));
		assertEquals(expected, moexUtils.getClearingTime(symbol2, initial));
		
		expected = LocalDateTime.of(2009, 11, 16, 18, 45, 0);
		assertEquals(expected, moexUtils.getClearingTime(symbol3, initial));
	}

}
