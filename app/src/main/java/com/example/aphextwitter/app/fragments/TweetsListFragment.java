package com.example.aphextwitter.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.aphextwitter.app.EndlessScrollListener;
import com.example.aphextwitter.app.R;
import com.example.aphextwitter.app.TweetsAdapter;
import com.example.aphextwitter.app.models.Tweet;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Activities that contain this fragment must implement the
 * {@link TweetsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public abstract class TweetsListFragment extends Fragment implements OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    protected TweetsAdapter twAdapter;
    protected long lowest_tweet_id = 0;
    protected PullToRefreshLayout mPullToRefreshLayout;
    private OnFragmentInteractionListener mListener;
    private ListView lvTweets;
    private long highest_tweet_id;

    public TweetsListFragment() {
        // Required empty public constructor
    }

    public TweetsAdapter getAdapter() {
        return twAdapter;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TweetsListFragment.
     */
//    TODO: Rename and change types and number of parameters
//    public static TweetsListFragment newInstance(String param1, String param2) {
//        TweetsListFragment fragment = new TweetsListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        lvTweets = (ListView) view.findViewById(R.id.lvTweets);

        twAdapter = new TweetsAdapter(getActivity(), new ArrayList<Tweet>());
        lvTweets.setAdapter(twAdapter);

        loadTweets(0, -1);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadTweets(0, lowest_tweet_id - 1);
            }
        });

        return view;
    }

    protected abstract void loadTweets(final long since_id, long tweet_id);

    @Override
    public void onRefreshStarted(View view) {
        highest_tweet_id = twAdapter.getItem(0).getId();
        loadTweets(highest_tweet_id, -1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getMyName() {
        return getClass().getSimpleName();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
