package fr.sapk.twitterlike;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.LoginResponse;
import fr.sapk.twitterlike.api.message.UserResponse;

public class TimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle extras = getIntent().getExtras(); //Get token
        if (extras != null) {
            token = extras.getString("secure-token");
        }
        View header = navigationView.getHeaderView(0); //Acces header

        //Get data and pass the header
        (new GetCurrentUserTask(drawer.getContext(), token, header)).execute((Void) null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
            //TODO open settings view
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_timeline) {
            //TODO view my message list
        } else if (id == R.id.nav_userlist) {
            //TODO view user lsit
        } else if (id == R.id.nav_settings) {
            //TODO view setting page
        } else if (id == R.id.nav_profile) {
            //TODO open settings view
        } else if (id == R.id.nav_logout) {
            //TODO logout and go to login page
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetCurrentUserTask extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        private final String token;
        private final View header;

        private UserResponse response = null;

        GetCurrentUserTask(Context c, String token , View header) {
            context = c;
            this.token = token;
            this.header = header;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                response = Api.CurrentUser(token);
                return response.isOk();
            } catch (Exception ex) {
                Toast.makeText(context, ex.toString(),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                ((TextView) header.findViewById(R.id.identityText)).setText(response.getFirstname() +" "+response.getName());
                ((TextView) header.findViewById(R.id.usernameText)).setText("@"+response.getUsername());
            } else {
                //TODO detect if not logged and go back to login
            }
        }

    }
}
