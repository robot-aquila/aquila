package ru.prolib.aquila.utils.experimental.chart.swing.layers;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TStamped;

import java.time.Instant;

/**
 * Created by TiM on 19.04.2017.
 */
public class TradeInfo implements TStamped {
    private final Instant time;
    private final OrderAction action;
    private final CDecimal price, volume;

    private Symbol symbol;
    private Account account;
    private Long orderId;

    public TradeInfo(Instant time, OrderAction action, CDecimal price, CDecimal volume) {
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

    public CDecimal getPrice() {
        return price;
    }

    public CDecimal getVolume() {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("time", time)
                .append("action", action)
                .append("price", price)
                .append("volume", volume)
                .append("symbol", symbol)
                .append("account", account)
                .append("orderId", orderId)
                .toString();
    }
}
