package com.example.alleg.hack2018;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alleg.hack2018.utility.Codes;
import com.example.alleg.hack2018.utility.DBHelper;
import com.example.alleg.hack2018.utility.DBUtility;

import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private DBHelper mDbHelp;

    // UI references.
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mPhoneView = findViewById(R.id.phone);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });

        Button phoneSignInButton = findViewById(R.id.phone_sign_in_button);
        phoneSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DBUtility util = new DBUtility(getApplicationContext());

                if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }else{// Write you code here if permission already given.
                Codes result = util.login(mDbHelp.getReadableDatabase(), mPhoneView.getText().toString(),mPasswordView.getText().toString());
                if(result == Codes.LoginBadPhone){
                    mPhoneView.setError("There are no accounts with that number");

                }
                else if(result == Codes.LoginBadPassword){
                    mPasswordView.setError("Incorrect Password");
                }
                else if(result == Codes.LoginSuccess){
                    Intent intent = new Intent(LoginActivity.this,MessagesActivity.class);
                    startActivity(intent);
                }
            }}
        });

        Button goSignInButton = findViewById(R.id.phone_go_sign_up_button);
        goSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goSignUpIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(goSignUpIntent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        this.mDbHelp = new DBHelper(this);
    }

    @Override
    protected void onDestroy() {
        mDbHelp.close();

        super.onDestroy();
    }
}