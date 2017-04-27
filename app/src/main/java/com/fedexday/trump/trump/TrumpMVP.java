package com.fedexday.trump.trump;

import android.widget.EditText;

import rx.Observable;

/**
 * Created by yb34982 on 26/04/2017.
 */

public interface TrumpMVP {
    public interface View{
        Observable getMainUserObservable(EditText editText);
        void appendToRecyclerView(String line , int side);
        void resetEditText();
        public void ttsSpeak(String line);
        void progressBarVisability(boolean visible);
        void exit();
    }

    public interface Presenter{
        void setView(View view);
        void setRetrofitService(TrumpRetrofitService trumpRetrofitService);
        void listenVoice(Observable<CharSequence> editTextObservable);

    }
}
