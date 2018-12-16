package com.example.mikita.ppo_lab.rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mikita.ppo_lab.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.MyViewHolder> {
    public ArrayList<FeedItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(ArrayList<FeedItem> feedItems) {
        this.feedItems = feedItems;
        this.notifyDataSetChanged();
    }

    protected ArrayList<FeedItem> feedItems;
    protected Context context;
    private OnItemClickListener onItemClickListener;

    public FeedsAdapter(Context context, ArrayList<FeedItem>feedItems, OnItemClickListener listener){
        this.feedItems=feedItems;
        this.context=context;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public FeedsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.news_list_item, parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedsAdapter.MyViewHolder holder, int position) {
        FeedItem item = feedItems.get(position);

        holder.Title.setText(item.getTitle());
        holder.Description.setText(item.getDescription());
        holder.Date.setText(item.getPubDate());
        Glide
                .with(context)
                .load(item.getThumbnailUrl())
                .into(holder.Thumbnail);
        holder.setOnClickListener(onItemClickListener, item);
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public void addItem(FeedItem item) {
        feedItems.add(item);
        this.notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title,Description,Date;
        ImageView Thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            Title= (TextView) itemView.findViewById(R.id.title_text);
            Description= (TextView) itemView.findViewById(R.id.description_text);
            Date= (TextView) itemView.findViewById(R.id.date_text);
            Thumbnail= (ImageView) itemView.findViewById(R.id.thumb_img);
        }

        public void setOnClickListener(final OnItemClickListener listener, final FeedItem item) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(FeedItem item);
    }
}
