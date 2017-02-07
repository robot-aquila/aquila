package ru.prolib.aquila.utils.experimental.charts.calculator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Candle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.prolib.aquila.utils.experimental.charts.Utils.toLocalDateTime;

/**
 * Created by TiM on 01.02.2017.
 */
public class TestCalculator implements Calculator {

    @Override
    public List<Pair<LocalDateTime, Double>> calculate(List<Candle> data) {
        return data.stream().map(c->
                new ImmutablePair<>(toLocalDateTime(c.getStartTime()), c.getHigh())).collect(Collectors.toList());
    }

    @Override
    public String getId() {
        return "HIGH";
    }

    @Override
    public String getName() {
        return "Test indicator";
    }
}
