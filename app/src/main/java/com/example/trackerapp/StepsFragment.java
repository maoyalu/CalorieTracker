package com.example.trackerapp;

import android.app.Dialog;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Update;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StepsFragment extends Fragment {
    View vSteps;
    StepDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vSteps = inflater.inflate(R.layout.fragment_steps, container, false);

        db = Room.databaseBuilder(getContext(), StepDatabase.class, "StepDatabase").fallbackToDestructiveMigration().build();

        final EditText stepNum = (EditText) vSteps.findViewById(R.id.steps_number);
        Button addButton = (Button) vSteps.findViewById(R.id.steps_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stepNumText = stepNum.getText().toString();
                if(!stepNumText.equals("")){
                    InsertDatabase addDatabase = new InsertDatabase();
                    addDatabase.execute(stepNumText);
                    stepNum.setText("");
                } else {
                    Toast.makeText(getActivity(), getString(R.string.add_step_error_empty_num), Toast.LENGTH_LONG).show();
                }
            }
        });

        SessionManagement sessionManagement = new SessionManagement(getContext());
        int uid = sessionManagement.getCurrentUserId();
        LoadData loadTask = new LoadData();
        loadTask.execute(uid);

        return vSteps;
    }

    private class LoadData extends AsyncTask<Integer, Void, List<Step>>{

        @Override
        protected List<Step> doInBackground(Integer... params) {
            return db.stepDao().findByUid(params[0]);
        }

        @Override
        protected void onPostExecute(List<Step> steps){
            final ListView stepList = (ListView) vSteps.findViewById(R.id.steps_list);
            final ArrayList<Step> data = new ArrayList<Step>(steps);
            final StepAdapter adapter = new StepAdapter(getContext(), data);
            stepList.setAdapter(adapter);

            stepList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final Step oldItem = (Step)stepList.getItemAtPosition(position);

                    final Dialog dialog = new Dialog(getContext());
                    dialog.setTitle("Edit Step");
                    dialog.setContentView(R.layout.step_update);

                    final EditText editText = (EditText) dialog.findViewById(R.id.new_step);
                    editText.setText(Integer.toString(oldItem.getStep()));

                    Button btn = (Button) dialog.findViewById(R.id.step_edit_button);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String stepNum = editText.getText().toString();
                            if(!stepNum.isEmpty()){
                                oldItem.setStep(Integer.parseInt(stepNum));

                                UpdateDatabase updateDatabase = new UpdateDatabase();
                                updateDatabase.execute(oldItem);


                                data.set(position, oldItem);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }
            });

        }


    }

    private class UpdateDatabase extends AsyncTask<Step, Void, Void>{

        @Override
        protected Void doInBackground(Step... params) {
            db.stepDao().updateSteps(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            Toast.makeText(getActivity(), getString(R.string.update_step_success), Toast.LENGTH_LONG).show();
        }
    }

    private class InsertDatabase extends AsyncTask<String, Void, Long>{

        @Override
        protected Long doInBackground(String... params) {
            SessionManagement sessionManagement = new SessionManagement(getContext());
            int uid = sessionManagement.getCurrentUserId();
            int stepNum = Integer.parseInt(params[0]);
            Step step = new Step(uid, stepNum);
            long id = db.stepDao().insert(step);
            return id;
        }

        @Override
        protected void onPostExecute(Long id){
            Toast.makeText(getActivity(), getString(R.string.add_step_success), Toast.LENGTH_LONG).show();
            SessionManagement sessionManagement = new SessionManagement(getContext());
            int uid = sessionManagement.getCurrentUserId();
            LoadData loadTask = new LoadData();
            loadTask.execute(uid);
        }
    }

    private class StepAdapter extends ArrayAdapter<Step>{
        public StepAdapter(Context context, ArrayList<Step> steps){
            super(context, 0, steps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Step step = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.step_listview, parent, false);
            }

            TextView sptime = (TextView) convertView.findViewById(R.id.sp_time);
            TextView spnum = (TextView) convertView.findViewById(R.id.sp_number);
            String timeStr = new SimpleDateFormat("HH:mm").format(step.getTime());
            String numStr = Integer.toString(step.getStep());

            sptime.setText(timeStr);
            spnum.setText(numStr);

            return convertView;
        }
    }
}
