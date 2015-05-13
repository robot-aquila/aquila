package ru.prolib.aquila.probe.storage.dao;

import static org.junit.Assert.*;

import java.util.*;
import org.hibernate.*;
import org.joda.time.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.probe.storage.model.*;
import ru.prolib.aquila.probe.utils.LiquibaseTestHelper;

@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class ConstantSecurityPropertiesRepositoryImplTest
	extends AbstractTransactionalJUnit4SpringContextTests
{
	private LiquibaseTestHelper liquibaseHelper;
	private SessionFactory sessionFactory;
	private SymbolRepositoryImpl symbols;
	private ConstantSecurityPropertiesRepositoryImpl repository;
	private ConstantSecurityPropertiesEntity entity;
	private SecurityDescriptor descr;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Autowired
	public void setLiquibaseHelper(LiquibaseTestHelper liquibaseHelper) {
		this.liquibaseHelper = liquibaseHelper;
	}
	
	private void assertTime(DateTime expected, DateTime actual)
			throws Exception
	{
		assertEquals(expected, actual);
	}

	@Before
	public void setUp() throws Exception {
		liquibaseHelper.setUpBeforeTestClass();
		repository = new ConstantSecurityPropertiesRepositoryImpl();
		repository.setSessionFactory(sessionFactory);
		symbols = new SymbolRepositoryImpl();
		symbols.setSessionFactory(sessionFactory);
	}
	
	@Test
	public void testGetById() throws Exception {
		entity = repository.getById(15L);
		assertNotNull(entity);
		assertEquals(new Long(15), entity.getId());
		SymbolEntity symbol = entity.getSymbol();
		assertNotNull(symbol);
		assertEquals(new Long(1001L), symbol.getId());
		assertEquals("RTS", entity.getDisplayName());
		assertTime(new DateTime(2015, 4, 15, 0, 0, 0), entity.getExpirationTime());
		assertEquals(Currency.getInstance("RUB"), entity.getCurrencyOfCost());
	}
	
	@Test (expected=ObjectNotFoundException.class)
	public void testGetById_ThrowsIfNotExists() throws Exception {
		repository.getById(1199282L);
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<ConstantSecurityPropertiesEntity> list = repository.getAll();
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals(new Long(15), list.get(0).getId());
		assertEquals(new Long(16), list.get(1).getId());
	}
	
	@Test
	public void testGetBySymbol() throws Exception {
		SymbolEntity symbol = symbols.getById(1002L);
		entity = repository.getBySymbol(symbol);
		assertNotNull(entity);
		assertEquals(new Long(16), entity.getId());
		assertEquals(new Long(1002), entity.getSymbol().getId());
		assertEquals("Si", entity.getDisplayName());
		assertTime(new DateTime(2015, 6, 16, 0, 0, 0), entity.getExpirationTime());
		assertEquals(Currency.getInstance("RUB"), entity.getCurrencyOfCost());
	}
	
	@Test (expected=RepositoryObjectNotFoundException.class)
	public void testGetBySymbol_ThrowsIfNotExists() throws Exception {
		SymbolEntity symbol = symbols.getById(1003L);
		repository.getBySymbol(symbol);
	}

	@Test
	public void testGetByDescirptor() throws Exception {
		descr = new SecurityDescriptor("RTS-6.15", "SPBFUT", "USD", SecurityType.FUT);
		entity = repository.getByDescriptor(descr);
		assertNotNull(entity);
		assertEquals(new Long(15), entity.getId());
		assertEquals(new Long(1001), entity.getSymbol().getId());
		assertEquals("RTS", entity.getDisplayName());
		assertTime(new DateTime(2015, 4, 15, 0, 0, 0), entity.getExpirationTime());
		assertEquals(Currency.getInstance("RUB"), entity.getCurrencyOfCost());
	}
	
	@Test (expected=RepositoryObjectNotFoundException.class)
	public void testGetByDescirptor_ThrowsIfNotExists() {
		descr = new SecurityDescriptor("XXX", "YYY", "RUB", SecurityType.CASH);
		repository.getByDescriptor(descr);
	}
	
	@Test
	public void testCreate() throws Exception {
		entity = repository.createEntity();
		assertNotNull(entity);
		assertNotSame(entity, repository.createEntity());
	}
	
	@Test
	public void testUpdate_NewEntity() throws Exception {
		entity = repository.createEntity();
		entity.setCurrencyOfCost(Currency.getInstance("EUR"));
		entity.setDisplayName("Zulu24");
		entity.setExpirationTime(new DateTime(2015, 04, 15, 13, 45, 10, 540));
		entity.setSymbol(symbols.getById(1003L));
		
		repository.update(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(new Long(17), entity.getId());
		assertEquals(1, super.countRowsInTableWhere("constant_security_properties",
				"id=17 AND symbol=1003 AND display_name='Zulu24' AND " +
				"expiration_time='2015-04-15 13:45:10.540' AND " +
				"currency_of_cost='EUR'"));
	}
	
	@Test
	public void testUpdate_ExistingEntity() throws Exception {
		entity = repository.getById(16L);
		entity.setCurrencyOfCost(Currency.getInstance("EUR"));
		entity.setDisplayName("Zuko");
		entity.setExpirationTime(new DateTime(2000, 1, 1, 23, 59, 59, 999));
		entity.setSymbol(symbols.getById(1003L));
		
		repository.update(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(1, super.countRowsInTableWhere("constant_security_properties",
				"id=16 AND symbol=1003 AND display_name='Zuko' AND " +
				"expiration_time='2000-01-01 23:59:59.999' AND " +
				"currency_of_cost='EUR'"));
		assertEquals(0, super.countRowsInTableWhere("constant_security_properties",
				"id=16 AND symbol=1002 AND display_name='Si' AND " +
				"expiration_time='2015-06-16 00:00:00.000' AND " +
				"currency_of_cost='RUB'"));
	}
	
	@Test
	public void testDelete() throws Exception {
		entity = repository.getById(16L);
		assertNotNull(entity);

		repository.delete(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(0, super.countRowsInTableWhere("constant_security_properties",
				"id=16"));
	}
	
	@Test
	public void testGetEntitySecurityDescriptor() throws Exception {
		entity = repository.getById(16L);
		SecurityDescriptor expected, actual;
		expected = new SecurityDescriptor("Si-6.15", "SPBFUT", "RUB", SecurityType.FUT);
		actual = entity.getSecurityDescriptor();
		assertEquals(expected, actual);
	}

}
