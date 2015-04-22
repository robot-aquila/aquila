package ru.prolib.aquila.probe.storage.dal;

import static org.junit.Assert.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class DataProviderImplTest {
	private DataSource dataSource;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void test_() throws Exception {
		Connection conn = dataSource.getConnection();
		System.out.println(conn);
		
		fail("Not yet implemented");
	}

}
