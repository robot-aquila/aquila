package ru.prolib.aquila.datatools.storage.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public class SymbolEntityTest {
	private SecurityDescriptor descr;
	private SymbolEntity entity;

	@Before
	public void setUp() throws Exception {
		descr = new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		entity = new SymbolEntity();
	}

	@Test
	public void testCtor_Defaults() {
		assertNull(entity.getId());
		assertNull(entity.getDescriptor());
	}
	
	@Test
	public void testSettersAndGetters() throws Exception {
		entity.setId(815L);
		entity.setDescriptor(descr);
		
		assertEquals(new Long(815), entity.getId());
		assertSame(descr, entity.getDescriptor());
	}

}
