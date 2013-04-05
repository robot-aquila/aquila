package ru.prolib.aquila.quik.subsys.row;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.PriceUnit;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.row.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.quik.subsys.*;

/**
 * Конструктор адаптеров таблиц.
 * <p>
 * 2013-02-15<br>
 * $Id$
 */
public class RowAdapters {
	public static final String DEAL_NUM = "TRADENUM";
	public static final String DEAL_DATE = "TRADEDATE";
	public static final String DEAL_TIME = "TRADETIME";
	public static final String DEAL_SEC = "SECCODE";
	public static final String DEAL_SECCLASS = "CLASSCODE";
	public static final String DEAL_PRICE = "PRICE";
	public static final String DEAL_QTY = "QTY";
	public static final String DEAL_DIR = "BUYSELL";
	public static final String DEAL_DIR_BUY = "BUY";
	public static final String DEAL_DIR_SELL = "SELL";
	public static final String DEAL_VALUE = "VALUE";
	/**
	 * Список обязательных полей таблицы "Таблица всех сделок".
	 */
	private static final String[] DEAL_REQUIRED_FIELDS = {
		DEAL_NUM,
		DEAL_DATE,
		DEAL_TIME,
		DEAL_SEC,
		DEAL_SECCLASS,
		DEAL_PRICE,
		DEAL_QTY,
		DEAL_DIR,
		DEAL_VALUE,
	};
	
	public static final String PORT_FIRM = "FIRMID";
	public static final String PORT_CODE = "CLIENTCODE";
	public static final String PORT_CASH = "TOTALMONEYBAL";
	public static final String PORT_BALANCE = "ALLASSETS";
	public static final String PORT_VMARGIN = "PROFITLOSS";
	/**
	 * Список обязательных полей таблицы "Клиентский портфель"
	 * (портфель по бумагам).
	 */
	private static final String[] PORT_REQUIRED_FIELDS = {
		PORT_FIRM,
		PORT_CODE,
		PORT_CASH,
		PORT_BALANCE,
		PORT_VMARGIN,
	};
	
	public static final String PORTF_FIRM = "FIRMID";
	public static final String PORTF_CODE = "TRDACCID";
	public static final String PORTF_CASH = "CBPLPLANNED";
	public static final String PORTF_BALANCE = "CBPLIMIT";
	public static final String PORTF_VMARGIN = "VARMARGIN";
	public static final String PORTF_TYPE = "LIMIT_TYPE";
	public static final String PORTF_TYPE_MONEY = "Ден.средства";
	/**
	 * Список обязательных полей таблицы "Ограничения по клиентским счетам
	 * (фьючерсы)" (портфели по деривативам).
	 */
	private static final String[] PORTF_REQUIRED_FIELDS = {
		PORTF_FIRM,
		PORTF_CODE,
		PORTF_CASH,
		PORTF_BALANCE,
		PORTF_VMARGIN,
		PORTF_TYPE,
	};
	
	public static final String POS_FIRM = "FIRMID";
	public static final String POS_PORTFOLIO = "CLIENT_CODE";
	public static final String POS_ACCOUNT = "TRDACCID";
	public static final String POS_SEC_NAME = "SEC_SHORT_NAME";
	public static final String POS_OPEN = "OPENBAL";
	public static final String POS_CURR = "CURRENTBAL";
	public static final String POS_LOCK = "LOCKED";
	/**
	 * Список обязаельных полей "Таблицы лимитов по бумагам"
	 * (позиции по бумагам). 
	 */
	private static final String[] POS_REQUIRED_FIELDS = {
		POS_FIRM,
		POS_PORTFOLIO,
		POS_ACCOUNT,
		POS_SEC_NAME,
		POS_OPEN,
		POS_CURR,
		POS_LOCK,
	};
	
	public static final String POSF_FIRM = "FIRMID";
	public static final String POSF_ACCOUNT = "TRDACCID";
	public static final String POSF_SEC_NAME = "SEC_SHORT_NAME";
	public static final String POSF_OPEN = "START_NET";
	public static final String POSF_CURR = "TOTAL_NET";
	public static final String POSF_VMARGIN = "VARMARGIN";
	/**
	 * Список обязательных полей таблицы "Позиции по клиентским счетам
	 * (фьючерсы)" (позиции по деривативам).
	 */
	private static final String[] POSF_REQUIRED_FIELDS = {
		POSF_FIRM,
		POSF_ACCOUNT,
		POSF_SEC_NAME,
		POSF_OPEN,
		POSF_CURR,
		POSF_VMARGIN,
	};
	
	public static final String ORD_ID = "ORDERNUM";
	public static final String ORD_TRANS_ID = "TRANSID";
	public static final String ORD_STATUS = "STATUS";
	public static final String ORD_SEC = "SECCODE";
	public static final String ORD_SECCLASS = "CLASSCODE";
	public static final String ORD_ACCOUNT = "ACCOUNT";
	public static final String ORD_PORTFOLIO = "CLIENTCODE";
	public static final String ORD_DIR = "BUYSELL";
	public static final String ORD_QTY = "QTY";
	public static final String ORD_QTY_REST = "BALANCE";
	public static final String ORD_PRICE = "PRICE";
	public static final String ORD_DATE = "ORDERDATE";
	public static final String ORD_TIME = "ORDERTIME";
	public static final String ORD_CHNGDATE = "WITHDRAW_DATE";
	public static final String ORD_CHNGTIME = "WITHDRAW_TIME";
	public static final String ORD_TYPE = "MODE";
	/**
	 * Карта сопоставления строкового статуса заявки в таблице заявок и объекта
	 * статуса.
	 */
	public static final Map<String, OrderStatus> ORD_STATUS_MAP;
	/**
	 * Карта сопоставления строкового представления направления заявки в
	 * таблице заявок и объекта направления заявки.
	 */
	public static final Map<String, OrderDirection> ORD_DIR_MAP;
	/**
	 * Карта сопоставления строкового представления типа заявки в объект типа
	 * заявки.
	 */
	public static final Map<String, OrderType> ORD_TYPE_MAP;
	
	/**
	 * Список обязательных полей таблицы "Заявки".
	 */
	private static final String[] ORD_REQUIRED_FIELDS = {
		ORD_ID,
		ORD_TRANS_ID,
		ORD_STATUS,
		ORD_SEC,
		ORD_SECCLASS,
		ORD_ACCOUNT,
		ORD_PORTFOLIO,
		ORD_DIR,
		ORD_QTY,
		ORD_QTY_REST,
		ORD_PRICE,
		ORD_DATE,
		ORD_TIME,
		ORD_CHNGDATE,
		ORD_CHNGTIME,
		ORD_TYPE,
	};
	
	public static final String ORDS_ID = "STOP_ORDERNUM";
	public static final String ORDS_TRANS_ID = "TRANSID";
	public static final String ORDS_STATUS = "STATUS";
	public static final String ORDS_SEC = "SECCODE";
	public static final String ORDS_SECCLASS = "CLASSCODE";
	public static final String ORDS_ACCOUNT = "ACCOUNT";
	public static final String ORDS_PORTFOLIO = "CLIENTCODE";
	public static final String ORDS_DIR = "BUYSELL";
	public static final String ORDS_QTY = "QTY";
	public static final String ORDS_PRICE = "PRICE";
	public static final String ORDS_TYPE = "STOP_ORDERKIND";
	public static final String ORDS_LINKED_ID = "LINKED_ORDER";
	public static final String ORDS_PRICE1 = "CONDITION_PRICE";
	public static final String ORDS_PRICE2 = "CONDITION_PRICE2";
	public static final String ORDS_OFFSET = "OFFSET";
	public static final String ORDS_OFFSET_UNITS = "OFFSET_UNITS";
	public static final String ORDS_SPREAD = "SPREAD";
	public static final String ORDS_SPREAD_UNITS = "SPREAD_UNITS";
	public static final String ORDS_DATE = "STOP_ORDERDATE";
	public static final String ORDS_TIME = "STOP_ORDERTIME";
	
	public static final String ORDS_TYPE_SL = "Стоп-лимит";
	public static final String ORDS_TYPE_TP = "Тэйк-профит";
	public static final String ORDS_TYPE_SLTP = "Тэйк-профит и стоп-лимит";

	/**
	 * Карта соответствия для извлечения из ряда типа стоп-заявки в зависимости
	 * от строкового представления типа в формате передачи из таблицы QUIK.
	 */
	public static final Map<String, OrderType> ORDS_TYPE_MAP;
	public static final Map<String, PriceUnit> ORDS_UNIT_MAP;
	public static final Map<String, OrderDirection> ORDS_DIR_MAP;
	public static final Map<String, OrderStatus> ORDS_STATUS_MAP;
	
	private static final String[] ORDS_REQUIRED_FIELDS = {
		ORDS_ID,
		ORDS_TRANS_ID,
		ORDS_STATUS,
		ORDS_SEC,
		ORDS_SECCLASS,
		ORDS_ACCOUNT,
		ORDS_PORTFOLIO,
		ORDS_DIR,
		ORDS_QTY,
		ORDS_PRICE,
		ORDS_TYPE,
		ORDS_LINKED_ID,
		ORDS_PRICE1,
		ORDS_PRICE2,
		ORDS_OFFSET,
		ORDS_OFFSET_UNITS,
		ORDS_SPREAD,
		ORDS_SPREAD_UNITS,
		ORDS_DATE,
		ORDS_TIME,
	};
	
	public static final String SEC_LOT_SIZE = "lotsize";
	public static final String SEC_PRICE_MAX = "pricemax";
	public static final String SEC_PRICE_MIN = "pricemin";
	public static final String SEC_STEP_PRICE = "steppricet";
	public static final String SEC_PRICE_STEP = "SEC_PRICE_STEP";
	public static final String SEC_SCALE = "SEC_SCALE";
	public static final String SEC_CODE = "CODE";
	public static final String SEC_CLASS = "CLASS_CODE";
	public static final String SEC_LAST = "last";
	public static final String SEC_OPEN = "open";
	public static final String SEC_CLOSE = "prevlegalclosepr";
	public static final String SEC_DISPNAME = "LONGNAME";
	public static final String SEC_SHORTNAME = "SHORTNAME";
	public static final String SEC_ASK = "offer";
	public static final String SEC_BID = "bid";
	public static final String SEC_HIGH = "high";
	public static final String SEC_LOW = "low";
	public static final String SEC_CURRENCY = "curstepprice";
	public static final String SEC_TYPE = "CLASSNAME";
	
	public static final String SEC_DEFAULT_CURRENCY = "SUR";
	public static final Map<String, SecurityType> SEC_TYPE_MAP;
	/**
	 * Список обязательных полей "Таблица текущих значений" (инструментов).
	 */
	private static final String[] SEC_REQUIRED_FIELDS = {
		SEC_LOT_SIZE,
		SEC_PRICE_MAX,
		SEC_PRICE_MIN,
		SEC_STEP_PRICE,
		SEC_PRICE_STEP,
		SEC_SCALE,
		SEC_CODE,
		SEC_CLASS,
		SEC_LAST,
		SEC_OPEN,
		SEC_CLOSE,
		SEC_DISPNAME,
		SEC_SHORTNAME,
		SEC_ASK,
		SEC_BID,
		SEC_HIGH,
		SEC_LOW,
		SEC_CURRENCY,
		SEC_TYPE,
	};

	static {
		Map<String, OrderStatus> status = new HashMap<String, OrderStatus>();
		status.put("KILLED", OrderStatus.CANCELLED);
		status.put("ACTIVE", OrderStatus.ACTIVE);
		status.put("FILLED", OrderStatus.FILLED);
		ORD_STATUS_MAP = Collections.unmodifiableMap(status);
		ORDS_STATUS_MAP = Collections.unmodifiableMap(status);
		
		Map<String, OrderDirection> dir = new HashMap<String, OrderDirection>();
		dir.put("B", OrderDirection.BUY);
		dir.put("S", OrderDirection.SELL);
		ORD_DIR_MAP = Collections.unmodifiableMap(dir);
		ORDS_DIR_MAP = Collections.unmodifiableMap(dir);
		
		Map<String, OrderType> type = new HashMap<String, OrderType>();
		type.put("L", OrderType.LIMIT);
		type.put("M", OrderType.MARKET);
		ORD_TYPE_MAP = Collections.unmodifiableMap(type);
		
		type = new HashMap<String, OrderType>();
		type.put(ORDS_TYPE_SL, OrderType.STOP_LIMIT);
		type.put(ORDS_TYPE_TP, OrderType.TAKE_PROFIT);
		type.put(ORDS_TYPE_SLTP, OrderType.TPSL);
		ORDS_TYPE_MAP = Collections.unmodifiableMap(type);
		
		Map<String, PriceUnit> unit = new HashMap<String, PriceUnit>();
		unit.put("Д", PriceUnit.MONEY);
		unit.put("%", PriceUnit.PERCENT);
		ORDS_UNIT_MAP = Collections.unmodifiableMap(unit);
		
		Map<String, SecurityType> stype = new HashMap<String, SecurityType>();
		stype.put("ФОРТС фьючерсы", SecurityType.FUT);
		SEC_TYPE_MAP = Collections.unmodifiableMap(stype);
	}

	private final QUIKServiceLocator locator;
	
	public RowAdapters(QUIKServiceLocator locator) {
		super();
		this.locator = locator;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы всех сделок.
	 * <p>  
	 * @return список полей
	 */
	public String[] getAllDealsRequiredFields() {
		return DEAL_REQUIRED_FIELDS;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы портфелей по
	 * бумагам.
	 * <p>
	 * @return список полей
	 */
	public String[] getPortfolioStkRequiredFields() {
		return PORT_REQUIRED_FIELDS;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы портфелей по
	 * деривативам.
	 * <p>
	 * @return список полей
	 */
	public String[] getPortfolioFutRequiredFields() {
		return PORTF_REQUIRED_FIELDS;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы позиций по
	 * бумагам.
	 * <p>
	 * @return список полей
	 */
	public String[] getPositionStkRequiredFields() {
		return POS_REQUIRED_FIELDS;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы позиций по
	 * деривативам.
	 * <p>
	 * @return список полей
	 */
	public String[] getPositionFutRequiredFields() {
		return POSF_REQUIRED_FIELDS;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы заявок.
	 * <p>
	 * @return список полей
	 */
	public String[] getOrderRequiredFields() {
		return ORD_REQUIRED_FIELDS;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы стоп-заявок.
	 * <p>
	 * @return список полей
	 */
	public String[] getStopOrderRequiredFields() {
		return ORDS_REQUIRED_FIELDS;
	}
	
	/**
	 * Получить список полей, необходимых для импорта таблицы инструментов.
	 * <p>
	 * @return список полей
	 */
	public String[] getSecurityRequiredFields() {
		return SEC_REQUIRED_FIELDS;
	}

	/**
	 * Создать набор адаптеров для конвертации таблицы всех сделок.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createAllDealsAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Trades: ");
		Map<String, G<?>> list = new HashMap<String, G<?>>();
		list.put(Spec.TRADE_ID, ea.createLong(DEAL_NUM));
		list.put(Spec.TRADE_TIME, ea.createDate(DEAL_DATE, DEAL_TIME,
				locator.getConfig().getDateFormat(),
				locator.getConfig().getTimeFormat(), true));
		list.put(Spec.TRADE_PRICE, ea.createDouble(DEAL_PRICE));
		list.put(Spec.TRADE_QTY, ea.createLong(DEAL_QTY));
		list.put(Spec.TRADE_SECDESCR,
				ea.createSecDescr(DEAL_SEC, DEAL_SECCLASS));
		list.put(Spec.TRADE_DIR,
				ea.createOrderDir(DEAL_DIR, DEAL_DIR_BUY, DEAL_DIR_SELL));
		list.put(Spec.TRADE_VOL, ea.createDouble(DEAL_VALUE));
		return list;
	}
	
	/**
	 * Создать набор адаптеров для конвертации таблицы портфелей по бумагам.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createPortfolioStkAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Portfolio STK: ");
		Map<String, G<?>> list = new HashMap<String, G<?>>();
		list.put(Spec.PORT_ACCOUNT, ea.createAccount(PORT_FIRM, PORT_CODE));
		list.put(Spec.PORT_CASH, ea.createDouble(PORT_CASH));
		list.put(Spec.PORT_BALANCE, ea.createDouble(PORT_BALANCE));
		list.put(Spec.PORT_VMARGIN, ea.createDouble(PORT_VMARGIN));
		return list;
	}
	
	/**
	 * Создать набор адаптеров для конвертации таблицы портфелей по деривативам.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createPortfolioFutAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Portfolio FUT: ");
		Map<String, G<?>> list = new HashMap<String, G<?>>();
		list.put(Spec.PORT_ACCOUNT, ea.createAccount(PORTF_FIRM, PORTF_CODE));
		list.put(Spec.PORT_CASH, ea.createDouble(PORTF_CASH));
		list.put(Spec.PORT_BALANCE, ea.createDouble(PORTF_BALANCE));
		list.put(Spec.PORT_VMARGIN, ea.createDouble(PORTF_VMARGIN));
		list.put(PORTF_TYPE, ea.createString(PORTF_TYPE));
		return list;
	}
	
	/**
	 * Создать набор адаптеров для конвертации таблицы позиций по бумагам.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createPositionStkAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Position STK: ");
		Map<String, G<?>> list = new HashMap<String, G<?>>();
		list.put(Spec.POS_ACCOUNT,
				ea.createAccount(POS_FIRM, POS_PORTFOLIO, POS_ACCOUNT));
		list.put(Spec.POS_SECDESCR, ea.createSecDescr(POS_SEC_NAME));
		list.put(Spec.POS_CURR, ea.createLong(POS_CURR));
		list.put(Spec.POS_LOCK, ea.createLong(POS_LOCK));
		list.put(Spec.POS_OPEN, ea.createLong(POS_OPEN));
		return list;
	}
	
	/**
	 * Создать набор адаптеров для конвертации таблицы позиций по деривативам.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createPositionFutAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Position FUT: ");
		Map<String, G<?>> list = new HashMap<String, G<?>>();
		list.put(Spec.POS_ACCOUNT, ea.createAccount(POSF_FIRM, POSF_ACCOUNT));
		list.put(Spec.POS_SECDESCR, ea.createSecDescr(POSF_SEC_NAME));
		list.put(Spec.POS_OPEN, ea.createLong(POSF_OPEN));
		list.put(Spec.POS_CURR, ea.createLong(POSF_CURR));
		list.put(Spec.POS_VMARGIN, ea.createDouble(POSF_VMARGIN));
		return list;
	}
	
	/**
	 * Создать набор адаптеров для конвертации таблицы заявок.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createOrderAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Order: ");
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		expected.put(Spec.ORD_ID, ea.createLong(ORD_ID));
		expected.put(Spec.ORD_TRANSID, ea.createLong(ORD_TRANS_ID));
		expected.put(Spec.ORD_STATUS,
				ea.createStringMap(ORD_STATUS, ORD_STATUS_MAP, true, null));
		expected.put(Spec.ORD_SECDESCR,ea.createSecDescr(ORD_SEC,ORD_SECCLASS));
		expected.put(Spec.ORD_ACCOUNT,
				ea.createOrderAccount(ORD_PORTFOLIO, ORD_ACCOUNT));
		expected.put(Spec.ORD_DIR,
				ea.createStringMap(ORD_DIR, ORD_DIR_MAP, true, null));
		expected.put(Spec.ORD_QTY, ea.createLong(ORD_QTY));
		expected.put(Spec.ORD_QTYREST, ea.createLong(ORD_QTY_REST));
		expected.put(Spec.ORD_PRICE, ea.createDouble(ORD_PRICE));
		expected.put(Spec.ORD_TIME, ea.createDate(ORD_DATE, ORD_TIME,
				locator.getConfig().getDateFormat(),
				locator.getConfig().getTimeFormat(), true));
		expected.put(Spec.ORD_CHNGTIME, ea.createDate(ORD_CHNGDATE,
				ORD_CHNGTIME,
				locator.getConfig().getDateFormat(),
				locator.getConfig().getTimeFormat(), false));
		expected.put(Spec.ORD_TYPE, ea.createOrderType(ORD_TYPE, ORD_TYPE_MAP));
		return expected;
	}
	
	/**
	 * Создать набор адаптеров для конвертации таблицы стоп-заявок.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createStopOrderAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Stop-order: ");
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		expected.put(Spec.ORD_ID, ea.createLong(ORDS_ID));
		expected.put(Spec.ORD_TRANSID, ea.createLong(ORDS_TRANS_ID));
		expected.put(Spec.ORD_STATUS,
				ea.createStringMap(ORDS_STATUS, ORDS_STATUS_MAP, true, null));
		expected.put(Spec.ORD_SECDESCR,
				ea.createSecDescr(ORDS_SEC, ORDS_SECCLASS));
		expected.put(Spec.ORD_ACCOUNT,
				ea.createOrderAccount(ORDS_PORTFOLIO, ORDS_ACCOUNT));
		expected.put(Spec.ORD_DIR,
				ea.createStringMap(ORDS_DIR, ORDS_DIR_MAP, true, null));
		expected.put(Spec.ORD_PRICE, ea.createDouble(ORDS_PRICE));
		expected.put(Spec.ORD_TIME, ea.createDate(ORDS_DATE, ORDS_TIME,
				locator.getConfig().getDateFormat(),
				locator.getConfig().getTimeFormat(), true));
		expected.put(Spec.ORD_TYPE, ea.createStringMap(ORDS_TYPE, ORDS_TYPE_MAP,
				true, OrderType.OTHER));
		expected.put(Spec.ORD_LINKID, ea.createLong(ORDS_LINKED_ID));
		Map<String, G<Double>> stopLimit = new HashMap<String, G<Double>>();
		stopLimit.put(ORDS_TYPE_SL,	ea.createDouble(ORDS_PRICE1));
		stopLimit.put(ORDS_TYPE_SLTP,	ea.createDouble(ORDS_PRICE2));
		expected.put(Spec.ORD_STOPLMT,
				ea.createStringMap2G(ORDS_TYPE, stopLimit, null));
		Map<String, G<Double>> takeProfit = new HashMap<String, G<Double>>();
		takeProfit.put(ORDS_TYPE_TP, ea.createDouble(ORDS_PRICE1));
		takeProfit.put(ORDS_TYPE_SLTP, ea.createDouble(ORDS_PRICE1));
		expected.put(Spec.ORD_TAKEPFT,
				ea.createStringMap2G(ORDS_TYPE, takeProfit, null));
		expected.put(Spec.ORD_OFFSET,
				ea.createPrice(ORDS_OFFSET, ORDS_OFFSET_UNITS, ORDS_UNIT_MAP));
		expected.put(Spec.ORD_SPREAD,
				ea.createPrice(ORDS_SPREAD, ORDS_SPREAD_UNITS, ORDS_UNIT_MAP));
		expected.put(Spec.ORD_QTY, ea.createLong(ORDS_QTY));

		return expected;
	}
	
	/**
	 * Создать набор адаптеров для конвертации таблицы инструментов.
	 * <p>
	 * @return набор адаптеров
	 */
	public Map<String, G<?>> createSecurityAdapters() {
		ElementAdapters ea = new ElementAdapters(locator, "Security: ");
		Map<String, G<?>> expected = new HashMap<String, G<?>>();
		expected.put(Spec.SEC_LOTSZ, ea.createInteger(SEC_LOT_SIZE, false));
		expected.put(Spec.SEC_MAXPR,
				ea.createElement(SEC_PRICE_MAX, Double.class));
		expected.put(Spec.SEC_MINPR,
				ea.createElement(SEC_PRICE_MIN, Double.class));
		expected.put(Spec.SEC_MINSTEPPR,
				ea.createElement(SEC_STEP_PRICE, Double.class));
		expected.put(Spec.SEC_MINSTEPSZ, ea.createDouble(SEC_PRICE_STEP));
		expected.put(Spec.SEC_PREC, ea.createInteger(SEC_SCALE));
		expected.put(Spec.SEC_DESCR, ea.createSecDescr(SEC_CODE, SEC_CLASS,
				SEC_CURRENCY, SEC_DEFAULT_CURRENCY, SEC_TYPE, SEC_TYPE_MAP));
		expected.put(Spec.SEC_LAST, ea.createElement(SEC_LAST, Double.class));
		expected.put(Spec.SEC_OPEN, ea.createElement(SEC_OPEN, Double.class));
		expected.put(Spec.SEC_CLOSE, ea.createElement(SEC_CLOSE, Double.class));
		expected.put(Spec.SEC_DISPNAME, ea.createString(SEC_DISPNAME));
		expected.put(Spec.SEC_SHORTNAME, ea.createString(SEC_SHORTNAME));
		expected.put(Spec.SEC_ASKPR, ea.createElement(SEC_ASK, Double.class));
		expected.put(Spec.SEC_BIDPR, ea.createElement(SEC_BID, Double.class));
		expected.put(Spec.SEC_HIGH, ea.createElement(SEC_HIGH, Double.class));
		expected.put(Spec.SEC_LOW, ea.createElement(SEC_LOW, Double.class));
		return expected;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowAdapters.class ) {
			RowAdapters o = (RowAdapters) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.isEquals();
		} else {
			return false;
		}
	}

}
