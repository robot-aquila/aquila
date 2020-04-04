package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class OrderDefinition {
	protected final Account account;
	protected final Symbol symbol;
	protected final OrderType type;
	protected final OrderAction action;
	protected final CDecimal qty, price;
	protected final String comment;
	protected final long maxExecTime;
	
	public OrderDefinition(Account account,
			Symbol symbol,
			OrderType type,
			OrderAction action,
			CDecimal qty,
			CDecimal price,
			String comment,
			long max_exec_time)
	{
		this.account = account;
		this.symbol = symbol;
		this.type = type;
		this.action = action;
		this.qty = qty;
		this.price = price;
		this.comment = comment;
		this.maxExecTime = max_exec_time;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public OrderType getType() {
		return type;
	}
	
	public OrderAction getAction() {
		return action;
	}
	
	public CDecimal getQty() {
		return qty;
	}
	
	public CDecimal getPrice() {
		return price;
	}
	
	public String getComment() {
		return comment;
	}

	public long getMaxExecutionTime() {
		return maxExecTime;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(917623221, 715)
				.append(account)
				.append(symbol)
				.append(type)
				.append(action)
				.append(qty)
				.append(price)
				.append(comment)
				.append(maxExecTime)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderDefinition.class ) {
			return false;
		}
		OrderDefinition o = (OrderDefinition) other;
		return new EqualsBuilder()
				.append(o.account, account)
				.append(o.symbol, symbol)
				.append(o.type, type)
				.append(o.action, action)
				.append(o.qty, qty)
				.append(o.price, price)
				.append(o.comment, comment)
				.append(o.maxExecTime, maxExecTime)
				.build();
	}

}
