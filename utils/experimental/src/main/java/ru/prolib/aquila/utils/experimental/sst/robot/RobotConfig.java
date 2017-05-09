package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class RobotConfig {
	private final Symbol symbol;
	private final Account account;
	private final double share;
	
	public RobotConfig(Symbol symbol, Account account, double share) {
		this.symbol = symbol;
		this.account = account;
		this.share = share;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public double getShare() {
		return share;
	}

}
