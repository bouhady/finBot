package com.fedexday.trump.trump;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yb34982 on 26/04/2017.
 */

public class TrumpRecyclerViewAdapter extends RecyclerView.Adapter<TrumpRecyclerViewAdapter.ChatItemViewHolder> {
    private List<FeedItem> feedItemList;
    private Context mContext;

    public TrumpRecyclerViewAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ChatItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bot_line_item, null);
        int width = viewGroup.getWidth();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.WRAP_CONTENT));
        ChatItemViewHolder viewHolder = new ChatItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatItemViewHolder chatItemViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);
        int side = feedItem.getSide();
        chatItemViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));
        chatItemViewHolder.textView.setBackgroundResource( (side==FeedItem.BOT)
                ? R.drawable.rounded_bubble_focused
                : R.drawable.rounded_bubble_white_focused);
        int white = ContextCompat.getColor(mContext, R.color.white);
        int charcoal = ContextCompat.getColor(mContext, R.color.charcoal_grey);
        chatItemViewHolder.textView.setTextColor( (side==FeedItem.BOT) ? white : charcoal);
        chatItemViewHolder.linearLayout.setGravity((side==FeedItem.BOT)
                ? Gravity.END
                : Gravity.START);

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class ChatItemViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected LinearLayout linearLayout;

        public ChatItemViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.chat_item_textview);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.list_item_linearlayout);
        }
    }
}
