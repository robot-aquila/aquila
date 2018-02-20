package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
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
    private CDecimal minValue = null;
    private CDecimal maxValue = null;

    public TradeInfoList() {
        lid = LID.createInstance();
    }

    public TradeInfoList(TradeInfoList tradeInfoList, List<Account> accounts){
        this();
        if(tradeInfoList!=null){
            tradeInfoList.lock();
            try {
                List<TradeInfo> list = new ArrayList<>(tradeInfoList.data);
                for(TradeInfo ti: list){
                    if(accounts==null || accounts.contains(ti.getAccount())){
                        add(ti);
                    }
                }
            } finally {
                tradeInfoList.unlock();
            }
        }
    }

    public int size() {
        return data.size();
    }

    public boolean add(TradeInfo tradeInfo) {
    	recalcValuesInterval(tradeInfo.getPrice());
        return data.add(tradeInfo);
    }

    public TradeInfo get(int index) {
        return data.get(index);
    }

    public void removeByOrderId(Long orderId){
        for(int i=data.size()-1; i>=0; i--){
            TradeInfo ti = data.get(i);
            if(orderId!=null && orderId.equals(ti.getOrderId())){
                data.remove(ti);
            }
        }
        recalcValuesInterval();
    }

    public boolean containsOrder(Long orderId){
        for(TradeInfo ti: data){
            if(ti.getOrderId()!=null && ti.getOrderId().equals(orderId)){
                return true;
            }
        }
        return false;
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

    public CDecimal getMaxValue(){
        return maxValue;
    }

    public CDecimal getMinValue() {
        return minValue;
    }

    private void recalcValuesInterval(){
        if(data.size()==0){
            minValue = null;
            maxValue = null;
            return;
        }
        for(TradeInfo tradeInfo: data){
        	recalcValuesInterval(tradeInfo.getPrice());
        }
    }
    
    private void recalcValuesInterval(CDecimal value) {
        if( minValue == null || value.compareTo(minValue) < 0 ){
            minValue = value;
        }
        if( maxValue == null || value.compareTo(maxValue) > 0 ){
            maxValue = value;
        }
    }
    
}
