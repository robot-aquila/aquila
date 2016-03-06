package ru.prolib.aquila.datatools.tickdatabase;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateImpl;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;

public class SimpleCsvL1UpdatePacker {
	private static final Map<TickType, String> tickTypeToCode;
	private static final Map<String, TickType> codeToTickType;
	
	static {
		tickTypeToCode = new HashMap<TickType, String>();
		tickTypeToCode.put(TickType.ASK, "A");
		tickTypeToCode.put(TickType.BID, "B");
		tickTypeToCode.put(TickType.TRADE, "T");
		codeToTickType = new HashMap<String, TickType>();
		codeToTickType.put("A", TickType.ASK);
		codeToTickType.put("B", TickType.BID);
		codeToTickType.put("T", TickType.TRADE);
	}
	
	public String pack(L1Update update) {
		Tick tick = update.getTick();
		Object tokens[] = {
				tickTypeToCode.get(tick.getType()),
				update.getSymbol(),
				tick.getTime(),
				tick.getPrice(),
				tick.getSize()
		};
		return StringUtils.join(tokens, ',');
	}
	
	public L1Update unpack(String record) throws SimpleCsvL1FormatException {
		String tokens[] = StringUtils.splitPreserveAllTokens(record, ',');
		if ( tokens.length != 5 ) {
			throw new SimpleCsvL1FormatException("Invalid number of fields: " + tokens.length);
		}
		TickType type = codeToTickType.get(tokens[0]);
		if ( type == null ) {
			throw new SimpleCsvL1FormatException("Invalid tick type code: " + tokens[0]);
		}
		Symbol symbol = null;
		try {
			symbol = new Symbol(tokens[1]);
		} catch ( IllegalArgumentException e ) {
			throw new SimpleCsvL1FormatException("Invalid symbol format: " + tokens[1], e);
		}
		Instant timestamp = null;
		try {
			timestamp = Instant.parse(tokens[2]);
		} catch ( DateTimeParseException e ) {
			throw new SimpleCsvL1FormatException("Invalid timestamp format: " + tokens[2], e);
		}
		Double price = null;
		try {
			price = Double.valueOf(tokens[3]);
		} catch ( NumberFormatException e ) {
			throw new SimpleCsvL1FormatException("Invalid price format: " + tokens[3], e);
		}
		Long size = null;
		try {
			size = Long.valueOf(tokens[4]);
		} catch ( NumberFormatException e ) {
			throw new SimpleCsvL1FormatException("Invalid size format: " + tokens[4], e);
		}
		Tick tick = Tick.of(type, timestamp, price, size);
		return new L1UpdateImpl(symbol, tick);
	}

}
