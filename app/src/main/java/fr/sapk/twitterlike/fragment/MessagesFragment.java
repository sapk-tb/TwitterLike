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
import fr.sapk.twitterlike.adapter.MessageAdapter;
import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.MessagesResponse;
import fr.sapk.twitterlike.session.Session;

/**
 * The type Messages fragment.
 */
public class MessagesFragment extends Fragment {

    //UI
    SwipeRefreshLayout swipeLayout;
    RecyclerView recyclerView;
    MessageAdapter adapter;

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
        View v = inflater.inflate(R.layout.fragment_messages, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.messages_list);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.messages_swiperefresh);
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
        new GetMessagesAsyncTask(MessagesFragment.this.getActivity()).execute();
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
        adapter = new MessageAdapter(this.getActivity());
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
     * The type Get messages async task.
     */
    protected class GetMessagesAsyncTask extends AsyncTask<String, Void, Boolean> {

        Context context;
        private MessagesResponse response = null;
        /**
         * Instantiates a new Get messages async task.
         *
         * @param context the context
         */
        GetMessagesAsyncTask(final Context context) {
            this.context = context;
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
                response = Api.GetMessages(Session.token); //TODO Session
                return response.isOk();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * On post execute.
         *
         * @param success the success state
         */
        @Override
        public void onPostExecute(final Boolean success) {
            if (success) {
                adapter.addMessage(response.getMessages());
            }
            swipeLayout.setRefreshing(false);
        }
    }
}
