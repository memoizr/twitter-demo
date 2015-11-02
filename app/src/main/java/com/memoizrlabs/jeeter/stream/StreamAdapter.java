package com.memoizrlabs.jeeter.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.api.model.MediaEntity;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.api.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

final class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.ViewHolder> {

    private final List<Tweet> tweetItems = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tweet tweet = tweetItems.get(position);
        final User user = tweet.getUser();

        displayProfileImage(holder.profileImageView, user.getProfileImageUrl());
        displayMediaImageIfPresent(holder.contentImageView, tweet);

        holder.userNameTextView.setText(user.getName());
        holder.dateTextView.setText(DateFormatter.parseDate(tweet.getCreatedAt()));
        holder.screenNameTextView.setText(getTwitterHandle(user.getScreenName()));
        holder.tweetTextView.setText(tweet.getText());
    }

    private static void displayProfileImage(@NonNull ImageView profileImageView,
                                            @NonNull String profileImageUrl) {
        Picasso.with(profileImageView.getContext())
               .load(profileImageUrl)
               .into(profileImageView);
    }

    private static void displayMediaImageIfPresent(@NonNull ImageView contentImageView,
                                                   @NonNull Tweet tweet) {
        Picasso.with(contentImageView.getContext())
               .load(getMediaUrl(tweet))
               .into(contentImageView);
    }

    @NonNull
    private static String getTwitterHandle(@NonNull String screenName) {
        return "@" + screenName;
    }

    @Nullable
    private static String getMediaUrl(@NonNull Tweet tweet) {
        final List<MediaEntity> media = tweet.getEntities()
                                             .getMedia();
        String url = null;
        if (media != null) {
            if (media.size() > 0) {
                url = media.get(0)
                           .getMediaUrl();
            }
        }
        return url;
    }

    @Override
    public int getItemCount() {
        return tweetItems.size();
    }

    public void setTweetItems(@NonNull List<Tweet> tweets) {
        tweetItems.clear();
        tweetItems.addAll(tweets);
        notifyDataSetChanged();
    }

    void addItem(@NonNull Tweet tweet) {
        tweetItems.add(0, tweet);
        notifyItemInserted(0);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImageView = (ImageView) itemView.findViewById(R.id.imageview_profile_image);
        private final ImageView contentImageView = (ImageView) itemView.findViewById(R.id.imageview_content_image);
        private final TextView userNameTextView = (TextView) itemView.findViewById(R.id.textview_username);
        private final TextView screenNameTextView = (TextView) itemView.findViewById(R.id.textview_screen_name);
        private final TextView dateTextView = (TextView) itemView.findViewById(R.id.textview_date);
        private final TextView tweetTextView = (TextView) itemView.findViewById(R.id.textinputlayout_tweet_content);

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
