package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.DefaultLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorage;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorageImpl;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by TiM on 13.06.2017.
 */
public abstract class AbstractChartLayer<TCategories, TValues> implements ChartLayer<TCategories, TValues> {
    protected String id;
    protected final ChartLayerDataStorage<TCategories, TValues> storage;
    protected Map<TCategories, String> currentTooltips = new HashMap<>();
    protected LabelFormatter<TValues> labelFormatter = new DefaultLabelFormatter<>();

    public AbstractChartLayer(String id) {
        this.id = id;
        storage = new ChartLayerDataStorageImpl<>();
    }

    public AbstractChartLayer(String id, ChartLayerDataStorage<TCategories, TValues> storage) {
        this.id = id;
        this.storage = storage;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setCategories(Series<TCategories> categories) {
        storage.setCategories(categories);
    }

    @Override
    public Series<TCategories> getCategories() {
        return storage.getCategories();
    }

    @Override
    public void setData(Series<TValues> data) {
        storage.setData(data);
        if(this.id == null || "".equals(this.id)){
            this.id = data.getId();
        }
    }

    @Override
    public Series<TValues> getData() {
        return storage.getData();
    }

    @Override
    public void clearData() {
        storage.clearData();
    }

    @Override
    public void paint(CoordConverter<TCategories> converter){
        Graphics2D g = (Graphics2D) converter.getGraphics().create();
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
                paintObject(category, value, converter, g);
            }
        }
    }

    @Override
    public Pair<Double, Double> getValuesInterval(List<TCategories> displayCategories) {
        Double minY = null;
        Double maxY = null;
        if(storage.getData()==null){
            return null;
        }
        Set<Lockable> locks = new HashSet<>();
        locks.add(storage.getCategories());
        locks.add(storage.getData());
        Multilock lock = new Multilock(locks);
        lock.lock();
        try {
            for(int i=0; i<storage.getCategories().getLength(); i++){
                TCategories category = null;
                TValues value;
                try {
                    category = storage.getCategories().get(i);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if(displayCategories.contains(category)){
                    try {
                        value = storage.getData().get(i);
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
        } finally {
            lock.unlock();
        }
        return new ImmutablePair<>(minY, maxY);
    }

    @Override
    public String getTooltip(TCategories category) {
        return currentTooltips.get(category);
    }

    @Override
    public LabelFormatter<TValues> getLabelFormatter() {
        return labelFormatter;
    }

    @Override
    public void setLabelFormatter(LabelFormatter<TValues> labelFormatter) {
        this.labelFormatter = labelFormatter;
    }

    protected TValues getByCategory(TCategories category) throws ValueException {
        return storage.getByCategory(category);
    }

    protected String createTooltipText(TValues value) {
        return String.format("%s: %s", getId(), labelFormatter.format(value));
    }


    protected abstract void paintObject(TCategories category, TValues value, CoordConverter<TCategories> converter, Graphics2D g);

    protected abstract double getMaxValue(TValues value);
    protected abstract double getMinValue(TValues value);

}
