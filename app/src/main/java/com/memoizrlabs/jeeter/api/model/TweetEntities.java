package com.memoizrlabs.jeeter.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Value;

@Value
public class TweetEntities implements Serializable {

    @SerializedName("media")
    private final List<MediaEntity> media;
}
