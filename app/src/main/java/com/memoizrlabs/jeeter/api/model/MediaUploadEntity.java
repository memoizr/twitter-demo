package com.memoizrlabs.jeeter.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Value;

@Value
public class MediaUploadEntity implements Serializable {

    @SerializedName("media_id")
    private final long mediaId;

    @SerializedName("media_id_string")
    private final String mediaIdString;

    @SerializedName("size")
    private final long size;
}
