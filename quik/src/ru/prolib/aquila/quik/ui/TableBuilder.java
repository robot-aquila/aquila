package ru.prolib.aquila.quik.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.quik.dde.Cache;
import ru.prolib.aquila.quik.dde.OrderCache;
import ru.prolib.aquila.quik.dde.TradeCache;
import ru.prolib.aquila.ui.ClassLabels;

public class TableBuilder {
	private static final SimpleDateFormat dateFormat;
	
	static {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
				return dateFormat.format(((OrderCache) obj).getTime());
			}
		}, Column.lONG));
		columns.add(new Column("COL_CACHE_ORDER_WITHDRAW_TIME", new G<Object>() {
			@Override public Object get(Object obj) {
				Date time = ((OrderCache) obj).getWithdrawTime();
				return time == null ? null : dateFormat.format(time);
			}
		}, Column.lONG));
		columns.add(new Column("COL_CACHE_ORDER_TYPE", new G<Object>() {
			@Override public Object get(Object obj) {
				return ((OrderCache) obj).getType();
			}
		}, Column.SHORT));
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
		}, Column.lONG));
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
		return new Table(new TradesCacheTableModel(labels,
				columns, cache.getTradesCache()));
	}

}
