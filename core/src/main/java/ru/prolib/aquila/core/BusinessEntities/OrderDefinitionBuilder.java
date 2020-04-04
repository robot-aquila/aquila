package ru.prolib.aquila.core.BusinessEntities;

public class OrderDefinitionBuilder {
	private Account account;
	private Symbol symbol;
	private OrderType type = OrderType.LMT;
	private OrderAction action;
	private CDecimal qty, price;
	private String comment;
	private Long maxExecTime;
	
	public OrderDefinitionBuilder withAccount(Account account) {
		this.account = account;
		return this;
	}
	
	public OrderDefinitionBuilder withSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}
	
	public OrderDefinitionBuilder withType(OrderType type) {
		this.type = type;
		return this;
	}
	
	public OrderDefinitionBuilder withAction(OrderAction action) {
		this.action = action;
		return this;
	}
	
	public OrderDefinitionBuilder withQty(CDecimal qty) {
		this.qty = qty;
		return this;
	}
	
	public OrderDefinitionBuilder withPrice(CDecimal price) {
		this.price = price;
		return this;
	}
	
	public OrderDefinitionBuilder withComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public OrderDefinitionBuilder withMaxExecutionTime(long time_millis) {
		this.maxExecTime = time_millis;
		return this;
	}
	
	public OrderDefinitionBuilder withLimitBuy(CDecimal qty, CDecimal price) {
		return withType(OrderType.LMT).withAction(OrderAction.BUY).withQty(qty).withPrice(price);
	}

	public OrderDefinitionBuilder withLimitSell(CDecimal qty, CDecimal price) {
		return withType(OrderType.LMT).withAction(OrderAction.SELL).withQty(qty).withPrice(price);
	}

	public OrderDefinitionBuilder withLimitCover(CDecimal qty, CDecimal price) {
		return withType(OrderType.LMT).withAction(OrderAction.COVER).withQty(qty).withPrice(price);
	}
	
	public OrderDefinitionBuilder withLimitSellShort(CDecimal qty, CDecimal price) {
		return withType(OrderType.LMT).withAction(OrderAction.SELL_SHORT).withQty(qty).withPrice(price);
	}
	
	public OrderDefinitionBuilder withMarketBuy(CDecimal qty) {
		return withType(OrderType.MKT).withAction(OrderAction.BUY).withQty(qty).withPrice(null);
	}
	
	public OrderDefinitionBuilder withMarketSell(CDecimal qty) {
		return withType(OrderType.MKT).withAction(OrderAction.SELL).withQty(qty).withPrice(null);
	}
	
	public OrderDefinitionBuilder withMarketCover(CDecimal qty) {
		return withType(OrderType.MKT).withAction(OrderAction.COVER).withQty(qty).withPrice(null);
	}
	
	public OrderDefinitionBuilder withMarketSellShort(CDecimal qty) {
		return withType(OrderType.MKT).withAction(OrderAction.SELL_SHORT).withQty(qty).withPrice(null);
	}
	
	public OrderDefinition buildDefinition() {
		if ( account == null ) {
			throw new IllegalStateException("Account was not defined");
		}
		if ( symbol == null ) {
			throw new IllegalStateException("Symbol was not defined");
		}
		if ( type == null ) {
			throw new IllegalStateException("Type was not defined");
		}
		if ( action == null ) {
			throw new IllegalStateException("Action was not specified");
		}
		if ( qty == null ) {
			throw new IllegalStateException("Quantity was not specified");
		}
		if ( OrderType.LMT.equals(type) ) {
			if ( price == null ) {
				throw new IllegalStateException("Price expected to be not null for " + type + " order type");
			}
		} else
		if ( OrderType.MKT.equals(type) ) {
			if ( price != null ) {
				throw new IllegalStateException("Price expected to be null for " + type + " order type");
			}
		}
		if ( maxExecTime == null ) {
			throw new IllegalStateException("Max execution time was not specified");
		}
		OrderDefinition result = new OrderDefinition(
				account,
				symbol,
				type,
				action,
				qty,
				price,
				comment,
				maxExecTime
			);
		action = null;
		qty = null;
		price = null;
		return result;
	}
	
}
