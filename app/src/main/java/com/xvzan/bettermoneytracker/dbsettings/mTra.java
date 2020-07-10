package com.xvzan.bettermoneytracker.dbsettings;

import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class mTra extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();

    private boolean editMe;
    private mAccount accU;
    private mAccount accB;
    private long uAm;
    private long bAm;
    private Date mDate;
    private String mNote;
    private boolean isInTask;
    private mPlanTask planTask;

    //新建或编辑交易时使用
    public void ubSet(mAccount u, mAccount b, long um, long bm, Date d) {
        accU = u;
        accB = b;
        uAm = um;
        bAm = bm;
        setAllAmount();
        mDate = d;
    }

    //导入数据时使用
    public void directSet(mAccount u, mAccount b, long um, long bm, Date d) {
        accU = u;
        accB = b;
        uAm = um;
        bAm = bm;
        mDate = d;
    }

    public void setAllAmount() {
        long deltaU = Math.abs(uAm);
        long deltaB = Math.abs(bAm);
        if (accB.getAcct() == 4) {
            uAm = deltaU;
            if (accU.getBl1() && !accU.getBl2()) {
                bAm = -deltaB;
            } else {
                bAm = deltaB;
            }
            return;
        }
        if (accU.getAcct() == 4) {
            if (accB.getBl1() && accB.getBl2())
                bAm = deltaB;
            else
                bAm = -deltaB;
            if (accB.getBl1())
                uAm = deltaU;
            else
                uAm = -deltaU;
            return;
        }
        if (accU.getBl1() || accU.getBl2() || (accB.getBl1() && accB.getBl2())) {
            uAm = deltaU;
        } else {
            uAm = -deltaU;
        }
        long bs = (accU.getBl2() ^ accB.getBl2()) ? uAm / deltaU : -uAm / deltaU;
        bAm = bs * deltaB;
    }

    public void setmNote(String note) {
        mNote = note;
    }

    public mAccount getAccU() {
        return accU;
    }

    public mAccount getAccB() {
        return accB;
    }

    public Date getmDate() {
        return mDate;
    }

    public long getuAm() {
        return uAm;
    }

    public long getbAm() {
        return bAm;
    }

    public long getDeltaU() {
        return Math.abs(uAm);
    }

    public long getDeltaB() {
        return Math.abs(bAm);
    }

    public String getmNote() {
        return mNote.toString();
    }

    public void setEditme() {
        editMe = true;
    }

    public void meEdited() {
        editMe = false;
    }

    public mCurrency getUCurrency() {
        if (accU.getCurrency() != null)
            return accU.getCurrency();
        else
            return accB.getCurrency();
    }

    public mCurrency getBCurrency() {
        if (accB.getCurrency() != null)
            return accB.getCurrency();
        else
            return accU.getCurrency();
    }

    public void setPlanTask(mPlanTask planTask) {
        this.planTask = planTask;
        this.isInTask = planTask != null;
    }

    public mPlanTask getPlanTask() {
        return planTask;
    }

    public void removePlanTask() {
        this.planTask = null;
        this.isInTask = false;
    }

    public boolean hasTask() {
        return isInTask;
    }
}