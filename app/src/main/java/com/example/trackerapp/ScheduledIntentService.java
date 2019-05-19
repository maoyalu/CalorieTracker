package com.example.trackerapp;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ScheduledIntentService extends IntentService {
    public ScheduledIntentService(){
        super("ScheduledIntentService");
    }

    @Override
    protected void onHandleIntent( Intent intent) {
        Log.i("TEST: ", "The Intent Serivce is running!!!!");
        SessionManagement session = new SessionManagement(this);
        int uid = session.getCurrentUserId();

        if(uid != -1){
            Integer id = RestClient.getNextReportId();
            double consumed = Double.parseDouble(RestClient.getTotalCaloriesConsumed(uid));
            double rest = RestClient.getCaloriesBurnedAtRest(uid);
            double cPerStep = RestClient.getTotalCaloriesPerStep(uid);
            StepDatabase db = Room.databaseBuilder(this, StepDatabase.class, "StepDatabase")
                    .fallbackToDestructiveMigration().build();
            int steps = db.stepDao().getDailiyTotalStepsByUid(uid);
            double burned = rest + cPerStep * steps;
            SharedPreferences pref = getSharedPreferences("calorie_goal", 0);
            double goal = pref.getInt("goal", 0);
            Users usr = RestClient.findUserById(uid);
            Report report = new Report(id, consumed, burned, steps, goal, usr);
            RestClient.createReport(report);
            db.stepDao().deleteAll();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }
}
