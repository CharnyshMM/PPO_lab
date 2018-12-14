package com.example.mikita.ppo_lab;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mikita.ppo_lab.rss.FeedItem;
import com.example.mikita.ppo_lab.rss.FeedsAdapter;
import com.example.mikita.ppo_lab.rss.RssReader;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements RssReader.OnFeedItemLoadedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private FeedsAdapter feedsAdapter;
    private String address;
    FeedsAdapter.OnItemClickListener onItemClickListener;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
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
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.news__recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        onItemClickListener = new FeedsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FeedItem item) {
                Intent intent = new Intent(getContext(), RssWebView.class);
                intent.putExtra("URL", item.getLink());
                startActivity(intent);
            }
        };
        feedsAdapter = new FeedsAdapter(getContext(), new ArrayList<FeedItem>(), onItemClickListener);
        recyclerView.setAdapter(feedsAdapter);


       // address = "http://www.sciencemag.org/rss/news_current.xml";
        address = "https://news.tut.by/rss/all.rss";
        RssReader rssReader = new RssReader(getContext(), address);
        rssReader.addOnFeedItemLoadedListener(this);


        rssReader.addOnExecutedListener(new RssReader.OnItemsLoadedListener() {
            @Override
            public void onItemsLoaded() {
                Toast.makeText(getContext(), "Feed loaded", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemsLoadFailed(Exception e) {
                Toast.makeText(getContext(), "Error occured", Toast.LENGTH_LONG).show();
            }
        });

        rssReader.execute();
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onFeedItemLoaded(final FeedItem item) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                feedsAdapter.addItem(item);
            }
        });

    }

    @Override
    public void onFeedItemLoadFailed(Exception e) {
        Toast.makeText(getContext(), "Error occured when loading item", Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

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

}
