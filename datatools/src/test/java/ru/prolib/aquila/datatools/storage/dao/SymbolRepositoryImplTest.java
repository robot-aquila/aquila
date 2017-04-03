package ru.prolib.aquila.datatools.storage.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.datatools.storage.dao.SymbolRepositoryImpl;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;
import ru.prolib.aquila.datatools.utils.LiquibaseTestHelper;

@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class SymbolRepositoryImplTest
	extends AbstractTransactionalJUnit4SpringContextTests
{
	static private boolean firstCall = true;
	private LiquibaseTestHelper liquibaseHelper;
	private SessionFactory sessionFactory;
	private SymbolRepositoryImpl repository;
	
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

	@Before
	public void setUp() throws Exception {
		if ( firstCall ) {
			liquibaseHelper.setUpBeforeTestClass();
			firstCall = false;
		}
		repository = new SymbolRepositoryImpl();
		repository.setSessionFactory(sessionFactory);
	}

	@Test
	public void testGetById() throws Exception {
		SymbolEntity x = repository.getById(1001L);
		assertNotNull(x);
		assertEquals(new Long(1001), x.getId());
		Symbol expected, actual;
		expected = new Symbol("RTS-6.15", "SPBFUT", "USD", SymbolType.FUTURES);
		actual = x.getSymbol();
		assertEquals(expected, actual);
	}
	
	@Test (expected=ObjectNotFoundException.class)
	public void testGetById_ThrowsIfNotExists() throws Exception {
		repository.getById(65535L);
	}
	
	@Test
	public void testGetBySymbol() {
		Symbol expected, actual;
		expected = new Symbol("Si-6.15", "SPBFUT", "RUB", SymbolType.FUTURES);
		SymbolEntity x = repository.getBySymbol(expected);
		assertNotNull(x);
		assertEquals(new Long(1002), x.getId());
		actual = x.getSymbol();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetBySymbol_CreatesNewIfNotExists() throws Exception {
		Symbol symbol = new Symbol("XXX", "YYY", "EUR", SymbolType.OPTION);
		
		SymbolEntity x = repository.getBySymbol(symbol);
		sessionFactory.getCurrentSession().flush();
		
		Long expectedId = new Long(1004);
		assertNotNull(x);
		assertEquals(expectedId, x.getId());
		assertEquals(1, super.countRowsInTableWhere("symbols",
			"id=1004 AND code='XXX' AND exchange_id='YYY' AND currency_code='EUR' AND type_code='O'"));
	}
	
	@Test
	public void testGetAll() throws Exception {
		liquibaseHelper.setUpBeforeTestClass();
		
		List<SymbolEntity> list = repository.getAll();
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals(repository.getById(1001L), list.get(0));
		assertEquals(repository.getById(1002L), list.get(1));
		assertEquals(repository.getById(1003L), list.get(2));
	}

}
