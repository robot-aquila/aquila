package ru.prolib.aquila.web.utils.moex.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.PriceScaleDB;
import ru.prolib.aquila.web.utils.moex.MoexContractField;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

public class MoexPriceScaleDB implements PriceScaleDB {
	static final Logger logger = LoggerFactory.getLogger(MoexPriceScaleDB.class);
	public static final int SYMBOL_DATA_ERROR = Integer.MAX_VALUE;
	public static final int SYMBOL_NOT_FOUND = SYMBOL_DATA_ERROR - 1;
	public static final int DEFAULT_SCALE = 5;
	private final Map<Symbol, Integer> scaleMap;
	private final MoexContractFileStorage storage;
	
	public MoexPriceScaleDB(MoexContractFileStorage storage, Map<Symbol, Integer> scale_map) {
		this.scaleMap = scale_map;
		this.storage = storage;
	}
	
	public MoexPriceScaleDB(MoexContractFileStorage storage) {
		this(storage, new HashMap<>());
	}

	@Override
	public synchronized int getScale(Symbol symbol) {
		Integer scale = scaleMap.get(symbol);
		if ( scale == null ) {
			scale = loadScale(symbol);
			scaleMap.put(symbol, scale);
		}
		if ( scale == SYMBOL_DATA_ERROR ) {
			throw new IllegalStateException("Reading symbol data error (permanent): " + symbol);
		} else if ( scale == SYMBOL_NOT_FOUND ) {
			return DEFAULT_SCALE;
		} else {
			return scale;
		}
	}
	
	private int loadScale(Symbol symbol) {
		try ( CloseableIterator<DeltaUpdate> reader = storage.createReader(symbol) ) {
			while ( reader.next() ) {
				Map<Integer, Object> data = reader.item().getContents();
				if ( data.containsKey(MoexContractField.TICK_SIZE) ) {
					return ((CDecimal) data.get(MoexContractField.TICK_SIZE)).getScale();
				}
			}
		} catch ( IOException e ) {
			logger.error("Error reading symbol data: {}", symbol, e);
			return SYMBOL_DATA_ERROR;
		}
		logger.warn("Symbol data not found: {}", symbol);
		return SYMBOL_NOT_FOUND;
	}

}
