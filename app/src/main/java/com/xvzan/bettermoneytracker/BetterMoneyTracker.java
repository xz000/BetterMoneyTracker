package com.xvzan.bettermoneytracker;

import android.app.Application;

import com.xvzan.bettermoneytracker.dbsettings.mAccount;
import com.xvzan.bettermoneytracker.dbsettings.mPlanTask;
import com.xvzan.bettermoneytracker.dbsettings.mTra;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.SyncConfiguration;

public class BetterMoneyTracker extends Application {
    public App CloudSyncApp;
    public RealmConfiguration config;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        config = new RealmConfiguration.Builder().name(BuildConfig.MONGODB_REALM_APP_ID).build();
        CloudSyncApp = new App(new AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID).build());
        String partitionValue = "myPartition";
        //SyncConfiguration syncConfiguration = new SyncConfiguration.Builder(Objects.requireNonNull(CloudSyncApp.currentUser()), partitionValue).build();
        //loopPlannedTasks();
    }

    public void loopPlannedTasks() {
        try (Realm realm = Realm.getDefaultInstance()) {
            Date timeNow = Calendar.getInstance().getTime();
            OrderedRealmCollection<mPlanTask> planTasks = realm.where(mPlanTask.class)
                    .equalTo("isActive", true).lessThan("nextTime", timeNow).findAll();
            if (planTasks.size() == 0) return;
            for (mPlanTask planTask : planTasks) {
                int t = planTask.getLoopType();
                int i = planTask.getLoopInterval();
                int f = 0;
                if (t == 3) f = planTask.getFeature();
                mAccount ua = planTask.getAccU();
                mAccount ba = planTask.getAccB();
                long um = planTask.getuAm();
                long bm = planTask.getbAm();
                String note = planTask.getmNote();
                Date date = planTask.getNextTime();
                while (date.before(timeNow)) {
                    if (planTask.hasEndTime() && date.after(planTask.getEndTime())) {
                        realm.beginTransaction();
                        planTask.setDisable();
                        realm.commitTransaction();
                        break;
                    }
                    mTra tra = new mTra();
                    tra.ubSet(ua, ba, um, bm, date);
                    if (note != null) {
                        tra.setmNote(note);
                    }
                    tra.setPlanTask(planTask);
                    realm.beginTransaction();
                    realm.copyToRealm(tra);
                    realm.commitTransaction();
                    date = calculateNextTime(t, i, f, date);
                }
                realm.beginTransaction();
                planTask.setNextTime(date);
                realm.commitTransaction();
            }
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
                    if (feature < 0) {
                        feature += calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;
                        if (feature < 1)
                            feature = 1;
                    } else
                        feature = Math.min(calendar.getActualMaximum(Calendar.DAY_OF_MONTH), feature);
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
