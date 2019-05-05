package com.example.trackerapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SignupFragment extends Fragment {
    View vSignup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vSignup = inflater.inflate(R.layout.fragment_signup, container, false);


        EditText dob = (EditText) vSignup.findViewById(R.id.signupDob);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });


        RadioGroup genders = (RadioGroup) vSignup.findViewById(R.id.signupGenderGroup);
        genders.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.genderFemale:
                        break;
                    case R.id.genderMale:
                        break;
                }
            }
        });


        List<Integer> list = new ArrayList<Integer>();
        for(Integer i = 1; i < 6; i++){
            list.add(i);
        }
        Spinner activityLevel = (Spinner) vSignup.findViewById(R.id.activityLevelSpinner);
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevel.setAdapter(spinnerAdapter);


        Button signupButton = (Button) vSignup.findViewById(R.id.signupConfirmButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.login_frame, new LoginFragment()).commit();
            }
        });

        return vSignup;
    }


}