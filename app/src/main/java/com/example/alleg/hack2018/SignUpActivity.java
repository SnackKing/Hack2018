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

import com.example.alleg.hack2018.contracts.MessageContract;
import com.example.alleg.hack2018.contracts.UserContract.User;
import com.example.alleg.hack2018.utility.DBHelper;
import com.example.alleg.hack2018.utility.DBUtility;
import com.example.alleg.hack2018.utility.Passwords;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity  {
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    TextView nameField;
    TextView phoneField;
    TextView passwordField;
    TextView confirmPasswordField;

    // to access db
    DBHelper mDbHelp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField = findViewById(R.id.name);
        phoneField = findViewById(R.id.phone);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) phoneField;

        mPasswordView = (EditText) passwordField;
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });


        Button signUp = findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               onPush();
            }
        });
        Button goSignUp = findViewById(R.id.email_sign_up_button);
        goSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goSignUpIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(goSignUpIntent);
            }
        });
        mLoginFormView = findViewById(R.id.login_form);

        // get a db helper
        this.mDbHelp = new DBHelper(this);
    }

    @Override
    protected void onDestroy() {
        this.mDbHelp.close();

        super.onDestroy();
    }

    private void onPush(){
        String name = nameField.getText().toString();
        String phone = phoneField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        boolean isValid = validate(name, phone, password, confirmPassword);
        if(isValid) {
            signUp(name, phone, password);
        }
    }

    private boolean validate(String name,String phone, String password, String confirmPassword){
        boolean isValid = true;
        if(name.length() == 0){
            isValid = false;
            nameField.setError("Name can't be blank");
        }
        if(phone.length() != 10){
            isValid = false;
            phoneField.setError("Invalid Phone");
        }
        if(password.length() == 0){
            isValid = false;
            passwordField.setError("Password can't be blank");
        }
        if(!password.equals(confirmPassword)){
            isValid = false;
            confirmPasswordField.setError("Passwords don't match");
        }
        return isValid;
    }

    private int signUp(String name, String phone, String password){

        DBUtility util = new DBUtility(getApplicationContext());

        SQLiteDatabase db = this.mDbHelp.getWritableDatabase();
        SQLiteDatabase dbr = this.mDbHelp.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + User.TABLE_NAME + " WHERE "
                + User.COLUMN_NAME_PHONE_NUMBER + " = " + phone;
        Cursor cursor = dbr.rawQuery(selectQuery, new String[] {});
        if (cursor.getCount() != 0) {
            //phone number is a duplicate
            return -1;
        }

        ContentValues values = new ContentValues();

        byte[] salt = Passwords.getNextSalt();
        byte[] salted = Passwords.hash(password.toCharArray(), salt);

        String key = String.valueOf(UUID.randomUUID());

        values.put(User.COLUMN_NAME_NAME, name);
        values.put(User.COLUMN_NAME_SALT, salt);
        values.put(User.COLUMN_NAME_PASSWORD, salted);
        values.put(User.COLUMN_NAME_PHONE_NUMBER, phone);
        values.put(User.COLUMN_NAME_RESIDENT, 1);
        values.put(User._ID, key);

        util.insertToDb(db, User.TABLE_NAME, key,null, values);

        // log the newly created user in
        util.login(mDbHelp.getReadableDatabase(), phone, password);
        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
        return 1;
    }
}

