package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventProducer;
import ru.prolib.aquila.core.concurrency.Lockable;

public interface BusinessEntity extends Lockable, EventProducer {

}
