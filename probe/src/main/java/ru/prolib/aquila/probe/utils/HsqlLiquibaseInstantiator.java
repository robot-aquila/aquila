package ru.prolib.aquila.probe.utils;

import javax.sql.DataSource;

import liquibase.Liquibase;
import liquibase.database.jvm.HsqlConnection;
import liquibase.resource.*;

public class HsqlLiquibaseInstantiator implements LiquibaseInstantiator {
	private Liquibase liquibase;
	
	public HsqlLiquibaseInstantiator(DataSource dataSource, String changelog) {
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

}
