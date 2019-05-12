package com.example.trackerapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class LoginFragment extends Fragment {
    View vLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vLogin = inflater.inflate(R.layout.fragment_login, container, false);


        Button signupButton = (Button) vLogin.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.login_frame, new SignupFragment()).commit();
            }
        });


        final EditText username = (EditText) vLogin.findViewById(R.id.loginUsername);
        final EditText password = (EditText) vLogin.findViewById(R.id.loginPassword);

        AppCompatCheckBox checkbox = (AppCompatCheckBox) vLogin.findViewById(R.id.showPassword);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        Button loginButton = (Button) vLogin.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.login_error_empty_username), Toast.LENGTH_LONG).show();
                    clearLoginUsernameAndPassword();
                } else if (password.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.login_error_empty_password), Toast.LENGTH_LONG).show();
                    clearLoginUsernameAndPassword();
                } else {
                    LoginAsyncTask getUser = new LoginAsyncTask();
                    getUser.execute(username.getText().toString(), password.getText().toString());
                }

            }
        });

        return vLogin;
    }


    private class LoginAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String...params){
            String usr = params[0];
            String pw = "";
            try{
                pw = LoginActivity.passwordHash(params[1]);
            } catch (Exception e){
                e.printStackTrace();
            }
            Log.i("passwordHash: ", pw);
            return RestClient.AuthenticateUser(usr, pw);
        }

        @Override
        protected void onPostExecute(String result){
            if (!result.equals("[]")){

                try{
                    JSONArray userJson = new JSONArray(result);
                    JSONObject user = userJson.getJSONObject(0).getJSONObject("userId");

                    int id = user.getInt("userId");
                    String name = user.getString("name");

                    SessionManagement session = new SessionManagement(getContext());
                    session.createLoginSession(id, name);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                clearLoginUsernameAndPassword();
                Toast.makeText(getActivity(), getString(R.string.login_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void clearLoginUsernameAndPassword(){
        EditText username = vLogin.findViewById(R.id.loginUsername);
        EditText password = vLogin.findViewById(R.id.loginPassword);
        username.setText("");
        password.setText("");
    }

}


