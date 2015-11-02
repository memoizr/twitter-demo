package com.memoizrlabs.jeeter.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Value;

@Value
public class Tweet implements Serializable {

    @SerializedName("entities")
    private final TweetEntities entities;

    @SerializedName("created_at")
    private final String createdAt;

    @SerializedName("text")
    private final String text;

    @SerializedName("user")
    private final User user;
}
