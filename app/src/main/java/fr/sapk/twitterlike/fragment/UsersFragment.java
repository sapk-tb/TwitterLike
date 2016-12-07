package fr.sapk.twitterlike.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.sapk.twitterlike.R;
import fr.sapk.twitterlike.adapter.UserAdapter;
import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.UsersResponse;
import fr.sapk.twitterlike.session.Session;

/**
 * The type Users fragment.
 */
public class UsersFragment extends Fragment {

    //UI
    SwipeRefreshLayout swipeLayout;
    RecyclerView recyclerView;
    UserAdapter adapter;

    /**
     * On create view view.
     *
     * @param inflater           the inflater
     * @param container          the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.users_list);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.users_swiperefresh);
        setupRefreshLayout();
        setupRecyclerView();
        return v;
    }

    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        loading();
    }

    /**
     * Load messages
     */
    private void loading() {
        swipeLayout.setRefreshing(true);
        new GetUsersAsyncTask(UsersFragment.this.getActivity()).execute();
    }

    /**
     * Setup refresh layout
     */
    private void setupRefreshLayout() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading();
            }
        });
        swipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
    }

    /**
     * Setup recycler view.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new UserAdapter(this.getActivity());
        recyclerView.setAdapter(adapter);

        // Add this.
        // Two scroller could have problem.
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
    }

    /**
     * The type Get users async task.
     */
    protected class GetUsersAsyncTask extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        private UsersResponse response = null;

        /**
         * Instantiates a new Get users async task.
         *
         * @param ctx the ctx
         */
        GetUsersAsyncTask(Context ctx) {
            this.context = ctx;
        }

        /**
         * Do in background list.
         *
         * @param params the params
         * @return the list
         */
        @Override
        protected Boolean doInBackground(String... params) {

            if (!Api.isAvailable(context)) {
                return null;
            }
            try {
                response = Api.GetUsers(Session.token);
                return response.isOk();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * On post execute.
         *
         * @param success the success of the request
         */
        @Override
        public void onPostExecute(final Boolean success) {
            if (success) {
                adapter.setUser(response.getUsers());
            }
            swipeLayout.setRefreshing(false);
        }
    }
}
