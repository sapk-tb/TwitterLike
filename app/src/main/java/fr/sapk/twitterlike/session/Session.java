package fr.sapk.twitterlike.session;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * The type Session.
 */
@SharedPref(value=SharedPref.Scope.APPLICATION_DEFAULT)
public interface Session {
    @DefaultString("")
    String token();

    @DefaultString("")
    String userId();
}
