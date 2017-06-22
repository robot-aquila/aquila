package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TiM on 13.06.2017.
 */
public abstract class AbstractChartLayer<TCategories, TValues> implements ChartLayer<TCategories, TValues> {
    protected String id;
    protected Series<TCategories> categories;
    protected Series<TValues> data;
    protected Map<TCategories, String> currentTooltips = new HashMap<>();

    public AbstractChartLayer(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setCategories(Series<TCategories> categories) {
        this.categories = categories;
    }

    @Override
    public Series<TCategories> getCategories() {
        return categories;
    }

    @Override
    public void setData(Series<TValues> data) {
        this.data = data;
        if(this.id == null || "".equals(this.id)){
            this.id = data.getId();
        }
    }

    @Override
    public Series<TValues> getData() {
        return data;
    }

    @Override
    public void clearData() {
        this.data = new SeriesImpl<>();
        this.categories = new SeriesImpl<>();
    }

    @Override
    public void paint(CoordConverter<TCategories> converter){
        Color bakColor = converter.getGraphics().getColor();
        currentTooltips.clear();
        int cnt = converter.getCategories().size();
        for (int i = 0; i < cnt; i++) {
            TCategories category = converter.getCategories().get(i);
            TValues value = null;
            try {
                value = getByCategory(category);
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(category!=null && value != null && converter.isCategoryDisplayed(category)){
                currentTooltips.put(category, createTooltipText(value));
                paintObject(category, value, converter);
            }
        }
        converter.getGraphics().setColor(bakColor);
    }

    @Override
    public Pair<Double, Double> getValuesInterval(List<TCategories> displayCategories) {
        Double minY = null;
        Double maxY = null;
        if(data==null){
            return null;
        }
        for(int i=0; i<categories.getLength(); i++){
            TCategories category = null;
            TValues value = null;
            try {
                category = categories.get(i);
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(displayCategories.contains(category)){
                try {
                    value = data.get(i);
                } catch (ValueException e) {
                    value = null;
                }
                if(value!=null){
                    double y = getMaxValue(value);
                    if(maxY==null || y>maxY){
                        maxY = y;
                    }
                    y = getMinValue(value);
                    if(minY==null || y<minY){
                        minY = y;
                    }
                }
            }
        }
        return new ImmutablePair<>(minY, maxY);
    }

    @Override
    public String getTooltip(TCategories category) {
        return currentTooltips.get(category);
    }

    protected TValues getByCategory(TCategories category) throws ValueException {
        for(int i=0; i<categories.getLength(); i++){
            if(category.equals(categories.get(i))){
                return data.get(i);
            }
        }
        return null;
    }

    protected abstract void paintObject(TCategories category, TValues value, CoordConverter<TCategories> converter);

    protected abstract double getMaxValue(TValues value);
    protected abstract double getMinValue(TValues value);

    protected abstract String createTooltipText(TValues value);
}
