package com.example.trackerapp;

import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class CalorieTrackerFragment extends Fragment {
    View vCalorieTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vCalorieTracker = inflater.inflate(R.layout.fragment_calorie_tracker, container, false);

        TextView goal = (TextView) vCalorieTracker.findViewById(R.id.calorieGoal);
        SharedPreferences pref = getContext().getSharedPreferences("calorie_goal", 0);
        String goalStr = Integer.toString(pref.getInt("goal", 0));
        goal.setText(goalStr);

        SessionManagement sessionManagement = new SessionManagement(getContext());
        int uid = sessionManagement.getCurrentUserId();
        GetStepsTask stepTask = new GetStepsTask();
        stepTask.execute(uid);

        GetTotalConsumedTask consumedTask = new GetTotalConsumedTask();
        consumedTask.execute(uid);

        GetTotalBurnedTask burnedTask = new GetTotalBurnedTask();
        burnedTask.execute(uid);

        return vCalorieTracker;
    }

    private class GetStepsTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            StepDatabase db = Room.databaseBuilder(getContext(), StepDatabase.class, "StepDatabase")
                    .fallbackToDestructiveMigration().build();
            int steps = db.stepDao().getDailiyTotalStepsByUid(params[0]);
            String stepStr = Integer.toString(steps);
            return stepStr;
        }

        @Override
        protected void onPostExecute(String result){
            TextView step = (TextView) vCalorieTracker.findViewById(R.id.totalSteps);
            step.setText(result);
        }
    }

    private class GetTotalConsumedTask extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... params) {
            return RestClient.getTotalCaloriesConsumed(params[0]);
        }

        @Override
        protected void onPostExecute(String result){
            TextView consumed = (TextView) vCalorieTracker.findViewById(R.id.totalConsumed);
            consumed.setText(result);
        }
    }

    private class GetTotalBurnedTask extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... params) {
            double rest = RestClient.getCaloriesBurnedAtRest(params[0]);
            double cPerStep = RestClient.getTotalCaloriesPerStep(params[0]);
            StepDatabase db = Room.databaseBuilder(getContext(), StepDatabase.class, "StepDatabase")
                    .fallbackToDestructiveMigration().build();
            int steps = db.stepDao().getDailiyTotalStepsByUid(params[0]);
            double total = rest + cPerStep * steps;
            return String.format("%.1f",total);
//            return Double.toString(total);
        }

        @Override
        protected void onPostExecute(String result){
            TextView burned = (TextView) vCalorieTracker.findViewById(R.id.totalBurned);
            burned.setText(result);
        }
    }
}
