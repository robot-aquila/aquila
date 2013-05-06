package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;

/**
 * Базовый обработчик кэширования таблицы типа зеркало.
 */
public class MirrorTableHandler implements DDETableHandler {
	private final MirrorTableHelper helper;
	
	public MirrorTableHandler(MirrorTableHelper helper) {
		super();
		this.helper = helper;
	}
	
	public MirrorTableHandler(CacheGateway gateway) {
		this(new MirrorTableHelper(gateway));
	}

	@Override
	public synchronized void handle(DDETable ddeTable) throws DDEException {
		try {
			handleTable(ddeTable);
		} catch ( Exception e ) {
			throw new DDETableImportException(ddeTable.getTopic(), e);
		}
	}
	
	/**
	 * Обработать данные таблицы.
	 * <p>
	 * @param ddeTable таблица
	 * @throws DDEException ошибка уровня конвертации данных
	 * @throws RowSetException ошибка позицирования ряда
	 */
	private void handleTable(DDETable ddeTable)
			throws DDEException, RowSetException
	{
		TableMeta meta = helper.createTableMeta(ddeTable);
		if ( meta.hasHeaderRow() ) {
			helper.updateHeaders(ddeTable);
			helper.getCacheGateway().clearCache();
		}
		RowSet rs = helper.createRowSet(meta, ddeTable);
		helper.checkRowSetChanged(meta, rs);
		helper.cacheRowSet(meta, rs);
		helper.getCacheGateway().fireUpdateCache();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == MirrorTableHandler.class ) {
			MirrorTableHandler o = (MirrorTableHandler) other;
			return new EqualsBuilder()
				.append(helper, o.helper)
				.isEquals();
		} else {
			return false;
		}
	}

}
