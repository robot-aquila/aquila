package ru.prolib.aquila.utils.experimental.charts.series;

import org.junit.Before;
import org.junit.Test;
import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by TiM on 27.05.2017.
 */
public class StampedListSeriesTest {

    private static class TStampedStub implements TStamped{
        private final Instant time;

        public TStampedStub(Instant time) {
            this.time = time;
        }

        @Override
        public Instant getTime() {
            return time;
        }
    }

    private StampedListSeries<TStampedStub> series;
    private Instant t1, t2, t3, t4, t5;
    private List<TStampedStub> list;

    @Before
    public void setUp() throws Exception {
        series = new StampedListSeries<>("STAMPED", TimeFrame.M1, new EventQueueImpl());
        t1 = Instant.parse("2017-05-13T02:50:10Z");
        t2 = Instant.parse("2017-05-13T02:50:20Z");
        t3 = Instant.parse("2017-05-13T02:50:30Z");
        t4 = Instant.parse("2017-05-13T02:51:10Z");
        t5 = Instant.parse("2017-05-13T02:51:20Z");
        list = new ArrayList<>();
        list.add(new TStampedStub(t1));
        list.add(new TStampedStub(t2));
        list.add(new TStampedStub(t3));
        list.add(new TStampedStub(t4));
        list.add(new TStampedStub(t5));
    }

    @Test
    public void testAdd() throws Exception {
        series.add(list.get(0));
        series.add(list.get(1));
        series.add(list.get(2));
        series.add(list.get(3));
        series.add(list.get(4));

        assertEquals(2, series.getLength());
        assertEquals(3, series.get(0).size());
        assertEquals(2, series.get(1).size());
        assertEquals(t1, series.get(0).get(0).getTime());
        assertEquals(t2, series.get(0).get(1).getTime());
        assertEquals(t3, series.get(0).get(2).getTime());
        assertEquals(t4, series.get(1).get(0).getTime());
        assertEquals(t5, series.get(1).get(1).getTime());
    }

    @Test
    public void testAdd_fireEvent() throws Exception {
        final List<TStampedStub> result = new ArrayList<>();
        series.onAdd().addSyncListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                result.add(((StampedDataEvent<TStampedStub>)event).getData());
            }
        });
        series.add(list.get(0));
        series.add(list.get(1));
        series.add(list.get(2));

        assertEquals(3, result.size());
        assertEquals(list.get(0), result.get(0));
        assertEquals(list.get(1), result.get(1));
        assertEquals(list.get(2), result.get(2));
    }

    @Test(expected = ValueException.class)
    public void testAdd_Illegal_time() throws Exception {
        series.add(list.get(4));
        series.add(list.get(0));
    }

    @Test
    public void testCreateFromArray() throws Exception {
        StampedListSeries<TStampedStub> series = new StampedListSeries<>("TRADE_INFO", TimeFrame.M1, new EventQueueImpl(), list);

        assertEquals(2, series.getLength());
        assertEquals(3, series.get(0).size());
        assertEquals(2, series.get(1).size());
        assertEquals(t1, series.get(0).get(0).getTime());
        assertEquals(t2, series.get(0).get(1).getTime());
        assertEquals(t3, series.get(0).get(2).getTime());
        assertEquals(t4, series.get(1).get(0).getTime());
        assertEquals(t5, series.get(1).get(1).getTime());
    }
}