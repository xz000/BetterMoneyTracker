package com.xvzan.bettermoneytracker.dbsettings;

import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class mPlanTask extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();

    private boolean isActive;
    private mAccount accU;
    private mAccount accB;
    private long uDelta;
    private long bDelta;
    private Date nextTime;
    private Date firstTime;
    //private Date lastTime;
    //private mTra firstTra;
    //private mTra latestTra;
    private Date endTime;
    private String mNote;
    private int loopType;//1,2,3,4,Day,Week,Month,Year
    private int loopInterval;//循环间隔
    private int feature;//循环特征，用于确定下次交易日期
    private int order;//用于排序以及查找同一任务下所有交易

/*
    public mTra getLatestTra() {
        return latestTra;
    }

    public void setLatestTra(mTra tra) {
        latestTra = tra;
    }

    public mTra getFirstTra() {
        return firstTra;
    }

    public void setFirstTra(mTra tra) {
        firstTra = tra;
    }
 */

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

    public void setBasic(mAccount aU, mAccount aB, long deltaU, long deltaB, String note, Date firstT) {
        accU = aU;
        accB = aB;
        uDelta = deltaU;
        bDelta = deltaB;
        mNote = note;
        firstTime = firstT;
    }

    public mAccount getAccU() {
        return accU;
    }

    public mAccount getAccB() {
        return accB;
    }

    public long getuAm() {
        return uDelta;
    }

    public long getbAm() {
        return bDelta;
    }

    public void setActive() {
        isActive = true;
    }

    public boolean getActive() {
        return isActive;
    }

    public void setDisable() {
        isActive = false;
    }

    public String getmNote() {
        return mNote;
    }

    public void setOrder(int order1) {
        order = order1;
    }

    public int getOrder() {
        return order;
    }

    public Date getFirstTime() {
        return firstTime;
    }
}
