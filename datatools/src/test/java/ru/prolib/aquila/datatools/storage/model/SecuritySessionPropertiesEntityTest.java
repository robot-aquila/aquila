package ru.prolib.aquila.datatools.storage.model;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;
import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;

public class SecuritySessionPropertiesEntityTest {
	private Symbol symbol;
	private SymbolEntity symbolEntity;
	private SecuritySessionPropertiesEntity entity;

	@Before
	public void setUp() throws Exception {
		symbol = new Symbol("SPY", "ARCA", "USD", SymbolType.FUTURE);
		symbolEntity = new SymbolEntity();
		symbolEntity.setSymbol(symbol);
		entity = new SecuritySessionPropertiesEntity();
	}
	
	@Test
	public void testCtor_Defaults() {
		assertNull(entity.getId());
		assertNull(entity.getSymbol());
		assertNull(entity.getSymbolInfo());
		assertNull(entity.getScale());
		assertNull(entity.getTickCost());
		assertNull(entity.getInitialMarginCost());
		assertNull(entity.getInitialPrice());
		assertNull(entity.getLowerPriceLimit());
		assertNull(entity.getUpperPriceLimit());
		assertNull(entity.getLotSize());
		assertNull(entity.getTickSize());
		assertNull(entity.getSnapshotTime());
		assertNull(entity.getClearingTime());
	}
	
	@Test
	public void testSettersAndGetters() throws Exception {
		entity.setId(280L);
		entity.setSymbol(symbolEntity);
		entity.setScale(2);
		entity.setTickCost(12.34);
		entity.setInitialMarginCost(22897.86);
		entity.setInitialPrice(102310d);
		entity.setLowerPriceLimit(90000d);
		entity.setUpperPriceLimit(110000d);
		entity.setLotSize(1);
		entity.setTickSize(10d);
		entity.setSnapshotTime(LocalDateTime.of(2001, 1, 10, 20, 30, 0, 954));
		entity.setClearingTime(LocalDateTime.of(2001, 1, 11, 18, 45, 0, 0));
		
		assertEquals(new Long(280), entity.getId());
		assertSame(symbolEntity, entity.getSymbol());
		assertEquals(new Integer(2), entity.getScale());
		assertEquals(new Double(12.34), entity.getTickCost(), 0.01);
		assertEquals(new Double(22897.86), entity.getInitialMarginCost(), 0.01);
		assertEquals(new Double(102310.0), entity.getInitialPrice(), 0.1);
		assertEquals(new Double(90000.0), entity.getLowerPriceLimit(), 0.1);
		assertEquals(new Double(110000.0), entity.getUpperPriceLimit(), 0.1);
		assertEquals(new Integer(1), entity.getLotSize());
		assertEquals(new Double(10.0), entity.getTickSize(), 0.1);
		assertEquals(LocalDateTime.of(2001, 1, 10, 20, 30, 0, 954), entity.getSnapshotTime());
		assertEquals(LocalDateTime.of(2001, 1, 11, 18, 45, 0, 0), entity.getClearingTime());
	}
	
	@Test
	public void testGetSymbol() throws Exception {
		entity.setSymbol(symbolEntity);
		assertSame(symbol, entity.getSymbolInfo());
	}

}
