package com.example.lollipop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.lollipop.ViewModel.FeedViewModelGetQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PeopleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
 {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PeopleFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<FeedViewModelGetQuery.User> mUsers;
    private RecyclerView recyclerView;
    private MyApolloClient myApolloClient;
    Handler myHandler;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeRefreshLayout;

    public PeopleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeopleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeopleFragment newInstance(String param1, String param2) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        myApolloClient = (MyApolloClient) getActivity().getApplication();
        myHandler = new Handler();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        getPosts();
        return view;
    }


     private void getPosts() {
        Log.d(TAG, "getPosts: Post");
        myApolloClient.apolloClient().query(
                FeedViewModelGetQuery.builder().build()).enqueue(new ApolloCall.Callback<FeedViewModelGetQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<FeedViewModelGetQuery.Data> response) {
                Log.d(TAG, "onResponse: Got the query");
                mUsers = response.data().core().users();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initRecyclerView();

                    }
                });

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d(TAG, e.toString());
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: Ya estoy esperando 1 sec");
                        getPosts();

                    }
                }, 1000);
            }
        });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecycleView: init recycle view");
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(),mUsers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

     @Override
     public void onRefresh() {
         Log.i(TAG, "onRefresh: I could refresh");
         new Handler().postDelayed(new Runnable() {
             @Override public void run() {
                 getPosts();
                 swipeRefreshLayout.setRefreshing(false);
             }
         }, 500);
     }

     /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
