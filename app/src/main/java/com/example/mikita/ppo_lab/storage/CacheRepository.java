package com.example.mikita.ppo_lab.storage;

import android.content.Context;
import android.net.Uri;

import com.example.mikita.ppo_lab.rss.FeedItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CacheRepository {
    private static CacheRepository instance;

    private CacheRepository() {

    }

    public static CacheRepository getInstance() {
        if (instance == null) {
            instance = new CacheRepository();
        }
        return instance;
    }

    public void writeRssToCache(Context context, ArrayList<FeedItem> feedItems, String userUid) {
        File cacheFile = getTempFile(context, userUid);

        try {
            FileOutputStream f = new FileOutputStream(cacheFile);
            ObjectOutputStream o = new ObjectOutputStream(f);

            for (FeedItem item: feedItems) {
                o.writeObject(item);
            }
            o.close();
            f.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<FeedItem> readRssCache(Context context, String userUid){
        ArrayList<FeedItem> feedItems = new ArrayList<>();
        File cacheFile = getTempFile(context, userUid);
        try {
            FileInputStream fi = new FileInputStream(cacheFile);
            ObjectInputStream oi = new ObjectInputStream(fi);

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }
//        } catch (ClassNotFoundException e) {
//
//            e.printStackTrace();
//        }
        return feedItems;
    }

    private File getTempFile(Context context, String fileName) {
        File file = null;
        File cacheDir = context.getCacheDir();
        File cacheFile = new File(context.getCacheDir(), fileName);
        if (cacheFile.exists()) {
            return  cacheFile;
        }
        try {
            //TODO: clear cache
            cacheFile.createNewFile();
        } catch (IOException e) {
            // Error while creating file
        }
        return cacheFile;
    }
}
