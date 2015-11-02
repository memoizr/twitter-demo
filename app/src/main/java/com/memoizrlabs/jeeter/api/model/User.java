package com.memoizrlabs.jeeter.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Value;

@Value
public class User implements Serializable {

    @SerializedName("name")
    private final String name;

    @SerializedName("profile_image_url")
    private final String profileImageUrl;

    @SerializedName("screen_name")
    private final String screenName;
}
