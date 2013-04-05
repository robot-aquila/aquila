package ru.prolib.aquila.ChaosTheory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Фиктивная стратегия риск-менеджмента.
 * Каждое увеличение позиции выполняется с фиксированным количеством в заявке.
 */
public class RiskManagerNone implements RiskManager {
	private static final Logger logger = LoggerFactory.getLogger(RiskManagerNone.class);
	private final int size;
	
	public RiskManagerNone(int size){
		super();
		this.size = size;
		logger.info("Configured with {} pcs. every order", size);
	}
	
	@Override
	public int getLongSize(double price) {
		return size;
	}

	@Override
	public int getShortSize(double price) {
		return size;
	}

}
