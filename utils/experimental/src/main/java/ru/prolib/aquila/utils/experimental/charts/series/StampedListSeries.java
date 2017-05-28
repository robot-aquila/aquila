package ru.prolib.aquila.utils.experimental.charts.series;

import org.threeten.extra.Interval;
import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by TiM on 27.05.2017.
 */
public class StampedListSeries<T extends TStamped> extends SeriesImpl<List<T>> {

    protected final TimeFrame timeFrame;
    private final EventType onAdd;
    private final EventQueue eventQueue;

    public StampedListSeries(String valueId, TimeFrame timeFrame, EventQueue eventQueue) {
        super(valueId);
        this.timeFrame = timeFrame;
        this.eventQueue = eventQueue;
        onAdd = new EventTypeImpl("STAMPED_ADD");

    }

    public StampedListSeries(String valueId, TimeFrame timeFrame, EventQueue eventQueue, List<T> initialList) throws ValueException {
        this(valueId, timeFrame, eventQueue);
        List<T> list = initialList.stream().sorted(Comparator.comparing(T::getTime)).collect(Collectors.toList());
        for(T t: list){
            add(t);
        }
    }

    public synchronized void add(T value) throws ValueException {
        Interval interval = timeFrame.getInterval(value.getTime());
        List<T> list = null;
        try {
            list = get();
        } catch (ValueException e){
            list = new Vector<>();
            super.add(list);
        }

        if(list.size()>0){
            Interval lastInterval = timeFrame.getInterval(list.get(0).getTime());
            if(interval.equals(lastInterval)){
                list.add(value);
            } else if(interval.getStart().isAfter(lastInterval.getStart())){
                list = new Vector<>();
                super.add(list);
                list.add(value);
            } else {
                throw new ValueException("We can change only last value");
            }

        } else {
            list.add(value);
        }
        fireAdd(value);
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public EventType onAdd() {
        return onAdd;
    }

    private void fireAdd(T data){
        eventQueue.enqueue(onAdd, new StampedEventFactory<T>(data));
    }
}
