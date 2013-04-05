package ru.prolib.aquila.ta.ds.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import ru.prolib.aquila.ta.ds.DataSetIterator;
import ru.prolib.aquila.ta.ds.MarketDataException;
import ru.prolib.aquila.ta.ds.MarketDataReader;

/**
 * Реализация доступа к данным о торгах через таблицу mysql.
 */
public class MarketDataReaderJdbc implements MarketDataReader {
	private final DbAccessor dba;
	private final String table;
	private final String column;
	private final int prepareLimit;
	private final int updateLimit;
	private Date actuality;
	
	public MarketDataReaderJdbc(DbAccessor dba, String table,
			String column, Date startTime,
			int prepareLimit, int updateLimit)
	{
		super();
		this.dba = dba;
		this.table = table;
		this.column = column;
		this.prepareLimit = prepareLimit;
		this.updateLimit = updateLimit;
		this.actuality = startTime;
	}

	/**
	 * Получить акцессор подключения с БД.
	 * @return
	 */
	public DbAccessor getDba() {
		return dba;
	}
	
	/**
	 * Получить имя таблицы-источника баров.
	 * @return
	 */
	public String getTable() {
		return table;
	}
	
	/**
	 * Получить имя колонки времени бара.
	 * @return
	 */
	public String getColumn() {
		return column;
	}
	
	public int getPrepareLimit() {
		return prepareLimit;
	}
	
	public int getUpdateLimit() {
		return updateLimit;
	}
	
	public Date getActualityPoint() {
		return actuality;
	}
	
	private ResultSet prepareQuery1() throws SQLException,DbException
	{
		PreparedStatement sth = dba.get()
			.prepareStatement("SELECT " + column +
				" FROM " + table +
				" WHERE " + column + " <= ?" +
				" ORDER BY " + column + " DESC" +
				" LIMIT ?");
		sth.setTimestamp(1, new Timestamp(actuality.getTime()));
		sth.setInt(2, prepareLimit);
		return sth.executeQuery();
	}
	
	private ResultSet prepareQuery2(Timestamp start)
		throws SQLException,DbException
	{
		PreparedStatement sth = dba.get()
			.prepareStatement("SELECT * FROM " + table +
				" WHERE " + column + " >= ? AND " + column + " <= ?" +
				" ORDER BY " + column + " ASC");
		sth.setTimestamp(1, start);
		sth.setTimestamp(2, new Timestamp(actuality.getTime()));
		return sth.executeQuery();
	}
	
	private ResultSet updateQuery() throws SQLException,DbException {
		PreparedStatement sth = dba.get()
			.prepareStatement("SELECT * FROM " + table +
				" WHERE " + column + " > ? " +
				" ORDER BY " + column +" ASC" +
				" LIMIT ?");
		sth.setTimestamp(1, new Timestamp(actuality.getTime()));
		sth.setInt(2, updateLimit);
		return sth.executeQuery();
	}

	@Override
	public synchronized DataSetIterator prepare() throws MarketDataException {
		ResultSet rs = null;
		try {
			rs = prepareQuery1();
			if ( ! rs.last() ) {
				return new DataSetIteratorJdbc(rs);
			}
		} catch ( Exception e ) {
			throw new MarketDataException(e.getMessage(), e);
		}
		try {
			return new
				DataSetIteratorJdbc(prepareQuery2(rs.getTimestamp(column)));
		} catch ( Exception e ) {
			throw new MarketDataException(e.getMessage(), e);
		} finally {
			DataSetIteratorJdbc.closeResultSet(rs);
		}
	}

	@Override
	public synchronized DataSetIterator update() throws MarketDataException {
		ResultSet rs = null;
		try {
			rs = updateQuery();
			if ( rs.last() ) {
				actuality = new Date(rs.getTimestamp(column).getTime());
				rs.beforeFirst();
			}
			return new DataSetIteratorJdbc(rs); 
		} catch ( Exception e ) {
			if ( rs != null ) {
				DataSetIteratorJdbc.closeResultSet(rs);
			}
			throw new MarketDataException(e.getMessage(), e);
		}
	}

}
