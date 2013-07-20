package ru.prolib.aquila.quik.assembler.cache.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;

/**
 * Типовой обработчик таблицы.
 * <p>
 * Обозначает структуру процесса обработки таблицы.
 */
public class TableHandler implements DDETableHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TableHandler.class);
	}
	
	private final TableHelper helper;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @param helper помощник обработчика таблицы
	 */
	TableHandler(TableHelper helper) {
		super();
		this.helper = helper;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param gateway шлюз таблицы
	 */
	public TableHandler(TableGateway gateway) {
		this(new TableHelper(gateway));
	}

	@Override
	public synchronized void handle(DDETable ddeTable) throws DDEException {
		try {
			handleTable(ddeTable);
		} catch ( Exception e ) {
			Object args[] = {ddeTable.getTopic(), e };
			logger.error("Error processing {}: ", args);
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
		}
		RowSet rs = helper.createRowSet(meta, ddeTable);
		helper.process(meta, rs);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TableHandler.class ) {
			return false;
		}
		TableHandler o = (TableHandler) other;
		return new EqualsBuilder()
			.append(helper, o.helper)
			.isEquals();
	}

}
