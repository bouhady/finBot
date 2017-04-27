package com.fedexday.trump.trump;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Created by yb34982 on 26/04/2017.
 */

public class FabMoveAnimation {
    View fab;

    public FabMoveAnimation( View fab) {

        this.fab = fab;
    }

    public void moveAway(){
        final TranslateAnimation animation = new TranslateAnimation(0, 300, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(500);
        fab.startAnimation(animation);
        fab.setClickable(false);
    }
    public void moveBack(){
        final TranslateAnimation animation = new TranslateAnimation(300, 0, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(500);
        fab.startAnimation(animation);
        fab.setClickable(true);
    }
}
