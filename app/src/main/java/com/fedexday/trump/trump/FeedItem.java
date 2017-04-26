package com.fedexday.trump.trump;

/**
 * Created by yb34982 on 26/04/2017.
 */

public class FeedItem {
    public static final int BOT = 1;
    public static final int USER = 2;


    private String title;
    private int side;


    public FeedItem(String title, int side) {
        this.title = title;
        this.side = side;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}