/**
 * 
 */
package ru.prolib.aquila.ta.ds.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbAccessorImpl implements DbAccessor {
	public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
	private final String driverClass;
	private final String url;
	private final String user;
	private final String password;
	
	protected Connection conn;
	
	public DbAccessorImpl(String driverClass,
			String url, String user, String pass)
	{
		super();
		this.driverClass = driverClass;
		this.url = url;
		this.user = user;
		this.password = pass;
	}
	
	public DbAccessorImpl() {
		this(DEFAULT_DRIVER, null, null, null);
	}

	public DbAccessorImpl(String url) {
		this(DEFAULT_DRIVER, url, null, null);
	}
	
	public DbAccessorImpl(String url, String user, String pass) {
		this(DEFAULT_DRIVER, url, user, pass);
	}
	
	@Override
	public Connection get() throws DbException {
		try {
			return (conn == null || ! conn.isValid(0)) ? reconnect() : conn;
		} catch ( SQLException e ) {
			throw new DbException(e.getMessage(), e);
		}
	}

	@Override
	public Connection reconnect() throws DbException {
		try {
			if ( conn != null ) {
				conn.close();
			}
			Class.forName(driverClass);
			if ( user == null ) {
				conn = getConnection1(url);
			} else {
				conn = getConnection3(url, user, password);
			}
			return conn;
		} catch ( ClassNotFoundException e ) {
			throw new DbException("Driver not found: " + driverClass);
		} catch ( SQLException e ) {
			throw new DbException(e.getMessage(), e);
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		DbAccessorImpl newObject = (DbAccessorImpl) super.clone();
		newObject.conn = null;
		return newObject;
	}
	
	protected Connection getConnection1(String url) throws SQLException {
		return DriverManager.getConnection(url);
	}
	
	protected Connection getConnection3(String url, String user, String pass)
		throws SQLException
	{
		return DriverManager.getConnection(url, user, pass);
	}
	
}