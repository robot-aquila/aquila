package ru.prolib.aquila.core.data.ta;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by TiM on 10.05.2017.
 */
public class QEMAIT {
    private SeriesImpl<CDecimal> closeSeries;
    private Series<CDecimal> qema;
    private final int days = 30;
    private final int period = 14;

    @Before
    public void setUp() throws Exception {
        closeSeries = new SeriesImpl<>("CANDLE.CLOSE");
        for(int i=0; i<days*24*60; i++){
            closeSeries.add(CDecimalBD.of((long)(Math.random() * 100)));
        }
        qema = new QEMA("QEMA", closeSeries, period);
    }

    @Test
    public void testPerfomance() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        for(int i = 0; i< qema.getLength(); i++){
            /*CDecimal val = */qema.get(i);
        }
        Long duration = start.until(LocalDateTime.now(), ChronoUnit.SECONDS);
        System.out.println(duration);
    }

}