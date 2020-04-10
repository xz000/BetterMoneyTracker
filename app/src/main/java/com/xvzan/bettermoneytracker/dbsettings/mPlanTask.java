package com.xvzan.bettermoneytracker.dbsettings;

import java.util.Date;

import io.realm.RealmObject;

public class mPlanTask extends RealmObject {
    private boolean isActive;
    private mAccount accU;
    private mAccount accB;
    private long uDelta;
    private long bDelta;
    private Date nextTime;
    private mTra latestTra;
    private Date endTime;
    private int loopType;//1,2,3,4,Day,Week,Month,Year
    private int loopInterval;//循环间隔
    private int feature;//循环特征，用于确定下次交易日期
    private int order;//用于排序以及查找同一任务下所有交易

    public mTra getLatestTra() {
        return latestTra;
    }

    public void setLatestTra(mTra tra) {
        latestTra = tra;
    }

    public int getLoopType() {
        return loopType;
    }

    public void setLoopType(int type) {
        loopType = type;
    }

    public int getLoopInterval() {
        return loopInterval;
    }

    public void setLoopInterval(int interval) {
        loopInterval = interval;
    }

    public int getFeature() {
        return feature;
    }

    public void setFeature(int feature1) {
        feature = feature1;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date time) {
        endTime = time;
    }

    public boolean hasEndTime() {
        return endTime != null;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date time) {
        nextTime = time;
    }
}
