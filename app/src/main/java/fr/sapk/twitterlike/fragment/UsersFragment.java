package fr.sapk.twitterlike.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import fr.sapk.twitterlike.R;
import fr.sapk.twitterlike.adapter.UserAdapter;
import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.UsersResponse;

/**
 * The type Users fragment.
 */
@EFragment
public class UsersFragment extends Fragment {

    //UI
    SwipeRefreshLayout swipeLayout;
    RecyclerView recyclerView;
    UserAdapter adapter;

    public void setToken(String token) {
        this.token = token;
    }
@FragmentArg
    String token;

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
        GetUsers(token);
        swipeLayout.setRefreshing(false);
        //new GetUsersAsyncTask(UsersFragment.this.getActivity()).execute();
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

    @Background
    void GetUsers(String token){
        if (!Api.isAvailable(this.getContext())) {
            return;
        }
        try {
            UsersResponse response = Api.GetUsers(token);
            if(response.isOk()){
                adapter.setUser(response.getUsers());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
