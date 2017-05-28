package ru.prolib.aquila.utils.experimental.charts.series;

import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * Created by TiM on 25.05.2017.
 */
public class StampedDataEvent<T extends TStamped> extends EventImpl {

    private final T data;

    public StampedDataEvent(EventType type, T data) {
        super(type);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
