package com.example.mikita.ppo_lab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mikita.ppo_lab.rss.FeedItem;
import com.example.mikita.ppo_lab.rss.FeedsAdapter;
import com.example.mikita.ppo_lab.rss.RssReader;
import com.example.mikita.ppo_lab.storage.CacheRepository;
import com.example.mikita.ppo_lab.storage.OnProgressListener;
import com.google.firebase.auth.FirebaseAuth;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class NewsFragment extends Fragment implements RssReader.OnFeedItemLoadedListener, RssReader.OnItemsLoadedListener, OnProgressListener {


    private RecyclerView recyclerView;
    private FeedsAdapter feedsAdapter;
    private RssReader rssReader;
    private ProgressDialog progressDialog;
    private boolean loadedFromCache = false;
    FeedsAdapter.OnItemClickListener onItemClickListener;

    public NewsFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                if (!loadedFromCache) {
                    Intent intent = new Intent(getContext(), RssWebView.class);
                    intent.putExtra("URL", item.getLink());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG);
                }
            }
        };
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String lastUserId = sharedPref.getString(getString(R.string.preference_last_userId), null);
        String rssUrl = sharedPref.getString(getString(R.string.preference_rssUrl), null);
        if (lastUserId == null){
            askToInputNewUrl("Welcome to RSS World!");
        } else if (!lastUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            CacheRepository.getInstance().removeCacheForUser(getContext(), lastUserId);
            askToInputNewUrl("Welcome to RSS World!");
        } else {
            doRss(rssUrl);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newsFragment__menu__change_source:
                showRssSourceInputDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doRss(String address) {
        feedsAdapter = new FeedsAdapter(getContext(), new ArrayList<FeedItem>(), onItemClickListener);
        recyclerView.setAdapter(feedsAdapter);
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            loadRssFromTheInternet(address);
        } else {
            loadRssFromCache();
        }
    }

    private void loadRssFromTheInternet(String address){
        rssReader = new RssReader(getContext(), address);
        rssReader.addOnFeedItemLoadedListener(this);
        rssReader.addOnExecutedListener(this);
        rssReader.addOnProgressListener(this);
        rssReader.execute();
    }

    private void loadRssFromCache() {
        loadedFromCache = true;
        ArrayList<FeedItem> items = CacheRepository.getInstance().readRssCache(getContext(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        feedsAdapter.setFeedItems(items);
        Toast.makeText(getContext(), "Loaded from cache", Toast.LENGTH_SHORT).show();
    }

    private void askToInputNewUrl(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setCancelable(false)
                .setMessage(R.string.rss_correct_url_request)
                .setTitle(title)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                showRssSourceInputDialog();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        builder.create().show();

    }

    private void showRssSourceInputDialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View dialogView = li.inflate(R.layout.rss_source_input_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext());

        builder.setView(dialogView);

        final EditText sourceInput = (EditText) dialogView
                .findViewById(R.id.rssSourseInputDialog__editText);

        builder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String url = sourceInput.getText().toString();
                                setRssUrlPreference(url);
                                doRss(url);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Error occured when loading item", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setLastUserUidPreference(String uid) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_last_userId), uid);
        editor.commit();
    }

    private void setRssUrlPreference(String url) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_rssUrl), url);
        editor.commit();
    }

    @Override
    public void onItemsLoaded() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Feed loaded", Toast.LENGTH_LONG).show();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                setLastUserUidPreference(uid);
                CacheRepository.getInstance().writeRssToCache(getContext(), feedsAdapter.getFeedItems(), uid);
            }
        });

    }

    @Override
    public void onItemsLoadFailed(Exception e) {
        if (e instanceof MalformedURLException) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    askToInputNewUrl("Incorrect RssFeed link");
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Loading failed", Toast.LENGTH_LONG).show();
                    loadRssFromCache();
                }
            });
        }
    }

    @Override
    public void onProgressStarted() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
        }
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
    }

    @Override
    public void onProgressEnded() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.news_fragment__menu, menu);
    }

}
