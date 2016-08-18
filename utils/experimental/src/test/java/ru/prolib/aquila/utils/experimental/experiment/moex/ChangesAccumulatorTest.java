package ru.prolib.aquila.utils.experimental.experiment.moex;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;
import ru.prolib.aquila.data.storage.DeltaUpdate;
import ru.prolib.aquila.data.storage.DeltaUpdateBuilder;
import ru.prolib.aquila.web.utils.moex.MoexContractField;

public class ChangesAccumulatorTest {
	private Set<Integer> expectedChangedTokens;
	private UpdatableStateContainer container;
	private ChangesAccumulator accumulator;
	private Map<Integer, Object> tokens;

	@Before
	public void setUp() throws Exception {
		expectedChangedTokens = new HashSet<>();
		expectedChangedTokens.add(MoexContractField.TICK_VALUE);
		expectedChangedTokens.add(MoexContractField.LOWER_PRICE_LIMIT);
		expectedChangedTokens.add(MoexContractField.UPPER_PRICE_LIMIT);
		expectedChangedTokens.add(MoexContractField.SETTLEMENT_PRICE);
		expectedChangedTokens.add(MoexContractField.INITIAL_MARGIN);
		container = new UpdatableStateContainerImpl("TEST");
		accumulator = new ChangesAccumulator(container, expectedChangedTokens);
		tokens = new HashMap<>();
	}

	@Test
	public void testAccumulate() {
		tokens.put(MoexContractField.INITIAL_MARGIN, 14269.16d);
		assertFalse(accumulator.accumulate(tokens));
		
		tokens.clear();
		tokens.put(MoexContractField.TICK_VALUE, 12.98928d);
		tokens.put(MoexContractField.LOWER_PRICE_LIMIT, 90240d);
		tokens.put(MoexContractField.UPPER_PRICE_LIMIT, 99740d);
		tokens.put(MoexContractField.FEE, 2.0d); // add this unexpected token
		assertFalse(accumulator.accumulate(tokens));
		
		tokens.clear();
		tokens.put(MoexContractField.SETTLEMENT_PRICE, 94990d);
		tokens.put(MoexContractField.TICK_VALUE, 12.92528d); // update twice
		assertTrue(accumulator.accumulate(tokens));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withTime(Instant.EPOCH)
			.withSnapshot(false)
			.withToken(MoexContractField.INITIAL_MARGIN, 14269.16d)
			.withToken(MoexContractField.TICK_VALUE, 12.92528d)
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, 90240d)
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, 99740d)
			.withToken(MoexContractField.FEE, 2.0d)
			.withToken(MoexContractField.SETTLEMENT_PRICE, 94990d)
			.buildUpdate();
		assertEquals(expected, accumulator.createDeltaUpdate(false, Instant.EPOCH));
	}
	
	@Test
	public void testAccumulate_EmptySetOfExpectedTolens_TrueWhenAnyChanged() {
		expectedChangedTokens.clear();
		accumulator = new ChangesAccumulator(container, expectedChangedTokens);
		
		assertFalse(accumulator.accumulate(tokens));
		
		tokens.put(MoexContractField.FEE, 2.0d);
		assertTrue(accumulator.accumulate(tokens));
	}
	
	@Test
	public void testCreateDeltaUpdate_CreateSnapshotUpdate() {
		tokens.put(MoexContractField.CONTRACT_DESCR, "foo");
		tokens.put(MoexContractField.SYMBOL, "bar");
		container.update(tokens);
		
		tokens.clear();
		tokens.put(MoexContractField.LOWER_PRICE_LIMIT, 90240d);
		tokens.put(MoexContractField.UPPER_PRICE_LIMIT, 99740d);
		accumulator.accumulate(tokens);
		
		Instant time = Instant.parse("2016-08-17T18:04:50Z");
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withTime(time)
			.withSnapshot(true)
			.withToken(MoexContractField.CONTRACT_DESCR, "foo")
			.withToken(MoexContractField.SYMBOL, "bar")
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, 90240d)
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, 99740d)
			.buildUpdate();
		assertEquals(expected, accumulator.createDeltaUpdate(true, time));
	}

	@Test
	public void testCreateDeltaUpdate_CreateRegularUpdate() {
		tokens.put(MoexContractField.CONTRACT_DESCR, "foo");
		tokens.put(MoexContractField.SYMBOL, "bar");
		container.update(tokens);
		
		tokens.clear();
		tokens.put(MoexContractField.LOWER_PRICE_LIMIT, 90240d);
		tokens.put(MoexContractField.UPPER_PRICE_LIMIT, 99740d);
		accumulator.accumulate(tokens);
		
		Instant time = Instant.parse("2016-08-17T18:06:00Z");
		DeltaUpdate expected = new DeltaUpdateBuilder()
			.withTime(time)
			.withSnapshot(false)
			.withToken(MoexContractField.LOWER_PRICE_LIMIT, 90240d)
			.withToken(MoexContractField.UPPER_PRICE_LIMIT, 99740d)
			.buildUpdate();
		assertEquals(expected, accumulator.createDeltaUpdate(false, time));
	}
	
	@Test
	public void testHasData() {
		assertFalse(accumulator.hasData());
		
		tokens.put(MoexContractField.TICK_VALUE, 12.98928d);
		accumulator.accumulate(tokens);
		assertTrue(accumulator.hasData());
	}
	
	@Test
	public void testHasChanges() {
		tokens.put(MoexContractField.CONTRACT_DESCR, "foo");
		tokens.put(MoexContractField.SYMBOL, "bar");
		container.update(tokens);
		
		assertFalse(accumulator.hasChanges());
		
		tokens.clear();
		tokens.put(MoexContractField.CONTRACT_DESCR, "foo");
		tokens.put(MoexContractField.SYMBOL, "bar");
		accumulator.accumulate(tokens); // the same data - no changes
		
		assertFalse(accumulator.hasChanges());
		
		tokens.clear();
		tokens.put(MoexContractField.CONTRACT_DESCR, "zulu");
		tokens.put(MoexContractField.SYMBOL, "charlie");
		accumulator.accumulate(tokens);
		
		assertTrue(accumulator.hasChanges());
	}

}
