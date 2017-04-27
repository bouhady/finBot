package com.fedexday.trump.trump;

import android.util.Log;

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
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(charSequence -> {
                    trumpView.resetEditText();
                    trumpView.progressBarVisability(true);
                })
                .observeOn(Schedulers.io())
                .subscribe(charSequence -> {
                    if (charSequence.toString().equals("bye bye"))
                    {
                        trumpView.progressBarVisability(false);
                        trumpView.appendToRecyclerView(charSequence.toString(), FeedItem.BOT);
                        trumpView.ttsSpeak(charSequence.toString());
                        trumpView.exit();
                    } else
                    trumpRetrofitService.askTrump(charSequence.toString())
                            .doOnSubscribe(() -> Log.d(TAG, "Subscribe!"))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorReturn(throwable -> new TrumpResponse("Error"))
                            .subscribe(trumpResponse -> {
                                Log.d(TAG, trumpResponse.getResponseText());
                                trumpView.progressBarVisability(false);
                                trumpView.appendToRecyclerView(trumpResponse.getResponseText(), FeedItem.BOT);
                                trumpView.ttsSpeak(trumpResponse.getResponseText());
                            });
                });
    }
}
