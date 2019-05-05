package com.example.trackerapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
            return RestClient.AuthenticateUser(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result){
            if (!result.equals("[]")){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
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


