package ru.prolib.aquila.ta.ds.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ta.ds.DataSetException;
import ru.prolib.aquila.ta.ds.DataSetIterator;

/**
 * Адаптер набора данных к результатам выборки JDBC ResultSet.
 */
public class DataSetIteratorJdbc implements DataSetIterator {
	private static final Logger logger = LoggerFactory.getLogger(DataSetIteratorJdbc.class);
	private final ResultSet rs;
	
	public DataSetIteratorJdbc(ResultSet rs) {
		super();
		this.rs = rs;
	}
	
	public ResultSet getResultSet() {
		return rs;
	}

	@Override
	public Double getDouble(String name) throws DataSetException {
		try {
			return rs.getDouble(name);
		} catch ( SQLException e ) {
			throw new DataSetIteratorJdbcException(e);
		}
	}

	@Override
	public String getString(String name) throws DataSetException {
		try {
			return rs.getString(name);
		} catch ( SQLException e ) {
			throw new DataSetIteratorJdbcException(e);
		}
	}

	@Override
	public Date getDate(String name) throws DataSetException {
		try {
			return rs.getTimestamp(name);
		} catch ( SQLException e ) {
			throw new DataSetIteratorJdbcException(e);
		}
	}
	
	@Override
	public Long getLong(String name) throws DataSetException {
		try {
			return rs.getLong(name);
		} catch ( SQLException e ) {
			throw new DataSetIteratorJdbcException(e);
		}
	}

	@Override
	public boolean next() throws DataSetException {
		try {
			return rs.next();
		} catch ( SQLException e ) {
			throw new DataSetIteratorJdbcException(e);
		}
	}
	
	@Override
	public void close() {
		closeResultSet(rs);
	}

	static public void closeResultSet(ResultSet rs) {
		try {
			rs.close();
		} catch ( SQLException e ) {
			Object args[] = null;
			if ( logger.isDebugEnabled() ) {
				args = new Object[] { e.getMessage(), e };
			} else {
				args = new Object[] { e.getMessage() };
			}
			logger.warn("Error closing SQL resource: {}", args);
		}
	}

}
