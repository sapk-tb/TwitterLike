package fr.sapk.twitterlike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.LoginResponse;
import fr.sapk.twitterlike.api.message.UserResponse;
import fr.sapk.twitterlike.session.Session_;


/**
 * A login screen that offers login via email/password.
 */

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean mAuthTask = false;

    // UI references.
    @ViewById
    EditText usernameForm;
    @ViewById
    EditText passwordForm;
    @ViewById
    View loginProgress;
    @ViewById
    View loginForm;

    @Pref
    Session_ session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(session.token().exists() && !session.token().get().equals("") && session.userId().exists() && !session.userId().get().equals("") ){
            //We have a token and userid
            Log.d("TwitterLike", "token store in prefs: " +  session.token().get());
            Log.d("TwitterLike", "userId store in prefs: " +  session.userId().get());
            Intent intent =  new Intent(this.getBaseContext(), TimelineActivity_.class);
            startActivity(intent);
        }
    }

    @Click({R.id.registerButton})
    void goRegister() {
        Intent intent =  new Intent(this,RegisterActivity_.class);
        startActivity(intent);
    }

    private boolean checkData(String username, String password) {
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordForm.setError(getString(R.string.error_invalid_password));
            passwordForm.requestFocus();
            return false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameForm.setError(getString(R.string.error_field_required));
            usernameForm.requestFocus();
            return false;
        } else if (!isUsernameValid(username)) {
            usernameForm.setError(getString(R.string.error_invalid_username));
            usernameForm.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @Click({R.id.email_sign_in_button})
    void attemptLogin() {
        if (mAuthTask == true) {
            return; //We all ready wait for response
        }

        // Reset errors.
        usernameForm.setError(null);
        passwordForm.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameForm.getText().toString();
        String password = passwordForm.getText().toString();

        if (checkData(username,password)) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            LoginUser(username, password);
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
    @Background
    public void LoginUser(String username, String password){
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
        }else {
            try {
                LoginResponse response = Api.Login(username, password);
                if (response.isOk()) {
                    session.token().put(response.getSecureToken());
                    session.userId().put(response.getUserId());
                    Log.d("Login", "token store in prefs: " +  session.token().get());
                    Log.d("Login", "userId store in prefs: " +  session.userId().get());
                    Intent intent =  new Intent(this, TimelineActivity_.class);
                    startActivity(intent);
                } else {
                    passwordForm.setError(getString(R.string.error_incorrect_password));
                    passwordForm.requestFocus();
                }
            } catch (Exception ex) {
                Handler handler =  new Handler(this.getMainLooper());
                final Context context = this.getBaseContext();
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(context, "Une erreur est apparue lors de la tentative de login",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        mAuthTask = false;
    }

}

