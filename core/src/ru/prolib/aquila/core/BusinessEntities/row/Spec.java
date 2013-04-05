package ru.prolib.aquila.core.BusinessEntities.row;

/**
 * Спецификаторы данных ряда для конвертации в объекты бизнес-модели.
 * <p>
 * Содержит идентификаторы элементов, используемых для конвертации ряда в
 * атрибуты соответствующих объектов модели.
 * <p>
 * 2013-02-17<br>
 * $Id$
 */
public class Spec {

	/**
	 * Идентификатор элемента ряда с направлением сделки. Ожидается экземпляр
	 * класса {@link ru.prolib.aquila.core.BusinessEntities.OrderDirection
	 * OrderDirection}. 
	 */
	public static final String TRADE_DIR = "TRD_DIR";
	/**
	 * Идентификатор элемента ряда с идентификатором сделки. Ожидается экземпляр
	 * класса {@link java.lang.Long Long}.
	 */
	public static final String TRADE_ID = "TRD_ID";
	/**
	 * Идентификатор элемента ряда с ценой заявки. Ожидается экземпляр класса
	 * {@link java.lang.Double Double}.
	 */
	public static final String TRADE_PRICE = "TRD_PRICE";
	/**
	 * Идентификатор элемента ряда с количеством заявки. Ожидается экземпляр
	 * класса {@link java.lang.Long Long}.
	 */
	public static final String TRADE_QTY = "TRD_QTY";
	/**
	 * Идентификатор элемента ряда с дескриптором инструмента заявки. Ожидается
	 * экземпляр класса {@link
	 * ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor
	 * SecurityDescriptor}.
	 */
	public static final String TRADE_SECDESCR = "TRD_SECDESCR";
	/**
	 * Идентификатор элемента ряда с временем исполнения заявки. Ожидается
	 * экземпляр класса {@link java.util.Date Date}.
	 */
	public static final String TRADE_TIME = "TRD_TIME";
	/**
	 * Идентификатор элемента ряда с объемом заявки (цена * количество).
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static final String TRADE_VOL = "TRD_VOL";

	
	/**
	 * Идентификатор элемента ряда с объектом счета портфеля. Ожидается
	 * экземпляр класса {@link ru.prolib.aquila.core.BusinessEntities.Account
	 * Account}.
	 */
	public static final String PORT_ACCOUNT = "PORT_ACC";
	/**
	 * Идентификатор элемента ряда с балансом портфеля.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static final String PORT_BALANCE = "PORT_BAL";
	/**
	 * Идентификатор элемента ряда с доступными денежными средствами портфеля.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static final String PORT_CASH = "PORT_CASH";
	/**
	 * Идентификатор элемента ряда с величиной текущей вариационной маржи
	 * портфеля. Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static final String PORT_VMARGIN = "PORT_VMARG";

	
	/**
	 * Идентификатор элемента ряда с объектом счета позиции. Ожидается
	 * экземпляр класса {@link ru.prolib.aquila.core.BusinessEntities.Account
	 * Account}.
	 */
	public static final String POS_ACCOUNT = "POS_ACC";
	/**
	 * Идентификатор элемента ряда с балансовой стоимостью позиции.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static final String POS_BOOKVAL = "POS_BOOKVAL";
	/**
	 * Идентификатор элемента ряда с рыночной стоимостью позиции.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static final String POS_MARKETVAL = "POS_MKTVAL";
	/**
	 * Идентификатор элемента ряда с текущим размером позиции.
	 * Ожидается экземпляр класса {@link java.lang.Long Long}.
	 */
	public static final String POS_CURR = "POS_CURR";
	/**
	 * Идентификатор элемента ряда с заблокированным под текущие операции
	 * количеством. Ожидается экземпляр класса {@link java.lang.Long Long}.
	 */
	public static final String POS_LOCK = "POS_LOCK";
	/**
	 * Идентификатор элемента ряда с размером позиции на начало торговой сессии.
	 * Ожидается экземпляр класса {@link java.lang.Long Long}.
	 */
	public static final String POS_OPEN = "POS_OPEN";
	/**
	 * Идентификатор элемента ряда с дескриптором инструмента позиции. Ожидается
	 * экземпляр класса {@link
	 * ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor
	 * SecurityDescriptor}.
	 */
	public static final String POS_SECDESCR = "POS_SECDESCR";
	/**
	 * Идентификатор элемента ряда с вариационной маржой позиции.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static final String POS_VMARGIN = "POS_VMARG";
	
	
	/**
	 * Идентификатор элемента ряда с объектом счета заявки. Ожидается
	 * экземпляр класса {@link ru.prolib.aquila.core.BusinessEntities.Account
	 * Account}.
	 */
	public static String ORD_ACCOUNT = "ORD_ACC";
	/**
	 * Идентификатор элемента ряда с направлением заявки. Ожидается экземпляр
	 * класса {@link ru.prolib.aquila.core.BusinessEntities.OrderDirection
	 * OrderDirection}. 
	 */
	public static String ORD_DIR = "ORD_DIR";
	/**
	 * Идентификатор элемента ряда со стоимостью исполненной части заявки
	 * (сумма по всем сделкам заявки). Не используется для стоп-заявок.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}. 
	 */
	public static String ORD_EXECVOL = "ORD_EXECVOL";
	/**
	 * Идентификатор элемента ряда с идентификатором заявки. Ожидается экземпляр
	 * класса {@link java.lang.Long Long}.
	 */
	public static String ORD_ID = "ORD_ID";
	/**
	 * Идентификатор элемента ряда с идентификатором порожденной заявки. Только
	 * для стоп-заявок. Ожидается экземпляр класса {@link java.lang.Long Long}.
	 */
	public static String ORD_LINKID = "ORD_LINKID";
	/**
	 * Идентификатор элемента ряда с отступом цены для тэйк-профита. Только для
	 * стоп-заявок. Ожидается экземпляр класса
	 * {@link ru.prolib.aquila.core.BusinessEntities.Price Price}.
	 */
	public static String ORD_OFFSET = "ORD_OFFSET";
	/**
	 * Идентификатор элемента ряда с ценой заявки. Ожидается экземпляр класса
	 * {@link java.lang.Double Double}. Для стоп заявок типа Тейк-профит
	 * значение цены не определено.
	 */
	public static String ORD_PRICE = "ORD_PRICE";
	/**
	 * Идентификатор элемента ряда с количеством заявки. Ожидается экземпляр
	 * класса {@link java.lang.Long Long}.
	 */
	public static String ORD_QTY = "ORD_QTY";
	/**
	 * Идентификатор элемента ряда с неисполненным количеством заявки. Не
	 * используется для стоп-заявок. Ожидается экземпляр класса
	 * {@link java.lang.Long Long}.
	 */
	public static String ORD_QTYREST = "ORD_QTYREST";
	/**
	 * Идентификатор элемента ряда с дескриптором инструмента заявки. Ожидается
	 * экземпляр класса {@link
	 * ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor
	 * SecurityDescriptor}.
	 */
	public static String ORD_SECDESCR = "ORD_SECDESCR";
	/**
	 * Идентификатор элемента ряда с защитным спредом тэйк-профита. Только для
	 * стоп-заявок. Ожидается экземпляр класса
	 * {@link ru.prolib.aquila.core.BusinessEntities.Price Price}.
	 */
	public static String ORD_SPREAD = "ORD_SPREAD";
	/**
	 * Идентификатор элемента ряда со статусом заявки. Ожидается экземпляр
	 * класса {@link ru.prolib.aquila.core.BusinessEntities.OrderStatus
	 * OrderStatus}.
	 */
	public static String ORD_STATUS = "ORD_STATUS";
	/**
	 * Идентификатор элемента ряда стоп-лимит цены заявки. Только для
	 * стоп-заявок. Ожидается экземпляр класса {@link java.lang.Double Double}. 
	 */
	public static String ORD_STOPLMT = "ORD_STOPLMT";
	/**
	 * Идентификатор элемента ряда тэйк-профит цены заявки. Только для
	 * стоп-заявок. Ожидается экземпляр класса {@link java.lang.Double Double}. 
	 */
	public static String ORD_TAKEPFT = "ORD_TAKEPFT";
	/**
	 * Идентификатор элемента ряда с идентификатором транзакции. Ожидается
	 * экземпляр класса {@link java.lang.Long Long}.
	 */
	public static String ORD_TRANSID = "ORD_TRANSID";
	/**
	 * Идентификатор элемента ряда с типом заявки. Ожидается экземпляр класса
	 * {@link ru.prolib.aquila.core.BusinessEntities.OrderType OrderType}.
	 */
	public static String ORD_TYPE = "ORD_TYPE";
	/**
	 * Идентификатор элемента ряда с временем выставления заявки. Ожидается
	 * экземпляр класса {@link java.util.Date}.
	 */
	public static String ORD_TIME = "ORD_TIME";
	/**
	 * Идентификатор элемента ряда с временем последнего изменения заявки.
	 * Ожидается экземпляр класса {@link java.util.Date}.
	 */
	public static String ORD_CHNGTIME = "ORD_CHNGTIME";
	
	
	/**
	 * Идентификатор элемента ряда с лучшей ценой предложения (продавец).
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_ASKPR = "SEC_ASKPR";
	/**
	 * Идентификатор элемента ряда с размером предложения по лучшей цене.
	 * Ожидается экземпляр класса {@link java.lang.Long Long}.
	 */
	public static String SEC_ASKSZ = "SEC_ASKSZ";
	/**
	 * Идентификатор элемента ряда с лучшей ценой спроса (покупатель).
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_BIDPR = "SEC_BIDPR";
	/**
	 * Идентификатор элемента ряда с размером спроса по лучшей цене. Ожидается
	 * экземпляр класса {@link java.lang.Long Long}.
	 */
	public static String SEC_BIDSZ = "SEC_BIDSZ";
	/**
	 * Идентификатор элемента ряда с ценой закрытия предыдущей торговой сессии.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_CLOSE = "SEC_CLOSE";
	/**
	 * Идентификатор элемента ряда с полным наименованием инструмента.
	 * Ожидается экземпляр класса {@link java.lang.String String}.
	 */
	public static String SEC_DISPNAME = "SEC_DISPNAME";
	/**
	 * Идентификатор элемента ряда с максимальной ценой за текущую сессию.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_HIGH = "SEC_HIGH";
	/**
	 * Идентификатор элемента ряда с ценой последней сделки текущей сессии.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_LAST = "SEC_LAST";
	/**
	 * Идентификатор элемента ряда с размером лота. Ожидается экземпляр класса
	 * {@link java.lang.Integer Integer}.
	 */
	public static String SEC_LOTSZ = "SEC_LOTSZ";
	/**
	 * Идентификатор элемента ряда с минимальной ценой за текущую сессию.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_LOW = "SEC_LOW";
	/**
	 * Идентификатор элемента ряда с максимально-допустимой ценой текущей
	 * сессии. Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_MAXPR = "SEC_MAXPR";
	/**
	 * Идентификатор элемента ряда с минимально-допустимой ценой текущей
	 * сессии. Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_MINPR = "SEC_MINPR";
	/**
	 * Идентификатор элемента ряда со стоимостью шага цены. Стоимость шага цены
	 * отличается от размера шага, если цена выражается в единицах, отличных
	 * от валюты инструмента. Например, цена фьючерса на индекс РТС выражается в
	 * пунктах, а торгуется за рубли. Ожидается экземпляр класса
	 * {@link java.lang.Double Double}.
	 */
	public static String SEC_MINSTEPPR = "SEC_MINSTEPPR";
	/**
	 * Идентификатор элемента ряда с минимальным шагом цены (размер тика).
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_MINSTEPSZ = "SEC_MINSTEPSZ";
	/**
	 * Идентификатор элемента ряда с ценой открытия текущей сессии.
	 * Ожидается экземпляр класса {@link java.lang.Double Double}.
	 */
	public static String SEC_OPEN = "SEC_OPEN";
	/**
	 * Идентификатор элемента ряда с точностью цены в знаках после точки.
	 * Ожидается экземпляр класса {@link java.lang.Integer Integer}.
	 */
	public static String SEC_PREC = "SEC_PREC";
	/**
	 * Идентификатор элемента ряда со статусом инструмента. Ожидается экземпляр
	 * класса {@link ru.prolib.aquila.core.BusinessEntities.SecurityStatus
	 * SecurityStatus}.
	 */
	public static String SEC_STATUS = "SEC_STATUS";
	/**
	 * Идентификатор элемента ряда с дескриптором инструмента. Ожидается
	 * экземпляр класса
	 * {@link ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor
	 * SecurityDescriptor}.
	 */
	public static String SEC_DESCR = "SEC_DESCR";
	/**
	 * Идентификатор элемента ряда с кратким наименованием инструмента.
	 * Ожидается экземпляр класса {@link java.lang.String String}.
	 */
	public static String SEC_SHORTNAME = "SEC_SHORTNAME";

}
