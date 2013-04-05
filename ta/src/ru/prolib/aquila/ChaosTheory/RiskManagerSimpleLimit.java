package ru.prolib.aquila.ChaosTheory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Риск менеджер, ограничивающий максимальный размер позиции.
 * Использует объект состояния портфеля, для определения текущей позции.
 */
public class RiskManagerSimpleLimit implements RiskManager {
	private static final Logger logger = LoggerFactory.getLogger(RiskManagerSimpleLimit.class);
	private final int max;
	private final PortfolioState state;
	
	public RiskManagerSimpleLimit(PortfolioState state, int max) {
		super();
		this.state = state;
		this.max = max;
		logger.info("Configured with {} pcs. limit", max);
	}

	@Override
	public int getLongSize(double price) {
		try {
			return state.getPosition() >= max ? 0 : 1;
		} catch ( PortfolioException e ) {
			logger.error("Get position failed: {}", e.getMessage());
			return 0;
		}
	}

	@Override
	public int getShortSize(double price) {
		try {
			return state.getPosition() <= -max ? 0 : 1;
		} catch ( PortfolioException e ) {
			logger.error("Get position failed: {}", e.getMessage());
			return 0;
		}
	}
	
}