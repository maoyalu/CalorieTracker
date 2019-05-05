package com.example.trackerapp;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainFragment extends Fragment {
    View vMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vMain = inflater.inflate(R.layout.fragment_main, container, false);

        final TextView goal = (TextView) vMain.findViewById(R.id.calorieGoalNumber);
        final EditText editGoal = (EditText) vMain.findViewById(R.id.editCalorieGoalNumber);
        final Button editGoalButton = (Button) vMain.findViewById(R.id.calorieGoalEditButton);
        final Button setGoalButton = (Button) vMain.findViewById(R.id.calorieGoalSetButton);

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
                }
                editGoal.setVisibility(View.INVISIBLE);
                setGoalButton.setVisibility(View.INVISIBLE);
                goal.setVisibility(View.VISIBLE);
                editGoalButton.setVisibility(View.VISIBLE);
            }
        });

        return vMain;
    }
}
