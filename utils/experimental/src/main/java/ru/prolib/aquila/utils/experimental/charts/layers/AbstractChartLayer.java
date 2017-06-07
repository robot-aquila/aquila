package ru.prolib.aquila.utils.experimental.charts.layers;

import javafx.scene.Node;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.Chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TiM on 02.05.2017.
 */
public abstract class AbstractChartLayer<TCategories, TValues> implements ChartLayer<TCategories, TValues> {
    protected Chart<TCategories> chart;
    protected Series<TCategories> categories;
    protected Series<TValues> data;
    protected Map<TCategories, String> currentTooltips = new ConcurrentHashMap<>();

    @Override
    public void setChart(Chart<TCategories> chart) {
        this.chart = chart;
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
    public List<Node> paint(){
        currentTooltips.clear();
        List<Node> result = new ArrayList<>();
        int cnt = chart.getCategories().size();
        for (int i = 0; i < cnt; i++) {
            TCategories category = chart.getCategories().get(i);
            TValues value = null;
            try {
                value = getByCategory(category);
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(category!=null && value != null && chart.isCategoryDisplayed(category)){
                currentTooltips.put(category, createTooltipText(value));
                Node node = chart.getNodeById(getIdByCategory(category));
                Node resultNode = paintNode(category, value, node);
                if(node==null && resultNode!=null){
                    resultNode.setId(getIdByCategory(category));
                    result.add(resultNode);
                }
            }
        }
        return result;
    }

    @Override
    public String getTooltip(TCategories category){
        return currentTooltips.get(category);
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

    protected TValues getByCategory(TCategories category) throws ValueException {
        for(int i=0; i<categories.getLength(); i++){
            if(category.equals(categories.get(i))){
                return data.get(i);
            }
        }
        return null;
    }

    protected String getIdByCategory(TCategories category){
        return getIdPrefix()+"@"+category.toString();
    }

    protected String getIdPrefix() {
        return "";
    };

    protected abstract double getMaxValue(TValues value);
    protected abstract double getMinValue(TValues value);

    protected abstract String createTooltipText(TValues value);


    public Node paintNode(TCategories category, TValues value, Node node){
        return null;
    }
}
