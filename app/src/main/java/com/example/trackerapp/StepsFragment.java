package com.example.trackerapp;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StepsFragment extends Fragment {
    View vSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vSteps = inflater.inflate(R.layout.fragment_steps, container, false);
        return vSteps;
    }
}