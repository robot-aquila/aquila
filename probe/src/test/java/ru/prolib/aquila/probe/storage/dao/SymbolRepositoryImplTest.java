package ru.prolib.aquila.probe.storage.dao;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.probe.storage.model.SymbolEntity;
import ru.prolib.aquila.probe.utils.LiquibaseTestHelper;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@TransactionConfiguration(transactionManager="transactionManager")
public class SymbolRepositoryImplTest {
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
		SecurityDescriptor descr, actual;
		descr = new SecurityDescriptor("XXX", "YYY", "EUR", SecurityType.OPT);
		SymbolEntity x = repository.getByDescriptor(descr);
		assertNotNull(x);
		assertThat(x.getId(), not(equalTo(0l)));
		actual = x.getDescriptor();
		assertSame(descr, actual);
	}
	
	@Test
	public void testGetAll() throws Exception {
		liquibaseHelper.setUpBeforeTestClass();
		
		List<SymbolEntity> list = repository.getAll();
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals(new Long(1001), list.get(0).getId());
		assertEquals(new Long(1002), list.get(1).getId());
		assertEquals(new Long(1003), list.get(2).getId());
	}

}
