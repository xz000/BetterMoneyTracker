package com.xvzan.bettermoneytracker;

import android.app.Application;

import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mPlanTask;
import com.xvzan.bettermoneytracker.dbsettings.mTra;

import java.util.Calendar;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BetterMoneyTracker extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("smt.realm").build();
        loopPlannedTasks();
    }

    private void loopPlannedTasks() {
        try (Realm realm = Realm.getDefaultInstance()) {
            Date timeNow = Calendar.getInstance().getTime();
            OrderedRealmCollection<mPlanTask> planTasks = realm.where(mPlanTask.class)
                    .equalTo("isActive", true).lessThan("nextTime", timeNow).findAll();
            if (planTasks.size() == 0) return;
            realm.beginTransaction();
            for (mPlanTask planTask : planTasks) {
                int t = planTask.getLoopType();
                int i = planTask.getLoopInterval();
                int f = planTask.getFeature();
                mAccount ua = planTask.getLatestTra().getAccU();
                mAccount ba = planTask.getLatestTra().getAccB();
                long um = planTask.getLatestTra().getuAm();
                long bm = planTask.getLatestTra().getbAm();
                String note = planTask.getLatestTra().getmNote();
                Date date = planTask.getNextTime();
                while (date.before(timeNow)) {
                    mTra tra = new mTra();
                    tra.directSet(ua, ba, um, bm, date);
                    if (note != null) {
                        tra.setmNote(note);
                    }
                    tra.setPlanTask(planTask);
                    realm.copyToRealm(tra);
                    date = calculateNextTime(t, i, f, date);
                }
                planTask.setNextTime(date);
            }
            realm.commitTransaction();
        }
    }

    public Date calculateNextTime(int type, int interval, int feature, Date latestDate) {
        Calendar calendar = Calendar.getInstance();
        switch (type) {
            case 1:
                calendar.setTime(latestDate);
                calendar.add(Calendar.DATE, interval);
                return calendar.getTime();
            case 2:
                calendar.setTime(latestDate);
                calendar.add(Calendar.DATE, interval * 7);
                return calendar.getTime();
            case 3:
                calendar.setTime(latestDate);
                calendar.add(Calendar.MONTH, interval);
                if (feature != 0) {
                    if (feature < 0)
                        feature += calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;
                    calendar.set(Calendar.DAY_OF_MONTH, feature);
                }
                return calendar.getTime();
            case 4:
                calendar.setTime(latestDate);
                calendar.add(Calendar.YEAR, interval);
                return calendar.getTime();
            default:
                return new Date(Long.MAX_VALUE);
        }
    }
}
