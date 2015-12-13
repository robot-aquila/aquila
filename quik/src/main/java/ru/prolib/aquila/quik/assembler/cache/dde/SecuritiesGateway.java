package ru.prolib.aquila.quik.assembler.cache.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.*;
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
	private static final String INITIAL_PRICE = "prevsettleprice";
	private static final String INITIAL_MARGIN_BUY = "buydepo";
	private static final String INITIAL_MARGIN_SELL = "selldepo";
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
		
		// При добавлении всех имеющихся параметров при наличии в таблице только
		// фьючей, этих параметров в таблице не появляется. Если указывать
		// их как обязательные, то будет исключение на этапе валидации
		// заголовков. Следовательно, делаем их опциональными.
		//OPEN,
		//CLOSE,
		
		DISPNAME,
		SHORTNAME,
		ASK,
		BID,
		HIGH,
		LOW,
		CURRENCY,
		INITIAL_PRICE,
		INITIAL_MARGIN_BUY,
		INITIAL_MARGIN_SELL,
	};
	
	/**
	 * Карта маппинга нестандартных кодов валют. Стандартные коды преобразуются
	 * в валюту через {@link Currency#valueOf(String)}.
	 */
	private static final Map<String, Currency> FIX_CURRENCY_MAP;
	
	private static final SymbolType DEFAULT_TYPE = SymbolType.STOCK;
	private static final Map<String, SymbolType> TYPE_MAP;
	
	static {
		TYPE_MAP = new HashMap<String, SymbolType>();
		TYPE_MAP.put("SPBFUT", SymbolType.FUTURE);
		TYPE_MAP.put("SPBOPT", SymbolType.OPTION);
		TYPE_MAP.put("EQOB", SymbolType.BOND);
		TYPE_MAP.put("EQOV", SymbolType.BOND);
		TYPE_MAP.put("EQNB", SymbolType.BOND);
		TYPE_MAP.put("EQDB", SymbolType.BOND);
		TYPE_MAP.put("TQOB", SymbolType.BOND);
		
		FIX_CURRENCY_MAP = new HashMap<String, Currency>();
		FIX_CURRENCY_MAP.put("SUR", ISO4217.RUB);
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
					converter.getString(row, CODE),
					converter.getString(row, CLASS_CODE),
					getCurrency(row),
					getType(row),
					converter.getDoubleOrNull(row, INITIAL_PRICE),
					converter.getDoubleOrNull(row, INITIAL_MARGIN_BUY)));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}
	
	/**
	 * Получить валюту шага цены.
	 * <p>
	 * @param row ряд
	 * @return валюта
	 * @throws ValueException ошибка доступа к элементу ряда
	 * @throws ValueNotExistsException валюта с таким кодом не найдена или
	 * пустое значение кода валюты
	 */
	private Currency getCurrency(Row row) throws ValueException {
		String code = converter.getString(row, CURRENCY);
		if ( code.length() == 0 ) {
			throw new ValueException("Zero currency code");
		}
		Currency currency = FIX_CURRENCY_MAP.get(code);
		if ( currency == null ) {
			try {
				currency = Currency.getInstance(code);
			} catch ( IllegalArgumentException e ) {
				throw new ValueException("Unknown currency: " + code);
			}
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
	private SymbolType getType(Row row) throws ValueException {
		String strType = converter.getString(row, CLASS_CODE);
		SymbolType type = TYPE_MAP.get(strType);
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
	public boolean shouldProcessRowByRow(TableMeta meta, RowSet rs)
		throws DDEException
	{
		return true;
	}

}
