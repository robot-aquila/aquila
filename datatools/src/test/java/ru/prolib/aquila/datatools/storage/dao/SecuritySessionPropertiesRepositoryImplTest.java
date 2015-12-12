package ru.prolib.aquila.datatools.storage.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ru.prolib.aquila.datatools.storage.dao.SymbolRepositoryImpl;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;
import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;
import ru.prolib.aquila.datatools.utils.LiquibaseTestHelper;

@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class SecuritySessionPropertiesRepositoryImplTest
	extends AbstractTransactionalJUnit4SpringContextTests
{
	private LiquibaseTestHelper liquibaseHelper;
	private SessionFactory sessionFactory;
	private SymbolRepositoryImpl symbols;
	private SecuritySessionPropertiesRepositoryImpl repository;
	private SecuritySessionPropertiesEntity entity;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Autowired
	public void setLiquibaseHelper(LiquibaseTestHelper liquibaseHelper) {
		this.liquibaseHelper = liquibaseHelper;
	}
	
	@Before
	public void setUp() throws Exception {
		liquibaseHelper.setUpBeforeTestClass();
		repository = new SecuritySessionPropertiesRepositoryImpl();
		repository.setSessionFactory(sessionFactory);
		symbols = new SymbolRepositoryImpl();
		symbols.setSessionFactory(sessionFactory);
	}
	
	@Test
	public void testGetById() throws Exception {
		entity = repository.getById(1L);
		assertNotNull(entity);
		assertEquals(new Long(1), entity.getId());
		SymbolEntity symbol = entity.getSymbol();
		assertNotNull(symbol);
		assertEquals(new Long(1001L), symbol.getId());
		assertEquals(new Integer(0), entity.getScale());
		assertEquals(10.2924, entity.getTickCost(), 0.0001);
		assertEquals(22897.86, entity.getInitialMarginCost(), 0.01);
		assertEquals(101970.0, entity.getInitialPrice(), 0.01);
		assertEquals(93460.0, entity.getLowerPriceLimit(), 0.01);
		assertEquals(110840.0, entity.getUpperPriceLimit(), 0.01);
		assertEquals(new Integer(1), entity.getLotSize());
		assertEquals(10.0, entity.getTickSize(), 0.1);
		assertEquals(new DateTime(2015, 4, 30, 10, 0, 0), entity.getSnapshotTime());
		assertEquals(new DateTime(2015, 4, 30, 18, 45, 0), entity.getClearingTime());
	}
	
	@Test (expected=ObjectNotFoundException.class)
	public void testGetById_ThrowsIfNotExists() throws Exception {
		repository.getById(1199282L);
	}
	
	@Test
	public void testGetAll() throws Exception {
		List<SecuritySessionPropertiesEntity> list = repository.getAll();
		assertNotNull(list);
		assertEquals(4, list.size());
		assertEquals(repository.getById(3L), list.get(0));
		assertEquals(repository.getById(4L), list.get(1));
		assertEquals(repository.getById(1L), list.get(2));
		assertEquals(repository.getById(2L), list.get(3));
	}
	
	@Test
	public void testSave_ExistingEntry() throws Exception {
		entity = repository.getById(2L);
		assertNotNull(entity);
		entity.setSymbol(symbols.getById(1002L));
		entity.setScale(1);
		entity.setTickCost(5.1534);
		entity.setInitialMarginCost(21500.0);
		entity.setInitialPrice(100250.0);
		entity.setLowerPriceLimit(50000.0);
		entity.setUpperPriceLimit(130000.0);
		entity.setLotSize(1);
		entity.setTickSize(20.0);
		entity.setSnapshotTime(new DateTime(1999, 12, 31, 23, 59, 59, 999));
		entity.setClearingTime(new DateTime(2000, 1, 1, 0, 0, 0, 0));
		
		repository.save(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(1, super.countRowsInTableWhere("security_session_properties",
			"id=2 AND symbol=1002 AND scale=1 AND tick_cost=5.1534 AND " +
			"initial_margin_cost=21500.0 AND initial_price=100250.0 AND " +
			"lower_price_limit=50000.0 AND upper_price_limit=130000.0 AND " +
			"lot_size=1 AND tick_size=20.0 AND " +
			"snapshot_time='1999-12-31 23:59:59.999' AND " +
			"clearing_time='2000-01-01 00:00:00.000'"));
		assertEquals(0, super.countRowsInTableWhere("security_session_properties",
			"id=2 AND symbol=1001 AND scale=2 AND tick_cost=10.2415 AND " +
			"initial_margin_cost=22621.45 AND initial_price=101800.0 AND " +
			"lower_price_limit=92000.0 AND upper_price_limit=130000.0 AND " +
			"lot_size=2 AND tick_size=10 AND " +
			"snapshot_time='2015-05-01 10:00:00' AND " +
			"clearing_time='2015-05-01 18:45:00'"));
	}
	
	@Test
	public void testSave_NewEntry() throws Exception {
		entity = repository.createEntity();
		assertNotNull(entity);
		entity.setSymbol(symbols.getById(1001L));
		entity.setScale(15);
		entity.setTickCost(1d);
		entity.setInitialMarginCost(2.0);
		entity.setInitialPrice(3.0);
		entity.setLowerPriceLimit(500.0);
		entity.setUpperPriceLimit(1500.0);
		entity.setLotSize(5);
		entity.setTickSize(0.1);
		entity.setSnapshotTime(new DateTime(1998, 1, 1, 15, 30, 45, 150));
		entity.setClearingTime(new DateTime(1999, 1, 1, 18, 45, 0, 0));
		
		repository.save(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(new Long(5), entity.getId());
		assertEquals(1, super.countRowsInTableWhere("security_session_properties",
				"id=5 AND symbol=1001 AND scale=15 AND tick_cost=1.0 AND " +
				"initial_margin_cost=2.0 AND initial_price=3.0 AND " +
				"lower_price_limit=500.0 AND upper_price_limit=1500.0 AND " +
				"lot_size=5 AND tick_size=0.1 AND " +
				"snapshot_time='1998-01-01 15:30:45.150' AND " +
				"clearing_time='1999-01-01 18:45:00.000'"));
	}

	@Test
	public void testDelete() throws Exception {
		entity = repository.getById(3L);
		assertNotNull(entity);
		
		repository.delete(entity);
		sessionFactory.getCurrentSession().flush();
		
		assertEquals(0, super.countRowsInTableWhere("security_session_properties",
				"id=3"));
	}

}
