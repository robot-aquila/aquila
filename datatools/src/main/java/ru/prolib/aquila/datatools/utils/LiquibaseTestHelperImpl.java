package ru.prolib.aquila.datatools.utils;

import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.database.jvm.HsqlConnection;
import liquibase.resource.*;

public class LiquibaseTestHelperImpl implements LiquibaseTestHelper {
	private Liquibase liquibase;
	
	public LiquibaseTestHelperImpl(DataSource dataSource, String changelog) {
		super();
		try {
			liquibase = new Liquibase(changelog,
					new FileSystemResourceAccessor(),
					new HsqlConnection(dataSource.getConnection()));
		} catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.utils.LiquibaseInstantiator#getLiquibase()
	 */
	@Override
	public Liquibase getLiquibase() {
		return liquibase;
	}

	@Override
	public void cleanUpAfterTestClass() throws Exception {
		setUpBeforeTestClass();
	}

	@Override
	public void setUpBeforeTestClass() throws Exception {
		liquibase.dropAll();
		liquibase.update("test");		
	}

}
