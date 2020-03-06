package ru.prolib.aquila.core.BusinessEntities;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;

public class L1UpdateBuilder {
	private Instant time = Instant.EPOCH;
	private TickType type = TickType.TRADE;
	private Symbol symbol;
	private CDecimal price = CDecimalBD.ZERO;
	private CDecimal size = CDecimalBD.ZERO;
	private CDecimal value = ZERO;
	private String comment;
	
	public L1UpdateBuilder(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public L1UpdateBuilder() {
		this(null);
	}
	
	public L1UpdateBuilder withTime(Instant time) {
		this.time = time;
		return this;
	}
	
	public L1UpdateBuilder withTime(String timeString) {
		return withTime(Instant.parse(timeString));
	}
	
	public L1UpdateBuilder withSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}
	
	public L1UpdateBuilder withType(TickType type) {
		this.type = type;
		return this;
	}
	
	public L1UpdateBuilder withAsk() {
		return withType(TickType.ASK);
	}
	
	public L1UpdateBuilder withBid() {
		return withType(TickType.BID);
	}
	
	public L1UpdateBuilder withTrade() {
		return withType(TickType.TRADE);
	}
	
	public L1UpdateBuilder withPrice(CDecimal price) {
		this.price = price;
		return this;
	}
	
	public L1UpdateBuilder withPrice(String price) {
		return withPrice(CDecimalBD.of(price));
	}
	
	public L1UpdateBuilder withPrice(long price) {
		return withPrice(CDecimalBD.of(price));
	}
	
	public L1UpdateBuilder withSize(CDecimal size) {
		this.size = size;
		return this;
	}
	
	public L1UpdateBuilder withSize(String size) {
		return withSize(CDecimalBD.of(size));
	}
	
	public L1UpdateBuilder withSize(long size) {
		return withSize(CDecimalBD.of(size));
	}
	
	public L1UpdateBuilder withComment(String text) {
		this.comment = text;
		return this;
	}
	
	public L1UpdateBuilder fromTick(Tick source) {
		this.price = source.getPrice();
		this.size = source.getSize();
		this.time = source.getTime();
		this.type = source.getType();
		this.comment = source.getComment();
		this.value = source.getValue();
		return this;
	}
	
	public L1Update buildL1Update() {
		if ( type == null ) {
			throw new IllegalStateException("Undefined tick type");
		}
		if ( time == null ) {
			throw new IllegalStateException("Undefined time");
		}
		if ( symbol == null ) {
			throw new IllegalStateException("Undefined symbol");
		}
		return new L1UpdateImpl(symbol, new Tick(type, time, price, size, value, comment));
	}

}
