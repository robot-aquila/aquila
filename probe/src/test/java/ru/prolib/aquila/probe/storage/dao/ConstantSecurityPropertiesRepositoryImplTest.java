package ru.prolib.aquila.probe.storage.dao;

import static org.junit.Assert.*;

import java.util.Currency;
import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.joda.time.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.probe.storage.model.ConstantSecurityPropertiesEntity;
import ru.prolib.aquila.probe.storage.model.SymbolEntity;
import ru.prolib.aquila.probe.utils.LiquibaseTestHelper;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
public class ConstantSecurityPropertiesRepositoryImplTest {
	static private boolean firstCall = true;
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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		firstCall = true;
	}
	
	private void assertTime(DateTime expected, DateTime actual)
			throws Exception
	{
		// TODO: fixme
		assertEquals(expected, actual.minusHours(1));
	}

	@Before
	public void setUp() throws Exception {
		if ( firstCall ) {
			liquibaseHelper.setUpBeforeTestClass();
			firstCall = false;
		}
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
		assertEquals(Currency.getInstance("USD"), entity.getCurrencyOfCost());
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
		assertEquals(Currency.getInstance("USD"), entity.getCurrencyOfCost());
	}
	
	@Test (expected=RepositoryObjectNotFoundException.class)
	public void testGetByDescirptor_ThrowsIfNotExists() {
		descr = new SecurityDescriptor("XXX", "YYY", "RUB", SecurityType.CASH);
		repository.getByDescriptor(descr);
	}

}
