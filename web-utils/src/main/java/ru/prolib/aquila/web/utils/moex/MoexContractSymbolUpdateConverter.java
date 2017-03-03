package ru.prolib.aquila.web.utils.moex;

import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;

public class MoexContractSymbolUpdateConverter {
	
	public MoexContractSymbolUpdateConverter() {
		
	}
	
	/**
	 * Convert delta-update from MOEX tokens to standard model tokens.
	 * <p>
	 * see {@link ru.prolib.aquila.core.BusinessEntities.SecurityField SecurityField}.
	 * <p>
	 * @param source - original update with MOEX tokens
	 * @return update which contains standard security tokens
	 */
	public DeltaUpdate toSymbolUpdate(DeltaUpdate source) {
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
			.withSnapshot(source.isSnapshot())
			.withTime(source.getTime());
		Map<Integer, Object> tokens = source.getContents();
		for ( int token : tokens.keySet() ) {
			switch ( token ) {
			case MoexContractField.SYMBOL:
				builder.withToken(SecurityField.DISPLAY_NAME, tokens.get(token));
				break;
			case MoexContractField.LOT_SIZE:
				builder.withToken(SecurityField.LOT_SIZE, tokens.get(token));
				break;
			case MoexContractField.TICK_SIZE:
				builder.withToken(SecurityField.TICK_SIZE, tokens.get(token));
				break;
			case MoexContractField.TICK_VALUE:
				builder.withToken(SecurityField.TICK_VALUE, tokens.get(token));
				break;
			case MoexContractField.LOWER_PRICE_LIMIT:
				builder.withToken(SecurityField.LOWER_PRICE_LIMIT, tokens.get(token));
				break;
			case MoexContractField.UPPER_PRICE_LIMIT:
				builder.withToken(SecurityField.UPPER_PRICE_LIMIT, tokens.get(token));
				break;
			case MoexContractField.SETTLEMENT_PRICE:
				builder.withToken(SecurityField.SETTLEMENT_PRICE, tokens.get(token));
				break;
			case MoexContractField.INITIAL_MARGIN:
				builder.withToken(SecurityField.INITIAL_MARGIN, tokens.get(token));
				break;
			}
		}
		return builder.buildUpdate();
	}

}
