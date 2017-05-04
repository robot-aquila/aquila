package ru.prolib.aquila.utils.experimental.charts.indicators.calculator;

import ru.prolib.aquila.core.data.Series;

/**
 * Created by TiM on 01.02.2017.
 */
public interface Calculator<TData, TResult> {
    String getId();

    String getName();

    Series<TResult> calculate(Series<TData> data);
}
