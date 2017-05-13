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
 * Created by TiM on 14.05.2017.
 */
public class TradeInfoTimeSeriesTest {

    private TradeInfoSeries series;
    private TradeInfoTimeSeries timeSeries;

    @Before
    public void setUp() throws Exception {
        List<TradeInfo> list = new ArrayList<>();
        list.add(new TradeInfo(Instant.parse("2017-05-13T02:50:10Z"), OrderAction.BUY, 100d, 100L));
        list.add(new TradeInfo(Instant.parse("2017-05-13T02:50:20Z"), OrderAction.BUY, 100d, 100L));
        list.add(new TradeInfo(Instant.parse("2017-05-13T02:51:10Z"), OrderAction.BUY, 100d, 100L));
        list.add(new TradeInfo(Instant.parse("2017-05-13T02:51:20Z"), OrderAction.BUY, 100d, 100L));
        series = new TradeInfoSeries("TRADE_INFO", TimeFrame.M1, list);
        timeSeries = new TradeInfoTimeSeries(series);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("TRADE_INFO.TIME", timeSeries.getId());
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
        series.add(new ArrayList<TradeInfo>());
        timeSeries.get();
    }

    @Test
    public void getLength() throws Exception {
        assertEquals(2, timeSeries.getLength());
    }

}