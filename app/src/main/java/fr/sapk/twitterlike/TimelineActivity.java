package fr.sapk.twitterlike;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

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

import java.util.ArrayList;
import java.util.List;

import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.UserResponse;
import fr.sapk.twitterlike.fragment.MessagesFragment;
import fr.sapk.twitterlike.fragment.UsersFragment;
import fr.sapk.twitterlike.fragment.WriteMsgDialog;
import fr.sapk.twitterlike.session.Session;

public class TimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                WriteMsgDialog.getInstance(Session.token, Session.userId).show(TimelineActivity.this.getFragmentManager(), "write");
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
        View header = navigationView.getHeaderView(0); //Acces header

        //Get data and pass the header
        (new GetCurrentUserTask(drawer.getContext(), Session.token, header)).execute((Void) null);


        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        if (pager != null) {
            Adapter adapter = new Adapter(getSupportFragmentManager());
            adapter.addFragment(new MessagesFragment(), "Messages");
            adapter.addFragment(new UsersFragment(), "Users");

            pager.setAdapter(adapter);
        }
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

    public void doLogout() {
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("secure-token");
        editor.remove("userid");
        editor.commit(); //Save to local pref for later use
        Session.token = "";
        Session.userId = "";
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
            doLogout();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * The type Adapter.
     */
    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        /**
         * Instantiates a new Adapter.
         *
         * @param fm the fm
         */
        Adapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Add fragment.
         *
         * @param fragment the fragment
         * @param title    the title
         */
        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        /**
         * Gets item.
         *
         * @param position the position
         * @return the item
         */
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        /**
         * Gets count.
         *
         * @return the count
         */
        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         * Get page title char sequence.
         *
         * @param position the position
         * @return the char sequence
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
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
            if (!Api.isAvailable(context)) {
                return false;
            }
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
