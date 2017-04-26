package com.fedexday.trump.trump;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yb34982 on 26/04/2017.
 */

class TrumpPresenterImpl implements TrumpMVP.Presenter {
    private static final String TAG = "Trump Presenter";
    TrumpMVP.View trumpView;
    TrumpRetrofitService trumpRetrofitService;


    @Override
    public void setView(TrumpMVP.View view) {
        trumpView = view;
    }

    @Override
    public void setRetrofitService(TrumpRetrofitService trumpRetrofitService) {
        this.trumpRetrofitService = trumpRetrofitService;
    }

    @Override
    public void listenVoice(Observable<CharSequence> editTextObservable) {
        editTextObservable
                .debounce(1000, TimeUnit.MILLISECONDS)
                .onErrorReturn(throwable -> {
                    Log.e(TAG, "Error223", throwable);
                    return "";
                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    trumpView.resetEditText();
                    trumpRetrofitService.askTrump(charSequence.toString())
                            .doOnSubscribe(() -> Log.d(TAG, "Subscribe!"))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorReturn(throwable -> new TrumpResponse("Error"))
                            .subscribe(trumpResponse -> {
                                Log.d(TAG, trumpResponse.getResponseText());
//                                trumpView.resetEditText();
                                trumpView.appendToRecyclerView(trumpResponse.getResponseText(), FeedItem.BOT);
                                trumpView.ttsSpeak(trumpResponse.getResponseText());
                            });
                });
    }
}
