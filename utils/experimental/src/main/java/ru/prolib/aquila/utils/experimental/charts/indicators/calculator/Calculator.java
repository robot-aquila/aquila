package ru.prolib.aquila.utils.experimental.charts.indicators.calculator;

import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Candle;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by TiM on 01.02.2017.
 */
public interface Calculator {
    String getId();

    String getName();

    List<Pair<LocalDateTime, Double>> calculate(List<Candle> data);
}
