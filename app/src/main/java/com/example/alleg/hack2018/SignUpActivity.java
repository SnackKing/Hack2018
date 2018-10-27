package com.example.alleg.hack2018;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.alleg.hack2018.models.UserContract.User;
import com.example.alleg.hack2018.utility.DBHelper;
import com.example.alleg.hack2018.utility.DBUtility;
import com.example.alleg.hack2018.utility.Passwords;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity  {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.phone);

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


        Button goSignUp = findViewById(R.id.email_sign_up_button);
        goSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               onPush();
            }
        });
        goSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goSignUpIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(goSignUpIntent);
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
    }
    private void onPush(){
        TextView nameField = findViewById(R.id.name);
        String name = nameField.getText().toString();
        TextView phoneField = findViewById(R.id.phone);
        String phone = phoneField.getText().toString();
        TextView passwordField = findViewById(R.id.password);
        String password = passwordField.getText().toString();

        signUp(name,phone,password);
    }

    private void signUp(String name, String phone, String password){
        DBHelper mDBHelp = new DBHelper(this);

        SQLiteDatabase db = mDBHelp.getWritableDatabase();

        ContentValues values = new ContentValues();

        byte[] salt = Passwords.getNextSalt();
        byte[] salted = Passwords.hash(password.toCharArray(), salt);

        values.put(User.COLUMN_NAME_NAME, name);
        values.put(User.COLUMN_NAME_SALT, salt);
        values.put(User.COLUMN_NAME_PASSWORD, salted);
        values.put(User.COLUMN_NAME_PHONE_NUMBER, phone);
        values.put(User.COLUMN_NAME_RESIDENT, 1);

        DBUtility.insertToDb(db, User.TABLE_NAME, null, values);
    }
}

