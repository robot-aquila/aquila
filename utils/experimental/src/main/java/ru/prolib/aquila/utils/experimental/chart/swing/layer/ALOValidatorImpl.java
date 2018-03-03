package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.util.HashSet;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Typical active limit order validator with supporting filter by symbol and account.
 */
public class ALOValidatorImpl implements ALOValidator {
	private final Set<Symbol> filterBySymbol;
	private final Set<Account> filterByAccount;
	
	ALOValidatorImpl(Set<Symbol> filterBySymbol, Set<Account> filterByAccount) {
		this.filterBySymbol = filterBySymbol;
		this.filterByAccount = filterByAccount;
	}
	
	public ALOValidatorImpl() {
		this(new HashSet<>(), new HashSet<>());
	}
	
	public ALOValidatorImpl(Symbol symbol) {
		this();
		addFilterBySymbol(symbol);
	}
	
	public synchronized Set<Symbol> getFiltersBySymbol() {
		return filterBySymbol;
	}
	
	public synchronized Set<Account> getFiltersByAccount() {
		return filterByAccount;
	}
	
	public synchronized void addFilterBySymbol(Symbol symbol) {
		filterBySymbol.add(symbol);
	}
	
	public synchronized void removeFilterBySymbol(Symbol symbol) {
		filterBySymbol.remove(symbol);
	}
	
	public synchronized void removeFiltersBySymbol() {
		filterBySymbol.clear();
	}
	
	public synchronized void addFilterByAccount(Account account) {
		filterByAccount.add(account);
	}
	
	public synchronized void removeFilterByAccount(Account account) {
		filterByAccount.remove(account);
	}
	
	public synchronized void removeFiltersByAccount() {
		filterByAccount.clear();
	}

	@Override
	public synchronized boolean isValid(Order order) {
		if ( order.getType() != OrderType.LMT || ! order.getStatus().isActive() ) {
			return false;
		}
		if ( filterBySymbol.size() > 0 && ! filterBySymbol.contains(order.getSymbol()) ) {
			return false;
		}
		if ( filterByAccount.size() > 0 && ! filterByAccount.contains(order.getAccount()) ) {
			return false;
		}
		return true;
	}

}
