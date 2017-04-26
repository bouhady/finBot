package com.fedexday.trump.trump;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by yb34982 on 26/04/2017.
 */

public interface TrumpRetrofitService {
    @GET("api/bot")
    Observable<TrumpResponse> askTrump(
            @Query("input") String request
    );
}
