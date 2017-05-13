package ru.prolib.aquila.utils.experimental.charts.series;

import org.junit.Before;
import org.junit.Test;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.layers.TradeInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by TiM on 13.05.2017.
 */
public class TradeInfoSeriesTest {
    private TradeInfoSeries series;
    private Instant t1, t2, t3, t4, t5;
    private List<TradeInfo> list;

    @Before
    public void setUp() throws Exception {
        series = new TradeInfoSeries("TRADE_INFO", TimeFrame.M1);
        t1 = Instant.parse("2017-05-13T02:50:10Z");
        t2 = Instant.parse("2017-05-13T02:50:20Z");
        t3 = Instant.parse("2017-05-13T02:50:30Z");
        t4 = Instant.parse("2017-05-13T02:51:10Z");
        t5 = Instant.parse("2017-05-13T02:51:20Z");
        list = new ArrayList<>();
        list.add(new TradeInfo(t1, OrderAction.BUY, 100d, 100L));
        list.add(new TradeInfo(t2, OrderAction.BUY, 100d, 100L));
        list.add(new TradeInfo(t3, OrderAction.BUY, 100d, 100L));
        list.add(new TradeInfo(t4, OrderAction.BUY, 100d, 100L));
        list.add(new TradeInfo(t5, OrderAction.BUY, 100d, 100L));
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

    @Test(expected = ValueException.class)
    public void testAdd_Illegal_time() throws Exception {
        series.add(list.get(4));
        series.add(list.get(0));
    }

    @Test
    public void testCreateFromArray() throws Exception {
        TradeInfoSeries series = new TradeInfoSeries("TRADE_INFO", TimeFrame.M1, list);

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