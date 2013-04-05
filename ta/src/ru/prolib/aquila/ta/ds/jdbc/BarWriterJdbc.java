package ru.prolib.aquila.ta.ds.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.ds.BarWriter;
import ru.prolib.aquila.ta.ds.BarWriterException;

public class BarWriterJdbc implements BarWriter {
	private final DbAccessor dba;
	private final String tableName;
	
	public BarWriterJdbc(DbAccessor dba, String tableName) {
		this.dba = dba;
		this.tableName = tableName;
	}
	
	public DbAccessor getConnectionAccessor() {
		return dba;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	@Override
	public boolean addBar(Candle bar) throws BarWriterException {
		try {
			PreparedStatement sth = dba.get().prepareStatement("INSERT INTO "
				+ tableName
				+ "(period_time,open,high,low,close,volume) VALUES "
				+ "(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
				+ "period_time=?,open=?,high=?,low=?,close=?,volume=?");
			Timestamp t = new Timestamp(bar.getTime().getTime());
			sth.setTimestamp(1, t);
			sth.setDouble(2, bar.getOpen());
			sth.setDouble(3, bar.getHigh());
			sth.setDouble(4, bar.getLow());
			sth.setDouble(5, bar.getClose());
			sth.setLong(6,   bar.getVolume());
			sth.setTimestamp(7, t);
			sth.setDouble(8,  bar.getOpen());
			sth.setDouble(9,  bar.getHigh());
			sth.setDouble(10, bar.getLow());
			sth.setDouble(11, bar.getClose());
			sth.setLong(12,	  bar.getVolume());
			sth.executeUpdate();
			sth.close();
			return true;
		} catch ( SQLException e ) {
			throw new BarWriterException(e.getMessage(), e);
		} catch ( DbException e ) {
			throw new BarWriterException(e.getMessage(), e);
		}
	}

	@Override
	public boolean flush() throws BarWriterException {
		return false;
	}
	
}