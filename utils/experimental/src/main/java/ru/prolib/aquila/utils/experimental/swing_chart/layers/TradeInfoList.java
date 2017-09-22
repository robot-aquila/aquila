package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by TiM on 19.09.2017.
 */
public class TradeInfoList implements Lockable {
    private final List<TradeInfo> data = new ArrayList<>();
    private final LID lid;
    private final Lock lock = new ReentrantLock();
    private Double minValue = null;
    private Double maxValue = null;

    public TradeInfoList() {
        lid = LID.createInstance();
    }

    public TradeInfoList(TradeInfoList tradeInfoList, List<Account> accounts){
        this();
        if(tradeInfoList!=null){
            List<TradeInfo> list = new ArrayList<>(tradeInfoList.data);
            for(TradeInfo ti: list){
                if(accounts==null || accounts.contains(ti.getAccount())){
                    add(ti);
                }
            }
        }
    }

    public int size() {
        return data.size();
    }

    public boolean add(TradeInfo tradeInfo) {
        if(minValue==null || tradeInfo.getPrice()<minValue){
            minValue = tradeInfo.getPrice();
        }
        if(maxValue==null || tradeInfo.getPrice()>maxValue){
            maxValue = tradeInfo.getPrice();
        }
        return data.add(tradeInfo);
    }

    public TradeInfo get(int index) {
        return data.get(index);
    }

    @Override
    public LID getLID() {
        return lid;
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    public Double getMaxValue(){
        return maxValue;
    }

    public Double getMinValue() {
        return minValue;
    }
}
