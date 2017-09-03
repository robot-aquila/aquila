package ru.prolib.aquila.utils.experimental.swing_chart.layers.data;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Created by TiM on 02.09.2017.
 */
public class ChartLayerDataStorageImpl<TCategories, TValues> implements ChartLayerDataStorage<TCategories, TValues> {

    protected Series<TCategories> categories;
    protected Series<TValues> data;

    @Override
    public Series<TCategories> getCategories() {
        return categories;
    }

    @Override
    public void setCategories(Series<TCategories> categories) {
        this.categories = categories;
    }

    @Override
    public Series<TValues> getData() {
        return data;
    }

    @Override
    public void setData(Series<TValues> data) {
        this.data = data;
    }

    @Override
    public void clearData() {
        this.data = new SeriesImpl<>();
        this.categories = new SeriesImpl<>();
    }

    @Override
    public TValues getByCategory(TCategories category) throws ValueException {
        for(int i=0; i<categories.getLength(); i++){
            if(category.equals(categories.get(i))){
                return data.get(i);
            }
        }
        return null;
    }
}
