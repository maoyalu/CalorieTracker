package com.example.trackerapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainFragment extends Fragment {
    View vMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vMain = inflater.inflate(R.layout.fragment_main, container, false);

        final TextView goal = (TextView) vMain.findViewById(R.id.calorieGoalNumber);
        SharedPreferences pref = getContext().getSharedPreferences("calorie_goal", 0);
        String goalStr = Integer.toString(pref.getInt("goal", 0));
        goal.setText(goalStr);

        final EditText editGoal = (EditText) vMain.findViewById(R.id.editCalorieGoalNumber);
        final Button editGoalButton = (Button) vMain.findViewById(R.id.calorieGoalEditButton);
        final Button setGoalButton = (Button) vMain.findViewById(R.id.calorieGoalSetButton);

        final SessionManagement session = new SessionManagement(getContext());

        TextView welcome = (TextView) vMain.findViewById(R.id.welcomeMessage);
        String newText = "Welcome, " + session.getCurrentUserFirstName() + "!";

        welcome.setText(newText);

        editGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goal.setVisibility(View.INVISIBLE);
                editGoalButton.setVisibility(View.INVISIBLE);
                editGoal.setVisibility(View.VISIBLE);
                setGoalButton.setVisibility(View.VISIBLE);
            }
        });

        editGoal.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    InputMethodManager inputMethodManager = (InputMethodManager) vMain.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(vMain.getWindowToken(), 0);
                }
            }
        });

        setGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editGoal.getText().toString().equals("")){
                    goal.setText(editGoal.getText());
                    SharedPreferences pref = getContext().getSharedPreferences("calorie_goal", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("goal", Integer.parseInt(editGoal.getText().toString()));
                    editor.apply();
                }
                editGoal.setVisibility(View.INVISIBLE);
                setGoalButton.setVisibility(View.INVISIBLE);
                goal.setVisibility(View.VISIBLE);
                editGoalButton.setVisibility(View.VISIBLE);
            }
        });

        Button postBtn = (Button) vMain.findViewById(R.id.button_service);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uid = session.getCurrentUserId();
                ServicePost servicePost = new ServicePost();
                servicePost.execute(uid);
            }
        });

        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getContext(), ScheduledIntentService.class);
        PendingIntent pdIntent = PendingIntent.getService(getContext(), 0, alarmIntent, 0);
        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.setTimeInMillis(System.currentTimeMillis());
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 23);
        alarmStartTime.set(Calendar.MINUTE, 59);
        alarmStartTime.set(Calendar.SECOND, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pdIntent);

        return vMain;
    }

    private class ServicePost extends AsyncTask<Integer, Void, Void>{

        @Override
        protected Void doInBackground(Integer... params) {
            Integer id = RestClient.getNextReportId();
            double consumed = Double.parseDouble(RestClient.getTotalCaloriesConsumed(params[0]));
            double rest = RestClient.getCaloriesBurnedAtRest(params[0]);
            double cPerStep = RestClient.getTotalCaloriesPerStep(params[0]);
            StepDatabase db = Room.databaseBuilder(getContext(), StepDatabase.class, "StepDatabase")
                    .fallbackToDestructiveMigration().build();
            int steps = db.stepDao().getDailiyTotalStepsByUid(params[0]);
            double burned = rest + cPerStep * steps;
            SharedPreferences pref = getContext().getSharedPreferences("calorie_goal", 0);
            double goal = pref.getInt("goal", 0);
            Users usr = RestClient.findUserById(params[0]);
            Report report = new Report(id, consumed, burned, steps, goal, usr);
            RestClient.createReport(report);
            db.stepDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            Toast.makeText(getActivity(), getString(R.string.add_report_success), Toast.LENGTH_LONG).show();
        }
    }
}
