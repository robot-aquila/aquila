package ru.prolib.aquila.quik.ui;

import java.text.SimpleDateFormat;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.*;
import ru.prolib.aquila.ui.table.*;

public class TableBuilder {
	private static final String SECTION_ID = "Quik";
	private static final SimpleDateFormat dateFormatMs;
	
	static {
		dateFormatMs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	private final IMessages messages;
	private final Cache cache; 
	
	public TableBuilder(IMessages messages, Cache cache) {
		super();
		this.messages = messages;
		this.cache = cache;
	}
	
	public static MsgID msgID(String messageId) {
		return new MsgID(SECTION_ID, messageId);
	}
	
	public Table createOwnTradesTable() {
		Columns columns = new Columns();
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_MODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getMode();
			}
		}, Column.SHORT));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_ID"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getId();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_ORDER_ID"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getOrderId();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_ACCOUNT"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getAccount();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_SEC_CODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getSecCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_CLASS_CODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getClassCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_EXCH_CODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getExchangeCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_QTY"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_PRICE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_DATE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getDate();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_OWNTRD_TIME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QTrade) obj).getTime();
			}
		}, Column.MIDDLE));
		return new Table(new OwnTradesCacheTableModel(messages, columns, cache));
	}

	public Table createOrdersTable() {
		Columns columns = new Columns();
		columns.add(new Column(msgID("COL_CACHE_ORDER_MODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getMode();
			}
		}, Column.SHORT));
		columns.add(new Column(msgID("COL_CACHE_ORDER_ID"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getOrderId();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_TRANS_ID"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getTransId();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_ACCOUNT"), new G<String>() {
			@Override public String get(Object obj) {
				return ((T2QOrder) obj).getAccount();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_DIR"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).isSell() ? "Sell" : "Buy";
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_SEC_CODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getSecCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_CLASS_CODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getClassCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_STATUS"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getStatus();
			}
		}, Column.SHORT));
		columns.add(new Column(msgID("COL_CACHE_ORDER_QTY"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_QTY_REST"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getBalance();
			}
		}, Column.MIDDLE));		
		columns.add(new Column(msgID("COL_CACHE_ORDER_PRICE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_DATE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getDate();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_TIME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getTime();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_ORDER_ACT_TIME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((T2QOrder) obj).getActivationTime();
			}
		}, Column.MIDDLE));

		return new Table(new OrdersCacheTableModel(messages, columns, cache));
	}
	
	public Table createTradesTable() {
		Columns columns = new Columns();
		columns.add(new Column(msgID("COL_CACHE_TRADES_COUNT"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradesEntry) obj).count();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_TRADES_POSITION"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradesEntry) obj).position();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_TRADES_ACSCNT"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradesEntry) obj).accessCount();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("ENTRY_TIME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((TradesEntry) obj).getEntryTime());
			}
		}, Column.LONG));
		return new Table(new TradesCacheTableModel(messages, columns, cache));
	}
	
	public Table createSymbolsTable() {
		Columns columns = new Columns();
		columns.add(new Column(msgID("COL_CACHE_DESCR_CODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return (((Symbol) obj).getCode());
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_DESCR_CLASS"), new G<Object>() {
			@Override public Object get(Object obj) {
				return (((Symbol) obj).getExchangeID());
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_DESCR_TYPE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return (((Symbol) obj).getType());
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_DESCR_CURR"), new G<Object>() {
			@Override public Object get(Object obj) {
				return (((Symbol) obj).getCurrency());
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_DESCR_SYSCODE"), new G<Object>() {
			@Override public Object get(Object obj) {
				return (((QUIKSymbol) obj).getSystemCode());
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_DESCR_SHORTNAME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return (((QUIKSymbol) obj).getShortName());
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_DESCR_DISPNAME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return (((QUIKSymbol) obj).getDisplayName());
			}
		}, Column.MIDDLE));
		return new Table(new SymbolsCacheTableModel(messages, columns, cache));
	}
	
	public Table createPositionsCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column(msgID("COL_CACHE_POSITION_ACCOUNT"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((PositionEntry) obj).getAccount();
			}
		}, Column.LONG));
		columns.add(new Column(msgID("COL_CACHE_POSITION_SNAME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((PositionEntry) obj).getSecurityShortName();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_POSITION_OPEN_QTY"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((PositionEntry) obj).getOpenQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_POSITION_CURR_QTY"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((PositionEntry) obj).getCurrentQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("COL_CACHE_POSITION_VMARGIN"), new G<Object>() {
			@Override public Object get(Object obj) {
				return ((PositionEntry) obj).getVarMargin();
			}
		}, Column.MIDDLE));
		columns.add(new Column(msgID("ENTRY_TIME"), new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((PositionEntry)obj)
						.getEntryTime());
			}
		}, Column.LONG));
		return new Table(new PositionsCacheTableModel(messages, columns, cache));
	}
	
}
