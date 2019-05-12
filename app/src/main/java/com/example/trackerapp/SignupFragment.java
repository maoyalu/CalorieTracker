package com.example.trackerapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


        Spinner activityLevel = (Spinner) vSignup.findViewById(R.id.activityLevelSpinner);
        List<Integer> list = new ArrayList<Integer>();
        for(Integer i = 1; i < 6; i++){
            list.add(i);
        }

        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevel.setAdapter(spinnerAdapter);


        Button signupButton = (Button) vSignup.findViewById(R.id.signupConfirmButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstName = (EditText) vSignup.findViewById(R.id.signupFirstName);
                EditText lastName = (EditText) vSignup.findViewById(R.id.signupLastName);
                EditText email = (EditText) vSignup.findViewById(R.id.signupEmail);
                EditText username = (EditText) vSignup.findViewById(R.id.signupUsername);
                EditText pwd = (EditText) vSignup.findViewById(R.id.signupPassword);
                EditText pwdConfirm = (EditText) vSignup.findViewById(R.id.signupPasswordConfirm);
                EditText dob = (EditText) vSignup.findViewById(R.id.signupDob);
                EditText height = (EditText) vSignup.findViewById(R.id.signupHeight);
                EditText weight = (EditText) vSignup.findViewById(R.id.signupWeight);
                RadioGroup genders = (RadioGroup) vSignup.findViewById(R.id.signupGenderGroup);
                RadioButton selectedGender = null;
                Spinner activityLevel = (Spinner) vSignup.findViewById(R.id.activityLevelSpinner);
                EditText steps = (EditText) vSignup.findViewById(R.id.signupStepsPerMile);
                EditText address = (EditText) vSignup.findViewById(R.id.signupAddress);
                EditText postcode = (EditText) vSignup.findViewById(R.id.signupPostcode);

                String name = firstName.getText().toString();
                String surname = lastName.getText().toString();
                String eml = email.getText().toString();
                String usr = username.getText().toString();
                String pw1 = pwd.getText().toString();
                String pw2 = pwdConfirm.getText().toString();
                String birthday = dob.getText().toString();
                String ht = height.getText().toString();
                String wt = weight.getText().toString();
                String stp = steps.getText().toString();
                String addr = address.getText().toString();
                String code = postcode.getText().toString();
                String lvl = activityLevel.getSelectedItem().toString();

                if(name.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_firstname), Toast.LENGTH_LONG).show();
                } else if (surname.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_lastname), Toast.LENGTH_LONG).show();
                } else if (eml.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_email), Toast.LENGTH_LONG).show();
                } else if (!eml.matches("[a-zA-Z0-9._-]+@[a-z]+[\\.+[a-z]+]+")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_invalid_email), Toast.LENGTH_LONG).show();
                } else if (usr.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_username), Toast.LENGTH_LONG).show();
                } else if (pw1.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_password), Toast.LENGTH_LONG).show();
                } else if (!pw1.equals(pw2)){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_unmatch_password), Toast.LENGTH_LONG).show();
                } else if (birthday.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_dob), Toast.LENGTH_LONG).show();
                } else if (ht.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_height), Toast.LENGTH_LONG).show();
                } else if (wt.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_weight), Toast.LENGTH_LONG).show();
                } else if (genders.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_gender), Toast.LENGTH_LONG).show();
                } else if (stp.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_steps), Toast.LENGTH_LONG).show();
                } else if (addr.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_address), Toast.LENGTH_LONG).show();
                } else if (code.equals("")){
                    Toast.makeText(getActivity(), getString(R.string.signup_error_empty_postcode), Toast.LENGTH_LONG).show();
                }

                else {
                    Button selectedSex = (Button) vSignup.findViewById(genders.getCheckedRadioButtonId());
                    String sex = selectedSex.getText().toString();
                    SignupAsyncTask signup = new SignupAsyncTask();
                    signup.execute(name, surname, eml, birthday, ht, wt, sex, addr, code, lvl, stp, usr, pw1);
                }
            }
        });

        return vSignup;
    }

    private class SignupAsyncTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String...params){
            final Integer RESULT_OK = 0;
            final Integer RESULT_EMAIL_EXIST = 1;
            final Integer RESULT_USERNAME_EXIST = 2;


            if(!RestClient.isUsernameExist(params[11])){
                if(!RestClient.isEmailExist(params[2])){
                    try{
                        Integer id = RestClient.getNextUserId();
                        String name = params[0];
                        String surname = params[1];
                        String eml = params[2];
                        Date db = new SimpleDateFormat("MMM d, yyyy").parse(params[3]);
                        double ht = Double.parseDouble(params[4]);
                        double wt = Double.parseDouble(params[5]);
                        Character sex = params[6].charAt(0);
                        String addr = params[7];
                        String code = params[8];
                        int lvl = Integer.parseInt(params[9]);
                        int stp = Integer.parseInt(params[10]);
                        String usrname = params[11];
                        String pw = LoginActivity.passwordHash(params[12]);

                        Users user = new Users(id, name, surname, eml, db, ht, wt, sex, addr, code, lvl, stp);
                        RestClient.createUser(user);

                        Credential c = new Credential(id, usrname, pw, user);
                        RestClient.createCredential(c);

                        return RESULT_OK;
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    return RESULT_EMAIL_EXIST;
                }
            }
            return RESULT_USERNAME_EXIST;
        }


        @Override
        protected void onPostExecute(Integer result){
            switch (result){

                // RESULT_OK
                case 0:
                    Toast.makeText(getActivity(), getString(R.string.signup_success), Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.login_frame, new LoginFragment()).commit();
                    break;

                // RESULT_EMAIL_EXIST
                case 1:
                    EditText eml = (EditText) vSignup.findViewById(R.id.signupEmail);
                    eml.setText("");
                    Toast.makeText(getActivity(), getString(R.string.signup_error_exist_email), Toast.LENGTH_LONG).show();
                    break;

                // RESULT_USER_EXIST
                case 2:
                    EditText usr = (EditText) vSignup.findViewById(R.id.signupUsername);
                    usr.setText("");
                    Toast.makeText(getActivity(), getString(R.string.signup_error_exist_username), Toast.LENGTH_LONG).show();
                    break;

                default:


            }

        }
    }

}