package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.util.HashSet;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class OEValidatorImpl implements OEValidator {
	private final Set<Symbol> filterBySymbol;
	private final Set<Account> filterByAccount;

	OEValidatorImpl(Set<Symbol> filterBySymbol, Set<Account> filterByAccount) {
		this.filterBySymbol = filterBySymbol;
		this.filterByAccount = filterByAccount;
	}
	
	public OEValidatorImpl() {
		this(new HashSet<>(), new HashSet<>());
	}
	
	public OEValidatorImpl(Symbol symbol) {
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
	public synchronized boolean isValid(OrderExecution execution) {
		if ( filterBySymbol.size() > 0 ) {
			if ( ! filterBySymbol.contains(execution.getSymbol()) ) {
				return false;
			}
		}
		if ( filterByAccount.size() > 0 ) {
			try {
				Account account = execution.getTerminal().getOrder(execution.getOrderID()).getAccount();
				if ( ! filterByAccount.contains(account) ) {
					return false;
				}
			} catch ( OrderException e ) {
				throw new IllegalStateException("Unable obtain order account", e);
			}
		}
		return true;
	}

}
