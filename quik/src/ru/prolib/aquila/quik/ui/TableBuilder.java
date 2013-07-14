package ru.prolib.aquila.quik.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.quik.assembler.cache.PortfolioEntry;
import ru.prolib.aquila.quik.assembler.cache.PositionEntry;
import ru.prolib.aquila.quik.assembler.cache.SecurityEntry;
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
				return (((SecurityEntry) obj).getDescriptor());
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_SECURITY_MINPRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getMinPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_MAXPRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getMaxPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_STEPSIZE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getMinStepSize());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_STEPPRICE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getMinStepPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_PRECISION", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getPrecision());
			}
		}, Column.SHORT));
		columns.add(new Column("COL_CACHE_SECURITY_LAST", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getLastPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_OPEN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getOpenPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_HIGH", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getHighPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_LOW", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getLowPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_CLOSE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getClosePrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_ASK", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getAskPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_BID", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getBidPrice());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_DISPNAME", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getDisplayName());
			}
		}, Column.LONG));
		columns.add(new Column("COL_CACHE_SECURITY_SHORTNAME", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((SecurityEntry) obj).getShortName());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_SECURITY_ENTRY_TIME",new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((SecurityEntry)obj).getEntryTime());
			}
		}, Column.LONG));
		return new Table(new SecuritiesCacheTableModel(labels,
				columns, cache.getSecuritiesCache()));
	}

	public Table createPortfoliosFortsCacheTable() {
		Columns columns = new Columns();
		columns.add(new Column("COL_CACHE_PORT_F_ACCOUNT", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioEntry) obj).getAccountCode());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_FIRMID", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioEntry) obj).getFirmId());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_BALANCE", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioEntry) obj).getBalance());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_CASH", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioEntry) obj).getCash());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_VARMARGIN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PortfolioEntry) obj).getVarMargin());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_PORT_F_ENTRY_TIME",new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((PortfolioEntry)obj)
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
				return (((PositionEntry) obj).getAccountCode());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_FIRMID", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionEntry) obj).getFirmId());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_SEC_SNAME", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionEntry) obj).getSecurityShortName());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_OPEN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionEntry) obj).getOpenQty());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_CURR", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionEntry) obj).getCurrentQty());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_VARMARGIN", new G<Object>() {
			@Override public Object get(Object obj) {
				return (((PositionEntry) obj).getVarMargin());
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_CACHE_POS_F_ENTRY_TIME",new G<Object>() {
			@Override public Object get(Object obj) {
				return dateFormatMs.format(((PositionEntry)obj)
						.getEntryTime());
			}
		}, Column.LONG));
		return new Table(new PositionsFCacheTableModel(labels,
				columns, cache.getPositionsFCache()));
	}
	
}
