package ru.prolib.aquila.utils.experimental.swing_chart.series;

import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;

/**
 * Created by TiM on 28.05.2017.
 */
public class StampedEventFactory<T extends TStamped>  implements EventFactory {
    final T data;

    StampedEventFactory(T data) {
        this.data = data;
    }

    @Override
    public Event produceEvent(EventType type) {
        return new StampedDataEvent(type, data);
    }
}
