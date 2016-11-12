package com.example.sugitatakuto.login_rememberme.com.example.sugitatakuto.login_rememberme.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.sugitatakuto.login_rememberme.R;
import com.example.sugitatakuto.login_rememberme.com.example.sugitatakuto.login_rememberme.asyncHttp.AsyncOkHttp;
import com.example.sugitatakuto.login_rememberme.com.example.sugitatakuto.login_rememberme.asyncHttp.AsyncResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String USER_TOKEN_KEY = "userTokenKey";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    private EditText mEditTextEmail, mEditTextPassword;
    private Button mLoginButton;
    private CheckBox mCheckBoxRememberMe;
    AsyncOkHttp asyncOkHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        takeUserWithSharedPreferencesToTheNextPage();
        setUIComponent();
    }

    private void takeUserWithSharedPreferencesToTheNextPage() {
        if(sharedPreferences.contains(USER_TOKEN_KEY)) {
            String userToken = sharedPreferences.getString(USER_TOKEN_KEY, "");
            if(!userToken.trim().isEmpty()) {
                Log.d(TAG, "Usertoken exists. + ¥n" + userToken);
                startPostLoginActivity();
            }
        }
    }
    private void setUIComponent() {
        mEditTextEmail = (EditText)findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText)findViewById(R.id.editTextPassword);
        mLoginButton = (Button)findViewById(R.id.buttonLogin);
        mCheckBoxRememberMe = (CheckBox)findViewById(R.id.checkboxRmemberMe);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        // Reset errors
        mEditTextPassword.setError(null);
        mEditTextPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if a password input is valid
        if(!isPasswordValid(password)) {
            mEditTextPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEditTextPassword;
            cancel = true;
        }

        // Check if an email input is valid
        if(!isEmailValid(email)) {
            mEditTextEmail.setError(getString(R.string.error_invalid_input));
            focusView = mEditTextEmail;
            cancel = true;
        }

        if(cancel) {
            // There was a validation error. Dont attempt login and focus the first form element with an error.
            focusView.requestFocus();
        } else {
            new AsyncOkHttp(this).execute(email, password);
        }
    }


    private boolean isPasswordValid(String password) {
        return !password.isEmpty() && password.length() > 3;
    }

    private boolean isEmailValid(String email) {
        return !email.isEmpty() && email.contains("@");
    }

    private void startPostLoginActivity() {
        Intent intent = new Intent(this, LoginSuccessfulActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void processFinish(JSONObject output) {

        if(output != null) {
            String email = null;
            try {
                email = output.getString("email");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (mCheckBoxRememberMe.isChecked()) {
                sharedPreferencesEditor.putString(USER_TOKEN_KEY, email);
                sharedPreferencesEditor.commit();
            }
            startPostLoginActivity();
            finish();
        } else {
            System.out.println("Got the null data or status code suggesting not 'OK'");
        }
    }

}
