/**
 * 
 */
package ru.prolib.aquila.ta.ds.jdbc;

import java.sql.Connection;

public interface DbAccessor extends Cloneable {
	
	public Connection get() throws DbException;
	
	public Connection reconnect() throws DbException;
	
}