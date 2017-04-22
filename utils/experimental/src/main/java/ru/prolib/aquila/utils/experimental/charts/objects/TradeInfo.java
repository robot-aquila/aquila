package ru.prolib.aquila.utils.experimental.charts.objects;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

import java.time.Instant;

/**
 * Created by TiM on 19.04.2017.
 */
public class TradeInfo {
    private final Instant time;
    private final OrderAction action;
    private final double price;
    private final long volume;

    private Symbol symbol;
    private Account account;
    private Long orderId;

    public TradeInfo(Instant time, OrderAction action, double price, long volume) {
        this.time = time;
        this.action = action;
        this.price = price;
        this.volume = volume;
    }

    public Instant getTime() {
        return time;
    }

    public OrderAction getAction() {
        return action;
    }

    public double getPrice() {
        return price;
    }

    public long getVolume() {
        return volume;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public TradeInfo withSymbol(Symbol symbol) {
        this.symbol = symbol;
        return this;
    }

    public Account getAccount() {
        return account;
    }

    public TradeInfo withAccount(Account account) {
        this.account = account;
        return this;
    }

    public Long getOrderId() {
        return orderId;
    }

    public TradeInfo withOrderId(Long orderId) {
        this.orderId = orderId;
        return this;
    }
}
