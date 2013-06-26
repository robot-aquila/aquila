package ru.prolib.aquila.ib.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ib.client.ContractDetails;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.ib.assembler.cache.*;
import ru.prolib.aquila.ui.ClassLabels;
import ru.prolib.aquila.ui.table.*;

/**
 * Конструктор параметров таблиц для отображения содержимого кэша данных.
 */
public class IBCacheTableBuilder {
	private static final SimpleDateFormat dateFormatMs;
	private static final SimpleDateFormat dateFormat;
	
	static {
		dateFormatMs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	private final ClassLabels labels;
	private final Cache cache;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param labels языковой файл
	 * @param cache кэш данных
	 */
	public IBCacheTableBuilder(ClassLabels labels, Cache cache) {
		super();
		this.labels = labels;
		this.cache = cache;
	}
	
	/**
	 * Создать параметры таблицы кэша контрактов.
	 * <p> 
	 * @return параметры таблицы
	 */
	public Table createContractTable() {
		Columns columns = new Columns();
		columns.add(new Column("CACHE_CONTR_COL_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).getContractId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_CONTR_COL_SECDESCR", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).getSecurityDescriptor();
			}
		}, Column.LONG));
		columns.add(new Column("CACHE_CONTR_COL_DISPNAME", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).getDisplayName();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_CONTR_COL_TYPE", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).getType();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_CONTR_COL_MINSTEPPRICE", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).getMinStepPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_CONTR_COL_MINSTEPSIZE", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).getMinStepSize();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_CONTR_COL_PREC", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).getPrecision();
			}
		}, Column.SHORT));
		columns.add(new Column("CACHE_CONTR_COL_VALIDEXCH", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				ContractDetails d=((ContractEntry) source).getContractDetails();
				return d == null ? d : d.m_validExchanges;
			}
		}, Column.LONG));
		columns.add(new Column("CACHE_CONTR_COL_ISSMART", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ContractEntry) source).isSmart() ? "yes" : "no";
			}
		}, Column.SHORT));
		columns.add(new Column("COL_ENTRY_TIME", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				Date time = ((ContractEntry) source).getEntryTime();
				return time == null ? null : dateFormatMs.format(time);
			}
		}, Column.LONG));
		return new Table(new IBContractCacheTableModel(labels, columns, cache));
	}
	
	public Table createExecTable() {
		Columns columns = new Columns();
		columns.add(new Column("CACHE_EXEC_COL_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ExecEntry) source).getId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_EXEC_COL_NATIVE_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ExecEntry) source).getNativeId();
			}
		}, Column.LONG));
		columns.add(new Column("CACHE_EXEC_COL_ORDER_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ExecEntry) source).getOrderId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_EXEC_COL_ACCOUNT", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ExecEntry) source).getAccount();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_EXEC_COL_CONTR_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ExecEntry) source).getContractId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_EXEC_COL_PRICE", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ExecEntry) source).getPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_EXEC_COL_QTY", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((ExecEntry) source).getQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_EXEC_COL_TIME", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				Date time = ((ExecEntry) source).getTime();
				return time == null ? null : dateFormat.format(time);
			}
		}, Column.LONG));
		columns.add(new Column("COL_ENTRY_TIME", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				Date time = ((ExecEntry) source).getEntryTime();
				return time == null ? null : dateFormatMs.format(time);
			}
		}, Column.LONG));
		return new Table(new IBExecCacheTableModel(labels, columns, cache));
	}
	
	public Table createOrderTable() {
		Columns columns = new Columns();
		columns.add(new Column("CACHE_ORDER_COL_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDER_COL_ACCOUNT", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getAccount();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDER_COL_DIR", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getDirection();
			}
		}, Column.SHORT));
		columns.add(new Column("CACHE_ORDER_COL_CONTR_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getContractId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDER_COL_PRICE", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDER_COL_QTY", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDER_COL_STATUS", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getStatus();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDER_COL_STPLMT", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getStopLimitPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDER_COL_TYPE", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderEntry) source).getType();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_ENTRY_TIME", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				Date time = ((OrderEntry) source).getEntryTime();
				return time == null ? null : dateFormatMs.format(time);
			}
		}, Column.LONG));
		return new Table(new IBOrderCacheTableModel(labels, columns, cache));
	}
	
	public Table createOrderStatusTable() {
		Columns columns = new Columns();
		columns.add(new Column("CACHE_ORDERST_COL_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderStatusEntry) source).getId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDERST_COL_STATUS", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderStatusEntry) source).getStatus();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDERST_COL_NAT_STATUS", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderStatusEntry) source).getNativeStatus();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDERST_COL_AVG_PRICE", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderStatusEntry) source).getAvgExecutedPrice();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_ORDERST_COL_REST", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((OrderStatusEntry) source).getQtyRest();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_ENTRY_TIME", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				Date time = ((OrderStatusEntry) source).getEntryTime();
				return time == null ? null : dateFormatMs.format(time);
			}
		}, Column.LONG));
		return
			new Table(new IBOrderStatusCacheTableModel(labels, columns, cache));
	}
	
	public Table createPositionTable() {
		Columns columns = new Columns();
		columns.add(new Column("CACHE_POSITION_COL_ACCOUNT", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((PositionEntry) source).getAccount();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_POSITION_COL_CONTR_ID", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((PositionEntry) source).getContractId();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_POSITION_COL_QTY", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((PositionEntry) source).getQty();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_POSITION_COL_BOOKVAL", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((PositionEntry) source).getBookValue();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_POSITION_COL_MKTVAL", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((PositionEntry) source).getMarketValue();
			}
		}, Column.MIDDLE));
		columns.add(new Column("CACHE_POSITION_COL_VMARGIN", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				return ((PositionEntry) source).getVarMargin();
			}
		}, Column.MIDDLE));
		columns.add(new Column("COL_ENTRY_TIME", new G<Object>() {
			@Override
			public Object get(Object source) throws ValueException {
				Date time = ((PositionEntry) source).getEntryTime();
				return time == null ? null : dateFormatMs.format(time);
			}
		}, Column.LONG));
		return new Table(new IBPositionCacheTableModel(labels, columns, cache));
	}

}
