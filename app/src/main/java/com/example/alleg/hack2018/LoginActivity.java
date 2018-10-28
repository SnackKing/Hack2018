package com.example.alleg.hack2018;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alleg.hack2018.utility.DBHelper;
import com.example.alleg.hack2018.utility.DBUtility;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

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
        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });

        Button phoneSignInButton = (Button) findViewById(R.id.phone_sign_in_button);
        phoneSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DBUtility util = new DBUtility(getApplicationContext());
                int result = util.login(mDbHelp.getReadableDatabase(), mPhoneView.getText().toString(),mPasswordView.getText().toString());
                if(result == -1){
                    mPhoneView.setError("There are no accounts with that number");

                }
                else if(result == -2){
                    mPasswordView.setError("Incorrect Password");
                }
                else if(result == 1){
                    Intent intent = new Intent(LoginActivity.this,MessagesActivity.class);
                    startActivity(intent);
                }
            }
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