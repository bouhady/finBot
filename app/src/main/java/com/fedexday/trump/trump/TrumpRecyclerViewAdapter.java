package com.fedexday.trump.trump;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yb34982 on 26/04/2017.
 */

public class TrumpRecyclerViewAdapter extends RecyclerView.Adapter<TrumpRecyclerViewAdapter.CustomViewHolder> {
    private List<FeedItem> feedItemList;
    private Context mContext;

    public TrumpRecyclerViewAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bot_line_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);
        //Setting text view title
        int side = feedItem.getSide();
        customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));
        customViewHolder.textView.setBackgroundResource( (side==FeedItem.BOT) ? R.color.charcoal_grey : R.color.warm_grey);
        int white = ContextCompat.getColor(mContext, R.color.white);
        int charcoal = ContextCompat.getColor(mContext, R.color.charcoal_grey);
        customViewHolder.textView.setTextColor( (side==FeedItem.BOT) ? white : charcoal);

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.chat_item_textview);
        }
    }
}
