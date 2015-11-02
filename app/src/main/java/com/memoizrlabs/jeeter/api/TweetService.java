package com.memoizrlabs.jeeter.api;

import com.memoizrlabs.jeeter.api.model.Tweet;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface TweetService {

    @GET("/1.1/statuses/home_timeline.json")
    Observable<List<Tweet>> getMainTimeline(@Query("count") int count);

    @POST("/1.1/statuses/update.json")
    Observable<Tweet> postUpdate(@Query("status") String status);

    @POST("/1.1/statuses/update.json")
    Observable<Tweet> postUpdate(@Query("status") String status, @Query("media_ids") String mediaIds);
}
