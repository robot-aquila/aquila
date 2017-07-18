package ru.prolib.aquila.core.concurrency;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BusinessEntity;

public class MultilockBuilderBETest {
	
	static class BusinessEntityStub extends LockableStub implements BusinessEntity {
		@Override public void suppressEvents() { }
		@Override public void restoreEvents() { }
		@Override public void purgeEvents() { }
	}
	
	private BusinessEntityStub entityStub1, entityStub2, entityStub3;
	private MultilockBuilderBE builder;

	@Before
	public void setUp() throws Exception {
		entityStub1 = new BusinessEntityStub();
		entityStub2 = new BusinessEntityStub();
		entityStub3 = new BusinessEntityStub();
		builder = new MultilockBuilderBE();
	}

	@Test
	public void testAdd() {
		assertSame(builder, builder.add(entityStub1));
		assertSame(builder, builder.add(entityStub2));
		assertSame(builder, builder.add(entityStub3));
		
		Set<BusinessEntity> expected = new HashSet<>();
		expected.add(entityStub1);
		expected.add(entityStub2);
		expected.add(entityStub3);
		assertEquals(expected, builder.getObjects());
	}
	
	@Test
	public void testBuildMultilock() {
		builder.add(entityStub1)
			.add(entityStub2)
			.add(entityStub3);
		
		Multilock actual = builder.buildMultilock();
		Set<BusinessEntity> dummy = new HashSet<>();
		dummy.add(entityStub1);
		dummy.add(entityStub2);
		dummy.add(entityStub3);
		Multilock expected = new Multilock(actual.getLID(), dummy);		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(builder.equals(builder));
		assertFalse(builder.equals(null));
		assertFalse(builder.equals(this));
	}
	
	@Test
	public void testEquals() {
		builder.add(entityStub1)
			.add(entityStub2)
			.add(entityStub3);
		MultilockBuilderBE builder2 = new MultilockBuilderBE()
				.add(entityStub1)
				.add(entityStub2)
				.add(entityStub3),
			builder3 = new MultilockBuilderBE()
				.add(entityStub1)
				.add(entityStub2);
		assertTrue(builder.equals(builder2));
		assertFalse(builder.equals(builder3));
	}

}
