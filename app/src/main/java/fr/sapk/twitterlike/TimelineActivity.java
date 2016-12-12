package fr.sapk.twitterlike;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.UserResponse;
import fr.sapk.twitterlike.fragment.MessagesFragment;
import fr.sapk.twitterlike.fragment.MessagesFragment_;
import fr.sapk.twitterlike.fragment.UsersFragment;
import fr.sapk.twitterlike.fragment.UsersFragment_;
import fr.sapk.twitterlike.fragment.WriteMsgDialog;
import fr.sapk.twitterlike.session.Session_;

@EActivity(R.layout.activity_timeline)
public class TimelineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MessagesFragment mFragment;
    private UsersFragment uFragment;

    @Pref
    Session_ session;

    @ViewById
    NavigationView navigationView;
    /*
    @ViewById
    TextView identityText;
    @ViewById
    TextView usernameText;
    */
    @ViewById
    FloatingActionButton floatingButton;
    @ViewById
    ViewPager viewpager;
    @ViewById
    DrawerLayout drawerLayout;
    @ViewById
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TwitterLike", "Create Timeline activity : " + session.userId().get() + " : " + session.token().get());
        if (!(session.token().exists() && !session.token().get().equals("") && session.userId().exists() && !session.userId().get().equals(""))) {
            finish();
        }
    }
    @AfterViews
    public void initUI() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (viewpager != null) {
            Adapter adapter = new Adapter(getSupportFragmentManager());
            mFragment = MessagesFragment_.builder().token(session.token().get()).build();
            uFragment = UsersFragment_.builder().token(session.token().get()).build();
            adapter.addFragment(mFragment, "Messages");
            adapter.addFragment(uFragment, "Users");
            viewpager.setAdapter(adapter);
        }

        View header = navigationView.getHeaderView(0); //Acces header
        GetCurrentUser((TextView) header.findViewById(R.id.identityText), (TextView) header.findViewById(R.id.usernameText));
    }
    @Click({R.id.floatingButton})
    void onFabClick(){
        WriteMsgDialog.getInstance().show(this.getFragmentManager(), "write");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            mFragment.refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doLogout() {
        session.clear();
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

        drawerLayout.closeDrawer(GravityCompat.START);
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

    @Background
    public void GetCurrentUser(TextView identityText, TextView usernameText) {
        if (!Api.isAvailable(this)) {
            Handler handler =  new Handler(this.getMainLooper());
            final Context context = this.getBaseContext();
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(context, "No internet connection !",
                            Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        try {
            UserResponse response = Api.CurrentUser(session.token().get());
            if (response.isOk()) {
                identityText.setText(response.getFirstname() + " " + response.getName());
                usernameText.setText("@" + response.getUsername());
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
}