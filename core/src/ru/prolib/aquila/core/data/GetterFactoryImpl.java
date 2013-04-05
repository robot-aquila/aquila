package ru.prolib.aquila.core.data;

import java.util.Date;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.getter.GAccount;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderFactory;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderResolverStd;
import ru.prolib.aquila.core.data.getter.GPrice;
import ru.prolib.aquila.core.data.getter.GSecurityDescr;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorEq;

/**
 * Фабрика геттеров ряда.
 * <p>
 * 2012-10-19<br>
 * $Id: GetterFactoryImpl.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class GetterFactoryImpl implements GetterFactory {
	
	/**
	 * Конструктор.
	 */
	public GetterFactoryImpl() {
		super();
	}

	@Override
	public G<Long> rowLong(String column) {
		return new GRowObj<Long>(column, new GLong());
	}

	@Override
	public G<Double> rowDouble(String column) {
		return new GRowObj<Double>(column, new GDouble());
	}

	@Override
	public G<Integer> rowInteger(String column) {
		return new GRowObj<Integer>(column, new GInteger());
	}

	@Override
	public G<String> rowString(String column) {
		return new GRowObj<String>(column, new GString());
	}

	@Override
	public G<Object> rowObject(String column) {
		return new GRowObj<Object>(column);
	}

	@Override
	public G<SecurityDescriptor> rowSecurityDescr(String secCode,
			String colClassCode, String valCurrCode, SecurityType valSecType)
	{
		return new GSecurityDescr(rowString(secCode),
								  rowString(colClassCode),
								  constString(valCurrCode),
								  new GConst<SecurityType>(valSecType));
	}

	@Override
	public G<Date> rowDate(String date, String time,
			String dateFormat, String timeFormat)
	{
		return new GDate2E(rowString(date), rowString(time),
				dateFormat, timeFormat);
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public G<OrderDirection>
			rowOrderDir(String column, Object buyEquivalent)
	{
		return new GCond<OrderDirection>(
				new GValidator(new GRowObj(column),
							   new ValidatorEq(buyEquivalent)),
				new GConst<OrderDirection>(OrderDirection.BUY),
				new GConst<OrderDirection>(OrderDirection.SELL)
		);
	}

	@Override
	public G<Account> rowAccount(String code) {
		return new GAccount(new GRowObj<String>(code, new GString()));
	}
	
	@Override
	public G<Account> rowAccount(String code, String subCode) {
		return new GAccount(new GRowObj<String>(code, new GString()),
				new GRowObj<String>(subCode, new GString()));
	}

	@Override
	public G<String> constString(String value) {
		return new GConst<String>(value);
	}

	@Override
	public G<Object> constObject(Object value) {
		return new GConst<Object>(value);
	}

	@Override
	public G<Portfolio> portfolio(Portfolios portfolios, G<Account> gAccount) {
		return new GPortfolio(gAccount, portfolios);
	}

	@Override
	public G<Security>
			security(Securities securities, G<SecurityDescriptor> gDescr)
	{
		return new GSecurity(gDescr, securities);
	}

	@Override
	public G<Portfolio> rowPortfolio(Portfolios portfolios, String code) {
		return portfolio(portfolios, rowAccount(code));
	}
	
	@Override
	public G<Portfolio> rowPortfolio(Portfolios portfolios,
			String code, String subCode)
	{
		return portfolio(portfolios, rowAccount(code, subCode));
	}

	@Override
	public G<Security> rowSecurity(Securities securities, String colCode,
			String colClassCode, String valCurrCode, SecurityType valSecType)
	{
		return security(securities, rowSecurityDescr(colCode, colClassCode,
				valCurrCode, valSecType));
	}

	@Override
	public G<Price>
			rowPrice(String price, String unit, Map<?, PriceUnit> map)
	{
		return new GPrice(rowDouble(price),
				new GMapTR<PriceUnit>(rowObject(unit), map));
	}

	@Override
	public G<Integer> condInteger(Validator validator,
			G<Integer> onValid, G<Integer> onInvalid)
	{
		return new GCond<Integer>(validator, onValid, onInvalid);
	}

	@Override
	public G<Integer>
			condInteger(Validator validator, int onValid, int onInvalid)
	{
		return condInteger(validator,
				new GConst<Integer>(onValid),
				new GConst<Integer>(onInvalid));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public G<Integer> rowCondInteger(String column,
			Object expected, int onEquals, int onNotEquals)
	{
		return condInteger(new GValidator(new GRowObj(column),
										  new ValidatorEq(expected)),
				onEquals, onNotEquals);
	}

	@Override
	public G<EditableOrder> rowOrder(EditableOrders orders,
			OrderFactory factory, String transId, String id)
	{
		return new GOrder(new OrderResolverStd(orders, factory),
				rowLong(transId), rowLong(id));
	}

	@Override
	public G<Date> rowDate(String column) {
		return new GRowObj<Date>(column, new GDate());
	}

}
