package ru.prolib.aquila.datatools.storage.dao;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.datatools.storage.dao.SecurityPropertiesRepositoryImpl;
import ru.prolib.aquila.datatools.storage.dao.RepositoryObjectNotFoundException;
import ru.prolib.aquila.datatools.storage.dao.SymbolRepositoryImpl;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;
import ru.prolib.aquila.datatools.utils.LiquibaseTestHelper;

@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class SecurityPropertiesRepositoryImplTest
	extends AbstractTransactionalJUnit4SpringContextTests
{
	private LiquibaseTestHelper liquibaseHelper;
	private SessionFactory sessionFactory;
	private SymbolRepositoryImpl symbols;
	private SecurityPropertiesRepositoryImpl repository;
	private SecurityPropertiesEntity entity;
	private Symbol symbol;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Autowired
	public void setLiquibaseHelper(LiquibaseTestHelper liquibaseHelper) {
		this.liquibaseHelper = liquibaseHelper;
	}
	
	private void assertTime(LocalDateTime expected, LocalDateTime actual)
			throws Exception
	{
		assertEquals(expected, actual);
	}

	@Before
	public void setUp() throws Exception {
		liquibaseHelper.setUpBeforeTestClass();
		repository = new SecurityPropertiesRepositoryImpl();
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
		assertTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0, 15000000), entity.getStartingTime());
		assertTime(LocalDateTime.of(2015, 4, 15, 0, 0, 0), entity.getExpirationTime());
		assertEquals(Currency.getInstance("RUB"), entity.getCurrencyOfCost());
	}
	
	@Test (expected=ObjectNotFoundException.class)
	public void testGetById_ThrowsIfNotExists() throws Exception {
		repository.getById(1199282L);
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<SecurityPropertiesEntity> list = repository.getAll();
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals(new Long(15), list.get(0).getId());
		assertEquals(new Long(16), list.get(1).getId());
	}
	
	@Test
	public void testGetBySymbolEntity() throws Exception {
		SymbolEntity symbol = symbols.getById(1002L);
		entity = repository.getBySymbolEntity(symbol);
		assertNotNull(entity);
		assertEquals(new Long(16), entity.getId());
		assertEquals(new Long(1002), entity.getSymbol().getId());
		assertEquals("Si", entity.getDisplayName());
		assertTime(LocalDateTime.of(2012, 12, 31, 23, 59, 59, 999000000), entity.getStartingTime());
		assertTime(LocalDateTime.of(2015, 6, 16, 0, 0, 0), entity.getExpirationTime());
		assertEquals(Currency.getInstance("RUB"), entity.getCurrencyOfCost());
	}
	
	@Test (expected=RepositoryObjectNotFoundException.class)
	public void testGetBySymbolEntity_ThrowsIfNotExists() throws Exception {
		SymbolEntity symbol = symbols.getById(1003L);
		repository.getBySymbolEntity(symbol);
	}

	@Test
	public void testGetBySymbol() throws Exception {
		symbol = new Symbol("RTS-6.15", "SPBFUT", "USD", SymbolType.FUTURE);
		entity = repository.getBySymbol(symbol);
		assertNotNull(entity);
		assertEquals(new Long(15), entity.getId());
		assertEquals(new Long(1001), entity.getSymbol().getId());
		assertEquals("RTS", entity.getDisplayName());
		assertTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0, 15000000), entity.getStartingTime());
		assertTime(LocalDateTime.of(2015, 4, 15, 0, 0, 0), entity.getExpirationTime());
		assertEquals(Currency.getInstance("RUB"), entity.getCurrencyOfCost());
	}
	
	@Test (expected=RepositoryObjectNotFoundException.class)
	public void testGetBySymbol_ThrowsIfNotExists() {
		symbol = new Symbol("XXX", "YYY", "RUB", SymbolType.CURRENCY);
		repository.getBySymbol(symbol);
	}
	
	@Test
	public void testCreate() throws Exception {
		entity = repository.createEntity();
		assertNotNull(entity);
		assertNotSame(entity, repository.createEntity());
	}
	
	@Test
	public void testSave_NewEntity() throws Exception {
		entity = repository.createEntity();
		entity.setCurrencyOfCost(Currency.getInstance("EUR"));
		entity.setDisplayName("Zulu24");
		entity.setStartingTime(LocalDateTime.of(2010, 1, 1, 0, 0, 0, 0));
		entity.setExpirationTime(LocalDateTime.of(2015, 04, 15, 13, 45, 10, 540000000));
		entity.setSymbol(symbols.getById(1003L));
		
		repository.save(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(new Long(17), entity.getId());
		assertEquals(1, super.countRowsInTableWhere("security_properties",
				"id=17 AND symbol=1003 AND display_name='Zulu24' AND " +
				"starting_time='2010-01-01 00:00:00.000' AND " +
				"expiration_time='2015-04-15 13:45:10.540' AND " +
				"currency_of_cost='EUR'"));
	}
	
	@Test
	public void testSave_ExistingEntity() throws Exception {
		entity = repository.getById(16L);
		entity.setCurrencyOfCost(Currency.getInstance("EUR"));
		entity.setDisplayName("Zuko");
		entity.setStartingTime(LocalDateTime.of(1999, 6, 1, 0, 0, 0, 1000000));
		entity.setExpirationTime(LocalDateTime.of(2000, 1, 1, 23, 59, 59, 999000000));
		entity.setSymbol(symbols.getById(1003L));
		
		repository.save(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(1, super.countRowsInTableWhere("security_properties",
				"id=16 AND symbol=1003 AND display_name='Zuko' AND " +
				"starting_time='1999-06-01 00:00:00.001' AND " +
				"expiration_time='2000-01-01 23:59:59.999' AND " +
				"currency_of_cost='EUR'"));
		assertEquals(0, super.countRowsInTableWhere("security_properties",
				"id=16 AND symbol=1002 AND display_name='Si' AND " +
				"starting_time='2012-12-31 23:59:59.999' AND " +
				"expiration_time='2015-06-16 00:00:00.000' AND " +
				"currency_of_cost='RUB'"));
	}
	
	@Test
	public void testDelete() throws Exception {
		entity = repository.getById(16L);
		assertNotNull(entity);

		repository.delete(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(0, super.countRowsInTableWhere("security_properties",
				"id=16"));
	}
	
	@Test
	public void testGetSymbol() throws Exception {
		entity = repository.getById(16L);
		Symbol expected, actual;
		expected = new Symbol("Si-6.15", "SPBFUT", "RUB", SymbolType.FUTURE);
		actual = entity.getSymbolInfo();
		assertEquals(expected, actual);
	}

}
