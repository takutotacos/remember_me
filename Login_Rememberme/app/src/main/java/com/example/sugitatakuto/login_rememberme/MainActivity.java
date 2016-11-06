package com.example.sugitatakuto.login_rememberme;


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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String USER_TOKEN_KEY = "userTokenKey";

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    private EditText mEditTextEmail, mEditTextPassword;
    private Button mLoginButton;
    private CheckBox mCheckBoxRememberMe;

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
            sendLoginRequest(email, password);
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

    private void sendLoginRequest(final String email, final String password) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JSONObject joLoginRequest = new JSONObject();
        try {
            joLoginRequest.put("email", email);
            joLoginRequest.put("password", password);
            String url = "http://127.0.0.2/db_connection.php";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.POST, url, joLoginRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            processLoginResponse(response, password);
                        }
                    }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                try {
                                    Toast.makeText(MainActivity.this, "Wrong email/password combination.", Toast.LENGTH_LONG).show();
                                } catch (NullPointerException e) {
                                    Log.e(TAG, e.getLocalizedMessage(), e);
                                }
                            }
                        });
                    mRequestQueue.add(jsObjRequest);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

    private void processLoginResponse(JSONObject loginResponse, String successfulPassword) {
        Log.i(TAG, "Got Login Response: ¥n" + loginResponse.toString());
        try {
            Log.i(TAG, "Data found!!!!!!: n¥" + loginResponse.getString("email"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        try {
            if(loginResponse != null) {
                if(mCheckBoxRememberMe.isChecked()) {
                    System.out.println("The checkbox was checked...");
                    // @TODO What value to be stored.
                    sharedPreferencesEditor.putString(USER_TOKEN_KEY, loginResponse.getString("email"));
                    sharedPreferencesEditor.commit();
                }
                startPostLoginActivity();
                finish();
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
