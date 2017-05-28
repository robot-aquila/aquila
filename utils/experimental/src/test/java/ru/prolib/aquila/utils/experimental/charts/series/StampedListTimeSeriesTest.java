package ru.prolib.aquila.utils.experimental.charts.series;

import org.junit.Before;
import org.junit.Test;
import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertEquals;

/**
 * Created by TiM on 27.05.2017.
 */
public class StampedListTimeSeriesTest {
    private StampedListSeries<TStampedStub> series;
    private StampedListTimeSeries timeSeries;

    private static class TStampedStub implements TStamped {
        private final Instant time;

        public TStampedStub(Instant time) {
            this.time = time;
        }

        @Override
        public Instant getTime() {
            return time;
        }
    }


    @Before
    public void setUp() throws Exception {
        List<TStampedStub> list = new ArrayList<>();
        list.add(new TStampedStub(Instant.parse("2017-05-13T02:50:10Z")));
        list.add(new TStampedStub(Instant.parse("2017-05-13T02:50:20Z")));
        list.add(new TStampedStub(Instant.parse("2017-05-13T02:51:10Z")));
        list.add(new TStampedStub(Instant.parse("2017-05-13T02:51:20Z")));
        series = new StampedListSeries<>("STAMPED", TimeFrame.M1, new EventQueueImpl(), list);
        timeSeries = new StampedListTimeSeries(series);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("STAMPED.TIME", timeSeries.getId());
    }

    @Test
    public void testGet() throws Exception {
        assertEquals(Instant.parse("2017-05-13T02:51:00Z"), timeSeries.get());
    }

    @Test
    public void testGet1() throws Exception {
        assertEquals(Instant.parse("2017-05-13T02:50:00Z"), timeSeries.get(0));
        assertEquals(Instant.parse("2017-05-13T02:51:00Z"), timeSeries.get(1));
    }

    @Test(expected = ValueException.class)
    public void testGet2() throws Exception {
        series.add(new Vector<TStampedStub>());
        timeSeries.get();
    }

    @Test
    public void getLength() throws Exception {
        assertEquals(2, timeSeries.getLength());
    }
}