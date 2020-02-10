package ru.prolib.aquila.qforts.impl;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface QFObjectRegistry {

	boolean isRegistered(Portfolio portfolio);

	boolean isRegistered(Security security);

	boolean isRegistered(Order order);

	void register(EditablePortfolio portfolio);

	void register(EditableSecurity security);

	/**
	 * Register an order.
	 * <p>
	 * @param order - order instance
	 * @return true if order has been registered, false - otherwise (possible already registered)
	 */
	boolean register(EditableOrder order);

	List<EditableSecurity> getSecurityList();

	List<EditablePortfolio> getPortfolioList();

	List<EditableOrder> getOrderList(Symbol symbol, CDecimal price);

	/**
	 * Remove order from the registry.
	 * <p>
	 * @param order - order instance
	 * @return true if order has been removed, false - otherwise (order was not found)
	 */
	boolean purgeOrder(Order order);

}