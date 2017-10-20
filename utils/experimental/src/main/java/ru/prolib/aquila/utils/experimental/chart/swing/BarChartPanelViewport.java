package ru.prolib.aquila.utils.experimental.chart.swing;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.INITIAL_NUMBER_OF_VISIBLE_CATEGORIES;

/**
 * Created by TiM on 21.10.2017.
 */
public class BarChartPanelViewport {
    private final AtomicInteger firstVisibleCategoryIndex = new AtomicInteger(0);
    private final AtomicInteger numberOfVisibleCategories = new AtomicInteger(INITIAL_NUMBER_OF_VISIBLE_CATEGORIES);
    private final AtomicBoolean autoScroll = new AtomicBoolean(true);
    private final AtomicBoolean changed = new AtomicBoolean(false);

    public int getFirstVisibleCategory() {
        return firstVisibleCategoryIndex.get();
    }

    public void setFirstVisibleCategory(int idx){
        firstVisibleCategoryIndex.set(idx);
        changed.set(true);
    }

    public int getNumberOfVisibleCategories() {
        return numberOfVisibleCategories.get();
    }

    public void setNumberOfVisibleCategories(int number){
        numberOfVisibleCategories.set(number);
        changed.set(true);
    }

    public boolean getAutoScroll() {
        return autoScroll.get();
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll.set(autoScroll);
        changed.set(true);
    }

    public boolean isChanged() {
        return changed.get();
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }
}
