package ru.prolib.aquila.datatools.storage.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.type.TimestampType;
import org.hibernate.usertype.UserType;

/**
 * Joda DateTime type adaptor for Hibernate.
 */
public class DateTimeType implements UserType {
	
	public DateTimeType() {
		super();
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException
	{
		return cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x != null ? x.equals(y) : y == null;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x != null ? x.hashCode() : 0;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException
	{
		assert names.length == 1;
		Date time = (Date) TimestampType.INSTANCE.nullSafeGet(rs, names[0]);
		if ( time == null ) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		LocalDateTime javaTime = time == null ? null :
			LocalDateTime.of(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, // Jan = 0
				cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND),
				cal.get(Calendar.MILLISECOND) * 1000000);
		//System.err.println(">>> INFO: java Date is " + time);
		//System.err.println(">>> INFO: joda DateTime is " + jodaTime);
		return javaTime;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException
	{
		if ( value == null ) {
			TimestampType.INSTANCE.nullSafeSet(st, null, index);
			return;
		}
		LocalDateTime javaTime = (LocalDateTime) value;
		Calendar cal = Calendar.getInstance();
		cal.set(javaTime.getYear(),
				javaTime.getMonth().getValue() - 1,
				javaTime.getDayOfMonth(),
				javaTime.getHour(),
				javaTime.getMinute(),
				javaTime.getSecond());
		cal.set(Calendar.MILLISECOND, javaTime.getNano() / 1000000);
		Date time = cal.getTime();
		//System.err.println(">>> INFO: java Date is " + time);
		//System.err.println(">>> INFO: joda DateTime is " + jodaTime);
		TimestampType.INSTANCE.nullSafeSet(st, time, index);
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException
	{
		return original;
	}

	@Override
	public Class<?> returnedClass() {
		return LocalDateTime.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { TimestampType.INSTANCE.sqlType() };
	}

}
