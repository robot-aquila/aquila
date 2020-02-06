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

public interface IQFObjectRegistry {

	boolean isRegistered(Portfolio portfolio);

	boolean isRegistered(Security security);

	boolean isRegistered(Order order);

	void register(EditablePortfolio portfolio);

	void register(EditableSecurity security);

	void register(EditableOrder order);

	List<EditableSecurity> getSecurityList();

	List<EditablePortfolio> getPortfolioList();

	List<EditableOrder> getOrderList(Symbol symbol, CDecimal price);

	void purgeOrder(Order order);

}