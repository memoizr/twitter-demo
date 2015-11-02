package com.memoizrlabs.jeeter.api;

import com.memoizrlabs.jeeter.api.model.MediaUploadEntity;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

public interface MediaUploadService {

    @POST("/1.1/media/upload.json")
    Observable<MediaUploadEntity> uploadVideoInit(@Query("command") String command,
                                                  @Query("media_type") String mediaType,
                                                  @Query("total_bytes") String totalBytes);

    @Multipart
    @POST("/1.1/media/upload.json")
    Observable<Object> uploadVideoAppend(@Part("command") String command,
                                         @Part("media_id") String mediaId,
                                         @Part("media") TypedFile media,
                                         @Part("segment_index") int segmentIndex);

    @POST("/1.1/media/upload.json")
    Observable<MediaUploadEntity> uploadVideoFinalize(@Query("command") String command,
                                                      @Query("media_id") String mediaId);

    final class Command {

        public static final String INIT = "INIT";
        public static final String APPEND = "APPEND";
        public static final String FINALIZE = "FINALIZE";

        private Command() {
        }
    }
}
