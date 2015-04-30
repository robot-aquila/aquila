package ru.prolib.aquila.probe.storage.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.probe.storage.model.SymbolEntity;
import ru.prolib.aquila.probe.utils.LiquibaseTestHelper;

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
		SecurityDescriptor expected, actual;
		expected = new SecurityDescriptor("RTS-6.15", "SPBFUT", "USD", SecurityType.FUT);
		actual = x.getDescriptor();
		assertEquals(expected, actual);
	}
	
	@Test (expected=ObjectNotFoundException.class)
	public void testGetById_ThrowsIfNotExists() throws Exception {
		repository.getById(65535L);
	}
	
	@Test
	public void testGetByDescriptor() {
		SecurityDescriptor descr, actual;
		descr = new SecurityDescriptor("Si-6.15", "SPBFUT", "RUB", SecurityType.FUT);
		SymbolEntity x = repository.getByDescriptor(descr);
		assertNotNull(x);
		assertEquals(new Long(1002), x.getId());
		actual = x.getDescriptor();
		assertEquals(descr, actual);
	}
	
	@Test
	public void testGetByDescriptor_CreatesNewIfNotExists() throws Exception {
		SecurityDescriptor descr;
		descr = new SecurityDescriptor("XXX", "YYY", "EUR", SecurityType.OPT);
		SymbolEntity x = repository.getByDescriptor(descr);
		sessionFactory.getCurrentSession().flush();
		
		Long expectedId = new Long(1004);
		assertNotNull(x);
		assertEquals(expectedId, x.getId());
		assertEquals(1, super.countRowsInTableWhere("symbols",
			"id=1004 AND code='XXX' AND class_code='YYY' AND currency='EUR' AND type='OPT'"));
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
