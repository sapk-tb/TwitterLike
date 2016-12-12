package fr.sapk.twitterlike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.RegisterResponse;

/**
 * A login screen that offers login via email/password.
 */
@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean mAuthTask = false;

    // UI references.
    @ViewById
    EditText usernameField;
    @ViewById
    EditText passwordField;
    @ViewById
    EditText passwordFieldCheck;
    @ViewById
    EditText nameField;
    @ViewById
    EditText firstnameField;
    @ViewById
    View loginProgress;
    @ViewById
    View loginForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean checkData(String username, String password, String passwordCheck, String name, String firstname) {
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordField.setError(getString(R.string.error_invalid_password));
            passwordField.requestFocus();
            return false;
        }

        if(!passwordCheck.equals(password)){
            passwordFieldCheck.setError(getString(R.string.error_incorrect_password_check));
            passwordFieldCheck.requestFocus();
            return false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameField.setError(getString(R.string.error_field_required));
            usernameField.requestFocus();
            return false;
        } else if (!isUsernameValid(username)) {
            usernameField.setError(getString(R.string.error_invalid_username));
            usernameField.requestFocus();
            return false;
        }


        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            nameField.setError(getString(R.string.error_field_required));
            nameField.requestFocus();
            return false;
        }
        // Check for a valid firstnmae.
        if (TextUtils.isEmpty(firstname)) {
            firstnameField.setError(getString(R.string.error_field_required));
            firstnameField.requestFocus();
            return false;
        }

        return true;
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @Click(R.id.email_register_button)
    void attemptRegister() {
        if (mAuthTask == true) {
            return; //We all ready wait for response
        }

        // Reset errors.
        usernameField.setError(null);
        passwordField.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordCheck = passwordFieldCheck.getText().toString();
        String name = nameField.getText().toString();
        String firstname = firstnameField.getText().toString();

        if (checkData(username,password,passwordCheck,name,firstname)) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress(true);
            RegisterUser(username, password, name, firstname);
            showProgress(false);
        }
    }


    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            loginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //
    @Background
    public void RegisterUser(String username, String password, String name, String firstname){
        mAuthTask = true;
        if (!Api.isAvailable(this)) {
            Handler handler =  new Handler(this.getMainLooper());
            final Context context = this.getBaseContext();
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(context, "No internet connection !",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            try {
                RegisterResponse response = Api.Register(username, password, name, firstname);
                if (response.isOk()) {
                    Intent intent = new Intent(this, LoginActivity_.class);
                    startActivity(intent);
                } else {
                    usernameField.setError(getString(R.string.error_failed_registration));
                    usernameField.requestFocus();
                }
            } catch (final Exception ex) {
                Handler handler =  new Handler(this.getMainLooper());
                final Context context = this.getBaseContext();
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(context, ex.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        mAuthTask = false;
    }

}

