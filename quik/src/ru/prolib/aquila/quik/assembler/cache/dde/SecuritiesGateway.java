package ru.prolib.aquila.quik.assembler.cache.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.quik.assembler.Assembler;
import ru.prolib.aquila.quik.assembler.cache.SecurityEntry;

/**
 * Шлюз таблицы инструментов.
 * <p>
 * Инструменты являются независимыми от других сущностей объектами и по этому
 * не нуждаются в кэшировании. Конвертирует входные данные в приемлимую для
 * обработки форму и передает на обработку сборщику.
 */
public class SecuritiesGateway implements TableGateway {
	private static final String LOT_SIZE = "lotsize";
	private static final String PRICE_MAX = "pricemax";
	private static final String PRICE_MIN = "pricemin";
	private static final String STEP_PRICE = "steppricet";
	private static final String PRICE_STEP = "SEC_PRICE_STEP";
	private static final String SCALE = "SEC_SCALE";
	private static final String CODE = "CODE";
	private static final String CLASS_CODE = "CLASS_CODE";
	private static final String LAST = "last";
	private static final String OPEN = "open";
	private static final String CLOSE = "prevlegalclosepr";
	private static final String DISPNAME = "LONGNAME";
	private static final String SHORTNAME = "SHORTNAME";
	private static final String ASK = "offer";
	private static final String BID = "bid";
	private static final String HIGH = "high";
	private static final String LOW = "low";
	private static final String CURRENCY = "curstepprice";
	private static final String TYPE = "CLASSNAME";
	private static final String REQUIRED_HEADERS[] = {
		LOT_SIZE,
		PRICE_MAX,
		PRICE_MIN,
		STEP_PRICE,
		PRICE_STEP,
		SCALE,
		CODE,
		CLASS_CODE,
		LAST,
		OPEN,
		CLOSE,
		DISPNAME,
		SHORTNAME,
		ASK,
		BID,
		HIGH,
		LOW,
		CURRENCY,
		TYPE,
	};
	
	private static final String DEFAULT_CURRENCY = "SUR";
	private static final SecurityType DEFAULT_TYPE = SecurityType.STK;
	private static final Map<String, SecurityType> TYPE_MAP;
	
	static {
		TYPE_MAP = new HashMap<String, SecurityType>();
		TYPE_MAP.put("ФОРТС фьючерсы", SecurityType.FUT);
	}

	private final Assembler asm;
	private final RowDataConverter converter;
	
	public SecuritiesGateway(RowDataConverter converter, Assembler asm) {
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
	public void process(Row row) throws DDEException {
		try {
			asm.assemble(new SecurityEntry(converter.getInteger(row, LOT_SIZE),
					converter.getDoubleOrNull(row, PRICE_MAX),
					converter.getDoubleOrNull(row, PRICE_MIN),
					converter.getDoubleOrNull(row, STEP_PRICE),
					converter.getDouble(row, PRICE_STEP),
					converter.getInteger(row, SCALE),
					converter.getDoubleOrNull(row, LAST),
					converter.getDoubleOrNull(row, OPEN),
					converter.getDoubleOrNull(row, CLOSE),
					converter.getString(row, DISPNAME),
					converter.getString(row, SHORTNAME),
					converter.getDoubleOrNull(row, ASK),
					converter.getDoubleOrNull(row, BID),
					converter.getDoubleOrNull(row, HIGH),
					converter.getDoubleOrNull(row, LOW),
					(SecurityDescriptor) getDescriptor(row)));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}

	/**
	 * Сформировать дескриптор инструмента.
	 * <p>
	 * @param row ряд
	 * @return дескриптор
	 * @throws DDEException
	 */
	private SecurityDescriptor getDescriptor(Row row) throws DDEException {
		try {
			return new SecurityDescriptor(converter.getString(row, CODE),
				converter.getString(row, CLASS_CODE),
				getCurrency(row), getType(row));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}
	
	/**
	 * Получить код валюты шага цены.
	 * <p>
	 * Возвращает код валюты по-умолчанию, если соответствующее значение ряда
	 * пустая строка.
	 * <p>
	 * @param row ряд
	 * @return код валюты
	 * @throws ValueException
	 */
	private String getCurrency(Row row) throws ValueException {
		String currency = converter.getString(row, CURRENCY);
		if ( currency.length() == 0 ) {
			currency = DEFAULT_CURRENCY;
		}
		return currency;
	}
	
	/**
	 * Получить тип инструмента.
	 * <p>
	 * Возвращает тип инструмента по-умолчанию, если соответствующего типа
	 * нет в карте типов.
	 * <p>
	 * @param row ряд
	 * @return тип инструмента
	 * @throws ValueException
	 */
	private SecurityType getType(Row row) throws ValueException {
		String strType = converter.getString(row, TYPE);
		SecurityType type = TYPE_MAP.get(strType);
		if ( type == null ) {
			type = DEFAULT_TYPE;
		}
		return type;
	}

	@Override
	public boolean shouldProcess(Row row) throws DDEException {
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecuritiesGateway.class ) {
			return false;
		}
		SecuritiesGateway o = (SecuritiesGateway) other;
		return new EqualsBuilder()
			.append(o.asm, asm)
			.append(o.converter, converter)
			.isEquals();
	}

	@Override
	public boolean shouldProcessRowByRow(TableMeta meta, RowSet rs) throws DDEException {
		return true;
	}

}
