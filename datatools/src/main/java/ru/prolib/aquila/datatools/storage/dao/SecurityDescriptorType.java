package ru.prolib.aquila.datatools.storage.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import org.hibernate.HibernateException;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;

public class SecurityDescriptorType implements UserType {
	
	public SecurityDescriptorType() {
		super();
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
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

	@Override
	public Class<?> returnedClass() {
		return SecurityDescriptor.class;
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException
	{
		return cached;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException
	{
		assert names.length == 4;
		StringType x = StringType.INSTANCE;
		String code = (String) x.get(rs, names[0]),
				classCode = (String) x.get(rs, names[1]),
				currencyCode = (String) x.get(rs, names[2]),
				typeCode = (String)x.get(rs, names[3]);
		if ( code == null ) {
			throw new HibernateException("Code cannot be null");
		}
		if ( classCode == null ) {
			throw new HibernateException("Class code cannot be null");
		}
		if ( currencyCode == null ) {
			throw new HibernateException("Currency code cannot be null");
		}
		if ( typeCode == null ) {
			throw new HibernateException("Type code cannot be null");
		}
		Currency currency;
		try {
			currency = Currency.getInstance(currencyCode);
		} catch ( IllegalArgumentException e ) {
			throw new HibernateException("Wrong currency code: "
					+ currencyCode, e);
		}
		SecurityType type;
		try {
			type = SecurityType.valueOf(typeCode);
		} catch ( IllegalArgumentException e ) {
			throw new HibernateException("Wrong security type: " + typeCode, e);
		}
		return new SecurityDescriptor(code, classCode, currency, type);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException
	{
		StringType x = StringType.INSTANCE;
		if ( value == null ) {
			x.set(st, null, index);
			x.set(st, null, index + 1);
			x.set(st, null, index + 2);
			x.set(st, null, index + 3);
		} else {
			final SecurityDescriptor descr = (SecurityDescriptor) value;
			x.set(st, descr.getCode(), index);
			x.set(st, descr.getClassCode(), index + 1);
			x.set(st, descr.getCurrency().getCurrencyCode(), index + 2);
			x.set(st,  descr.getType().getCode(), index  +3);
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException
	{
		return original;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] {
				StringType.INSTANCE.sqlType(),
				StringType.INSTANCE.sqlType(),
				StringType.INSTANCE.sqlType(),
				StringType.INSTANCE.sqlType()
		};
	}

}
