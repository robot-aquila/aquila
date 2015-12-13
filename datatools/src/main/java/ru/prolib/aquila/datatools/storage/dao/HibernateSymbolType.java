package ru.prolib.aquila.datatools.storage.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import org.hibernate.HibernateException;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;

public class HibernateSymbolType implements UserType {
	
	public HibernateSymbolType() {
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
		return Symbol.class;
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
		return new Symbol(code, classCode, currencyCode, typeCode);
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
			final Symbol symbol = (Symbol) value;
			x.set(st, symbol.getCode(), index);
			x.set(st, symbol.getExchangeID(), index + 1);
			x.set(st, symbol.getCurrencyCode(), index + 2);
			x.set(st,  symbol.getTypeCode(), index  +3);
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
