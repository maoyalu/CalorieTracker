package com.example.trackerapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
                } else if (!eml.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
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

    private class SignupAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String...params){

            if(false){
//            if(firstName.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_firstname), Toast.LENGTH_LONG).show();
//            } else if (lastName.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_lastname), Toast.LENGTH_LONG).show();
//            } else if (email.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_email), Toast.LENGTH_LONG).show();
//            } else if (!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_invalid_email), Toast.LENGTH_LONG).show();
//            } else if (pwd.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_password), Toast.LENGTH_LONG).show();
//            } else if (!pwd.getText().toString().equals(pwdConfirm.getText().toString())){
//
//                Toast.makeText(getActivity(), getString(R.string.signup_error_unmatch_password), Toast.LENGTH_LONG).show();
//            } else if (dob.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_dob), Toast.LENGTH_LONG).show();
//            } else if (height.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_height), Toast.LENGTH_LONG).show();
//            } else if (weight.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_weight), Toast.LENGTH_LONG).show();
//            } else if (genders.getCheckedRadioButtonId() == -1){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_gender), Toast.LENGTH_LONG).show();
//            } else if (steps.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_steps), Toast.LENGTH_LONG).show();
//            } else if (address.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_address), Toast.LENGTH_LONG).show();
//            } else if (postcode.getText().toString().equals("")){
//                Toast.makeText(getActivity(), getString(R.string.signup_error_empty_postcode), Toast.LENGTH_LONG).show();
            } else {
    //                Toast.makeText(getActivity(), getString(R.string.signup_success), Toast.LENGTH_LONG).show();
    //                FragmentManager fragmentManager = getFragmentManager();
    //                fragmentManager.beginTransaction().replace(R.id.login_frame, new LoginFragment()).commit();

    //            PostSignupAsyncTask signup = new PostSignupAsyncTask();
    //            signup.execute();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean resultOk){
            if(resultOk){
                Toast.makeText(getActivity(), getString(R.string.signup_success), Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.login_frame, new LoginFragment()).commit();
            }

        }
    }

//    private boolean isValidFirstName(String firstname){
//        if(firstname.equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_firstname), Toast.LENGTH_LONG).show();
//        }
//    }



//    private void signup(){
//        EditText firstName = (EditText) vSignup.findViewById(R.id.signupFirstName);
//        EditText lastName = (EditText) vSignup.findViewById(R.id.signupLastName);
//        EditText email = (EditText) vSignup.findViewById(R.id.signupEmail);
//        EditText pwd = (EditText) vSignup.findViewById(R.id.signupPassword);
//        EditText pwdConfirm = (EditText) vSignup.findViewById(R.id.signupPasswordConfirm);
//        EditText dob = (EditText) vSignup.findViewById(R.id.signupDob);
//        EditText height = (EditText) vSignup.findViewById(R.id.signupHeight);
//        EditText weight = (EditText) vSignup.findViewById(R.id.signupWeight);
//        RadioGroup genders = (RadioGroup) vSignup.findViewById(R.id.signupGenderGroup);
//        RadioButton selectedGender;
//        Spinner activityLevel = (Spinner) vSignup.findViewById(R.id.activityLevelSpinner);
//        EditText steps = (EditText) vSignup.findViewById(R.id.signupStepsPerMile);
//        EditText address = (EditText) vSignup.findViewById(R.id.signupAddress);
//        EditText postcode = (EditText) vSignup.findViewById(R.id.signupPostcode);
//
//        if(firstName.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_firstname), Toast.LENGTH_LONG).show();
//        } else if (lastName.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_lastname), Toast.LENGTH_LONG).show();
//        } else if (email.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_email), Toast.LENGTH_LONG).show();
//        } else if (!email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_invalid_email), Toast.LENGTH_LONG).show();
//        } else if (pwd.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_password), Toast.LENGTH_LONG).show();
//        } else if (!pwd.getText().toString().equals(pwdConfirm.getText().toString())){
//
//            Toast.makeText(getActivity(), getString(R.string.signup_error_unmatch_password), Toast.LENGTH_LONG).show();
//        } else if (dob.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_dob), Toast.LENGTH_LONG).show();
//        } else if (height.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_height), Toast.LENGTH_LONG).show();
//        } else if (weight.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_weight), Toast.LENGTH_LONG).show();
//        } else if (genders.getCheckedRadioButtonId() == -1){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_gender), Toast.LENGTH_LONG).show();
//        } else if (steps.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_steps), Toast.LENGTH_LONG).show();
//        } else if (address.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_address), Toast.LENGTH_LONG).show();
//        } else if (postcode.getText().toString().equals("")){
//            Toast.makeText(getActivity(), getString(R.string.signup_error_empty_postcode), Toast.LENGTH_LONG).show();
//        } else {
//                Toast.makeText(getActivity(), getString(R.string.signup_success), Toast.LENGTH_LONG).show();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.login_frame, new LoginFragment()).commit();
//
////            PostSignupAsyncTask signup = new PostSignupAsyncTask();
////            signup.execute();
//        }
//    }

//    private class PostSignupAsyncTask extends AsyncTask<Void, Void, String>{
//        @Override
//        protected String doInBackground(Void...params){
//            GetIdAsyncTask getId = new GetIdAsyncTask();
//            getId.execute();
//            return getString(R.string.signup_success);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.login_frame, new LoginFragment()).commit();
//        }
//    }

//    private class GetIdAsyncTask extends  AsyncTask<Void, Void, Integer>{
//        @Override
//        protected Integer doInBackground(Void...params){
//            return RestClient.getNextUserId();
//        }
//
//        @Override
//        protected void onPostExecute(Integer nextId){
//            try{
//                String name = firstName.getText().toString();
//                String surname = lastName.getText().toString();
//                String eml = email.getText().toString();
//                Date db = new SimpleDateFormat("MMM dd, yyyy").parse(dob.getText().toString());
//                double ht = Double.parseDouble(height.getText().toString());
//                double wt = Double.parseDouble(weight.getText().toString());
//                Character sex = selectedGender.getText().toString().charAt(0);
//                String addr = address.getText().toString();
//                String code = postcode.getText().toString();
//                int lvl = Integer.parseInt(levelOfActivity.getSelectedItem().toString());
//                int stp = Integer.parseInt(steps.getText().toString());
//
//                User user = new User(nextId, name, surname, eml, db, ht, wt, sex, addr, code, lvl, stp);
//                RestClient.createUser(user);
//            } catch (Exception e){
//                //
//            }
//
//        }
//
//    }

}