package ru.prolib.aquila.web.utils.moex;

import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;

/**
 * Converter class from MOEX updates to standard symbol updates.
 * <p>
 * <b>Note</b> this class is stateful. It stores some passed data all time to
 * fix scale of price and money values. <b>Do not use the single instance</b>
 * of this class to convert updates of different symbols.
 */
public class MoexContractSymbolUpdateConverter {
	private int priceScale = 0, tickValScale = 2, initMarginScale = 2;
	
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
			case MoexContractField.TICK_SIZE:
			case MoexContractField.LOWER_PRICE_LIMIT:
			case MoexContractField.UPPER_PRICE_LIMIT:
			case MoexContractField.SETTLEMENT_PRICE:
				priceScale = Math.max(priceScale, ((FDecimal) tokens.get(token)).getScale());
				break;
			case MoexContractField.TICK_VALUE:
				tickValScale = Math.max(tickValScale, ((FMoney) tokens.get(token)).getScale());
				break;
			case MoexContractField.INITIAL_MARGIN:
				initMarginScale = Math.max(initMarginScale, ((FMoney) tokens.get(token)).getScale());
				break;
			}
		}
		
		for ( int token : tokens.keySet() ) {
			switch ( token ) {
			case MoexContractField.SYMBOL:
				builder.withToken(SecurityField.DISPLAY_NAME, tokens.get(token));
				break;
			case MoexContractField.LOT_SIZE:
				builder.withToken(SecurityField.LOT_SIZE, tokens.get(token));
				break;
			case MoexContractField.TICK_SIZE:
				builder.withToken(SecurityField.TICK_SIZE,
						((FDecimal) tokens.get(token)).withScale(priceScale));
				break;
			case MoexContractField.TICK_VALUE:
				builder.withToken(SecurityField.TICK_VALUE,
						((FMoney) tokens.get(token)).withScale(tickValScale));
				break;
			case MoexContractField.LOWER_PRICE_LIMIT:
				builder.withToken(SecurityField.LOWER_PRICE_LIMIT,
						((FDecimal) tokens.get(token)).withScale(priceScale));
				break;
			case MoexContractField.UPPER_PRICE_LIMIT:
				builder.withToken(SecurityField.UPPER_PRICE_LIMIT,
						((FDecimal) tokens.get(token)).withScale(priceScale));
				break;
			case MoexContractField.SETTLEMENT_PRICE:
				builder.withToken(SecurityField.SETTLEMENT_PRICE,
						((FDecimal) tokens.get(token)).withScale(priceScale));
				break;
			case MoexContractField.INITIAL_MARGIN:
				builder.withToken(SecurityField.INITIAL_MARGIN,
						((FMoney) tokens.get(token)).withScale(initMarginScale));
				break;
			}
		}
		return builder.buildUpdate();
	}

}
