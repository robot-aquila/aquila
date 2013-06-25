package ru.prolib.aquila.quik.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.quik.dde.*;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.table.Column;
import ru.prolib.aquila.ui.table.Columns;
import ru.prolib.aquila.ui.table.Table;

public class TableBuilder {
	private static final SimpleDateFormat dateFormat;
	private static final SimpleDateFormat dateFormatMs;
	
	static {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatMs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	private final ClassLabels labels;
	private final Cache cache; 
	
	public TableBuilder(ClassLabels labels, Cache cache) {
		super();
		this.labels = labels;
		this.cache = cache;
	}

	public Table createOrdersCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column("COL_CACHE_ORDER_ID", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_TRANS_ID", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getTransId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_STATUS", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getStatus();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_SEC_CODE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getSecurityCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_SEC_CLASS_CODE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getSecurityClassCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_ACCOUNT", new G<String>() {
			@Override public String get(Object obj) {
				return ((OrderCache) obj).getAccountCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_CLIENT_CODE", new G<String>() {
			@Override public String get(Object obj) {
				return ((OrderCache) obj).getClientCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_DIR", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getDirection();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_QTY", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_QTY_REST", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getQtyRest();
			}
		}, Column.MIDDLE));		
		columns.add(new Column("COL_CACHE_ORDER_PRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_ORDER_TIME", new G<Object>() {
			@Override public Object get(Object obj) {
				Date time = ((OrderCache) obj).getTime();
				return time == null ? null : dateFormat.format(time);
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_ORDER_WITHDRAW_TIME", new G<Object>() {
			@Override public Object get(Object obj) {
				Date time = ((OrderCache) obj).getWithdrawTime();
				return time == null ? null : dateFormat.format(time);
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_ORDER_TYPE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getType();
			}
		}, Column.SHORT));
		columns.add(new Column("COL_CACHE_ORDER_ENTRY_TIME", new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((OrderCache) obj).getEntryTime());
			}
		}, Column.LONG));
		return new Table(new OrdersCacheTableModel(labels,
				columns, cache.getOrdersCache()));
	}
	
	public Table createTradesCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column("COL_CACHE_TRADE_ID", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradeCache) obj).getId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_TRADE_TIME", new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormat.format(((TradeCache) obj).getTime());
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_TRADE_ORDER_ID", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradeCache) obj).getOrderId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_TRADE_PRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradeCache) obj).getPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_TRADE_QTY", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradeCache) obj).getQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_TRADE_VOLUME", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((TradeCache) obj).getVolume();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_TRADE_ENTRY_TIME", new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((TradeCache) obj).getEntryTime());
			}
		}, Column.LONG));
		return new Table(new TradesCacheTableModel(labels,
				columns, cache.getTradesCache()));
	}
	
	public Table createSecuritiesCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column("COL_CACHE_SECURITY_DESCR", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getDescriptor());
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_SECURITY_MINPRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getMinPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_MAXPRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getMaxPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_STEPSIZE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getMinStepSize());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_STEPPRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getMinStepPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_PRECISION", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getPrecision());
			}
		}, Column.SHORT));
		columns.add(new Column("COL_CACHE_SECURITY_LAST", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getLastPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_OPEN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getOpenPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_HIGH", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getHighPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_LOW", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getLowPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_CLOSE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getClosePrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_ASK", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getAskPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_BID", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getBidPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_DISPNAME", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getDisplayName());
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_SECURITY_SHORTNAME", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityCache) obj).getShortName());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_ENTRY_TIME",new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((SecurityCache)obj).getEntryTime());
			}
		}, Column.LONG));
		return new Table(new SecuritiesCacheTableModel(labels,
				columns, cache.getSecuritiesCache()));
	}

	public Table createPortfoliosFortsCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column("COL_CACHE_PORT_F_ACCOUNT", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioFCache) obj).getAccountCode());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_FIRMID", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioFCache) obj).getFirmId());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_BALANCE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioFCache) obj).getBalance());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_CASH", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioFCache) obj).getCash());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_VARMARGIN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioFCache) obj).getVarMargin());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_ENTRY_TIME",new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((PortfolioFCache)obj)
						.getEntryTime());
			}
		}, Column.LONG));
		return new Table(new PortfoliosFCacheTableModel(labels,
				columns, cache.getPortfoliosFCache()));
	}
	
	public Table createPositionsFortsCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column("COL_CACHE_POS_F_ACCOUNT", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionFCache) obj).getAccountCode());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_FIRMID", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionFCache) obj).getFirmId());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_SEC_SNAME", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionFCache) obj).getSecurityShortName());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_OPEN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionFCache) obj).getOpenQty());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_CURR", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionFCache) obj).getCurrentQty());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_VARMARGIN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionFCache) obj).getVarMargin());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_ENTRY_TIME",new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((PositionFCache)obj)
						.getEntryTime());
			}
		}, Column.LONG));
		return new Table(new PositionsFCacheTableModel(labels,
				columns, cache.getPositionsFCache()));
	}
	
	public Table createStopOrdersCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column("COL_CACHE_STOP_ORDER_ID", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_TRANS_ID",
				new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getTransId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_STATUS", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getStatus();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_SEC_CODE",
				new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getSecurityCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_SEC_CLASS",
				new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getSecurityClassCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_ACCOUNT", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getAccountCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_CLIENT_CODE",
				new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getAccountCode();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_DIR", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getDirection();
			}
		}, Column.SHORT));
		columns.add(new Column("COL_CACHE_STOP_ORDER_QTY", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getQty();
			}
		}, Column.SHORT));
		columns.add(new Column("COL_CACHE_STOP_ORDER_PRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_STOP_LIMIT",
				new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getStopLimitPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_TAKE_PROFIT",
				new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getTakeProfitPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_OFFSET", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getOffset();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_SPREAD", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getSpread();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_LINKED_ID",
				new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getLinkedOrderId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_TIME", new G<Object>() {
			@Override public Object get(Object obj) {
				Date time = ((StopOrderCache) obj).getTime();
				return time == null ? null : dateFormat.format(time);
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_STOP_ORDER_WITHDRAW_TIME",
				new G<Object>() {
			@Override public Object get(Object obj) {
				Date time = ((StopOrderCache) obj).getWithdrawTime();
				return time == null ? null : dateFormat.format(time);
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_STOP_ORDER_TYPE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((StopOrderCache) obj).getType();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_STOP_ORDER_ENTRY_TIME",
				new G<Object>() {
			@Override public Object get(Object obj) {
				Date time = ((StopOrderCache)obj).getEntryTime();
				return time == null ? null : dateFormatMs.format(time);
			}
		}, Column.LONG));
		return new Table(new StopOrdersCacheTableModel(labels,
				columns, cache.getStopOrdersCache()));
	}
	
}
