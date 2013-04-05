/**
 * 
 */
package ru.prolib.aquila.ta.ds.jdbc;

public class DbException extends Exception {
	private static final long serialVersionUID = 5605598678724924087L;
	
	public DbException() {
		super();
	}
	
	public DbException(String msg) {
		super(msg);
	}
	
	public DbException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public DbException(Throwable t) {
		super(t);
	}
	
}