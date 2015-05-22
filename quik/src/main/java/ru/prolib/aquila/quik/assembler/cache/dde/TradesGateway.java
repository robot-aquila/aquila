package ru.prolib.aquila.quik.assembler.cache.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.Assembler;
import ru.prolib.aquila.quik.assembler.cache.TradesEntry;

/**
 * Шлюз таблицы всех сделок.
 * <p>
 * Поскольку объем передаваемых данных для этой таблицы может быть очень большим
 * и на обработку может потребоваться много времени, обработка данной таблицы
 * отличается от других. Что бы избежать отвала передающей стороны по таймауту,
 * данные этой таблицы кэшируются. Помимо отложенной обработки, кэширование
 * таблицы целиком так же решает проблему согласования сделок по инструментам:
 * на момент поступления данных о сделках не все инструменты могут быть
 * доступны.
 * <p>
 * Данный класс заворачивает таблицу в кэш-запись, которую передает на обработку
 * сборщику объектов. В последствии, созданная кэш-запись обращается к
 * создавшему ее экземпляру для формирования объекта сделки на основании ряда. 
 */
public class TradesGateway implements TableGateway {
	private static final String ID = "TRADENUM";
	private static final String DATE = "TRADEDATE";
	private static final String TIME = "TRADETIME";
	private static final String SEC_CODE = "SECCODE";
	private static final String SEC_CLASS = "CLASSCODE";
	private static final String PRICE = "PRICE";
	private static final String QTY = "QTY";
	private static final String DIR = "BUYSELL";
	private static final String VALUE = "VALUE";
	private static final String MICROSECONDS = "TRADETIME_MSEC";
	private static final String REQUIRED_HEADERS[] = {
		ID,
		DATE,
		TIME,
		SEC_CODE,
		SEC_CLASS,
		PRICE,
		QTY,
		DIR,
		VALUE,
		MICROSECONDS,
	};
	private static final Map<String, Direction> DIR_MAP;
	
	static {
		DIR_MAP = new Hashtable<String, Direction>();
		DIR_MAP.put("BUY", Direction.BUY);
		DIR_MAP.put("SELL", Direction.SELL);
	}
	
	private final RowDataConverter converter;
	private final Assembler asm;
	
	public TradesGateway(RowDataConverter converter, Assembler asm) {
		super();
		this.converter = converter;
		this.asm = asm;
	}
	
	RowDataConverter getRowDataConverter() {
		return converter;
	}
	
	Assembler getAssembler() {
		return asm;
	}
	
	@Override
	public String[] getRequiredHeaders() {
		return REQUIRED_HEADERS;
	}

	@Override
	public boolean shouldProcess(Row row) throws DDEException {
		return false;
	}

	@Override
	public void process(Row row) throws DDEException {

	}

	@Override
	public boolean shouldProcessRowByRow(TableMeta meta, RowSet rs) {
		asm.assemble(new TradesEntry(this, rs, meta.getDataRowCount()));
		return false;
	}
	
	/**
	 * Создать сделку на основании ряда.
	 * <p>
	 * @param terminal терминал
	 * @param row ряд
	 * @return сделка или null, если не удалось создать сделку
	 * @throws RowException ошибка доступа к данным ряда
	 */
	public Trade makeTrade(QUIKTerminal terminal, Row row) throws RowException {
		try {
			SecurityDescriptor descr = terminal.getDataCache()
				.getDescriptor(converter.getString(row, SEC_CODE),
						converter.getString(row, SEC_CLASS));
			if ( descr == null ) {
				return null;
			}
			Trade trade  = new Trade(terminal);
			trade.setDirection((Direction)
					converter.getStringMappedTo(row, DIR, DIR_MAP));
			trade.setId(converter.getLong(row, ID));
			trade.setPrice(converter.getDouble(row, PRICE));
			trade.setQty(converter.getLong(row, QTY));
			trade.setSecurityDescriptor(descr);
			trade.setTime(converter.getTime(row, DATE, TIME, false)
				.plusMillis(converter.getInteger(row, MICROSECONDS) / 1000));
			trade.setVolume(converter.getDouble(row, VALUE));
			return trade;
		} catch ( ValueException e ) {
			throw new RowException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TradesGateway.class ) {
			return false;
		}
		TradesGateway o = (TradesGateway) other;
		return new EqualsBuilder()
			.append(o.asm, asm)
			.append(o.converter, converter)
			.isEquals();
	}

}
