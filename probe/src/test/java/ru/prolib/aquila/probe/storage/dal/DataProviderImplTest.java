package ru.prolib.aquila.probe.storage.dal;

import static org.junit.Assert.*;

import java.sql.Connection;

import javax.sql.DataSource;

import liquibase.Liquibase;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.prolib.aquila.probe.utils.LiquibaseInstantiator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class DataProviderImplTest {
	private DataSource dataSource;
	private Liquibase liquibase;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Autowired
	public void setLiquibaseInstantiator(LiquibaseInstantiator instantiator) {
		liquibase = instantiator.getLiquibase();
	}

	@Before
	public void setUp() throws Exception {
		liquibase.dropAll();
		liquibase.update("test");
	}

	@Test
	public void test_() throws Exception {
		Connection conn = dataSource.getConnection();
		System.out.println(conn);
		assertTrue(true);
	}

}
