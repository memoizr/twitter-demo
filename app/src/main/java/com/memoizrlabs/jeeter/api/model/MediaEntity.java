package com.memoizrlabs.jeeter.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Value;

@Value
public class MediaEntity implements Serializable {

    @SerializedName("id")
    private final long id;

    @SerializedName("id_str")
    private final String idStr;

    @SerializedName("media_url")
    private final String mediaUrl;

    @SerializedName("media_url_https")
    private final String mediaUrlHttps;

    @SerializedName("source_status_id")
    private final long sourceStatusId;

    @SerializedName("source_status_id_str")
    private final String sourceStatusIdStr;

    @SerializedName("type")
    private final String type;
}
