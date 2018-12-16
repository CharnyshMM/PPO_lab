package com.example.mikita.ppo_lab.rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;

import com.example.mikita.ppo_lab.storage.OnProgressListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RssReader extends AsyncTask<Void, Void, Void>  {

    protected String address;
    protected ArrayList<FeedItem> feedItems;
    protected URL url;

    public RssReader(Context context,  String address) {
        this.address = address;
        feedItems = new ArrayList<FeedItem>();
        onItemsLoadedListeners = new ArrayList<>();
        onFeedItemLoadedListeners = new ArrayList<>();
        onProgressListeners = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        notifyOnProgressStarted();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        notifyOnProgressEnded();
    }



    public interface OnItemsLoadedListener {
        void onItemsLoaded();
        void onItemsLoadFailed(Exception e);
    }
    private ArrayList<OnProgressListener> onProgressListeners;
    public void addOnProgressListener(OnProgressListener listener) {
        if (!onProgressListeners.contains(listener)) {
            onProgressListeners.add(listener);
        }
    }

    public void removeOnProgressListener(OnProgressListener listener) {
        onProgressListeners.remove(listener);
    }

    public void notifyOnProgressStarted() {
        for (OnProgressListener listener:onProgressListeners) {
            listener.onProgressStarted();
        }
    }

    public void notifyOnProgressEnded() {
        for (OnProgressListener listener:onProgressListeners) {
            listener.onProgressEnded();
        }
    }


    private ArrayList<OnItemsLoadedListener> onItemsLoadedListeners;

    public void addOnExecutedListener(OnItemsLoadedListener listener) {
        if (!onItemsLoadedListeners.contains(listener)) {
            onItemsLoadedListeners.add(listener);
        }
    }

    public void removeOnExecutedListener(OnItemsLoadedListener listener) {
        onItemsLoadedListeners.remove(listener);
    }

    private void notifyOnItemsLoaded() {
        for (OnItemsLoadedListener listener: onItemsLoadedListeners) {
            listener.onItemsLoaded();
        }
    }

    private void notifyOnItemsLoadFailed(Exception e) {

        for (OnItemsLoadedListener listener: onItemsLoadedListeners) {
            listener.onItemsLoadFailed(e);
        }
    }

    public interface OnFeedItemLoadedListener {
        void onFeedItemLoaded(FeedItem item);
        void onFeedItemLoadFailed(Exception e);
    }

    private ArrayList<OnFeedItemLoadedListener> onFeedItemLoadedListeners;
    public void addOnFeedItemLoadedListener(OnFeedItemLoadedListener listener) {
        if (!onFeedItemLoadedListeners.contains(listener)) {
            onFeedItemLoadedListeners.add(listener);
        }
    }

    public void removeOnFeedItemLoadedListener(OnFeedItemLoadedListener listener) {
        onFeedItemLoadedListeners.remove(listener);
    }

    private void notifyOnFeedItemLoaded(FeedItem item) {

        for (OnFeedItemLoadedListener listener: onFeedItemLoadedListeners) {
            listener.onFeedItemLoaded(item);
        }
    }

    private void notifyOnFeedItemLoadFailed(Exception e) {
        for (OnFeedItemLoadedListener listener: onFeedItemLoadedListeners) {
            listener.onFeedItemLoadFailed(e);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        processXml(getData());
        return null;
    }

    private void processXml(Document data) {
        if (data == null){
            return;
        }
        Element root = data.getDocumentElement();
        Node channel = root.getChildNodes().item(1);
        NodeList items = channel.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            if (item.getNodeName().equalsIgnoreCase("item")) {
                FeedItem feedItem = new FeedItem();
                NodeList itemChildNodes = item.getChildNodes();
                for (int j = 0; j < itemChildNodes.getLength(); j++) {
                    Node node = itemChildNodes.item(j);
                    if (node.getNodeName().equalsIgnoreCase("title")) {
                        feedItem.setTitle(node.getTextContent());
                    } else if (node.getNodeName().equalsIgnoreCase("pubDate")) {
                        feedItem.setPubDate(node.getTextContent());
                    } else if (node.getNodeName().equalsIgnoreCase("link")) {
                        feedItem.setLink(node.getTextContent());
                    } else if (node.getNodeName().equalsIgnoreCase("media:thumbnail")
                            || node.getNodeName().equalsIgnoreCase("media:content")) {
                        String url = node.getAttributes().item(0).getTextContent();
                        feedItem.setThumbnailUrl(url);
                    }else if (node.getNodeName().equalsIgnoreCase("description")) {
                        feedItem.setDescription(node.getTextContent());
                    }
                }
                feedItems.add(feedItem);
                notifyOnFeedItemLoaded(feedItem);
            }
        }
        notifyOnItemsLoaded();
    }

    public Document getData() {
        try {
            url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            InputStream inputStream = connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(inputStream);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            notifyOnItemsLoadFailed(e);
            return null;

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            notifyOnItemsLoadFailed(e);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            notifyOnItemsLoadFailed(e);
            return null;
        }
    }
}
