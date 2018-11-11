package ru.prolib.aquila.core.eque;

import java.util.List;

public interface DispatchingMethod {
	void dispatch(List<DeliveryEventTask> tasks);
	void shutdown();
}