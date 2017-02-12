package ru.prolib.aquila.utils.experimental.charts.indicators.calculator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Candle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.prolib.aquila.utils.experimental.charts.Utils.toLocalDateTime;

/**
 * Created by TiM on 01.02.2017.
 */
public class MovingAverageCalculator implements Calculator {

    private final int period;

    public MovingAverageCalculator(int period) {
        this.period = period;
    }

    @Override
    public String getId() {
        return "MA"+period;
    }

    @Override
    public String getName() {
        return "Moving average "+period;
    }

    @Override
    public List<Pair<LocalDateTime, Double>> calculate(List<Candle> data) {
        List<Pair<LocalDateTime, Double>> result = new ArrayList<>();
        for(int i=period-1; i<data.size(); i++){
            double sum = 0;
            for(int j=0; j<period; j++){
                sum+= data.get(i-j).getClose();
            }
            result.add(new ImmutablePair<LocalDateTime, Double>(toLocalDateTime(data.get(i).getStartTime()), sum/period));
        }
        return result;
    }
}
