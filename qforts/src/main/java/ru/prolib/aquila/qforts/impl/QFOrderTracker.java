package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.Order;

public interface QFOrderTracker {
	void startTrackingOrder(Order order);
	void stopTrackingOrder(Order order);
}
