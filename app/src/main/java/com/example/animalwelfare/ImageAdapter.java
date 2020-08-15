package com.example.animalwelfare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.PostsViewHolder> {

    private Context mContext;
    private List<Posts> mPosts;

    public ImageAdapter(Context context, List<Posts> posts)
    {
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cards_feed, viewGroup, false);

        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i) {
        Posts postCurrent = mPosts.get(i);
        postsViewHolder.cards_username.setText(postCurrent.getUsername());
        postsViewHolder.cards_date.setText(postCurrent.getDate());
        postsViewHolder.cards_issue.setText(postCurrent.getIssue());
        postsViewHolder.cards_location.setText(postCurrent.getLocation());
        postsViewHolder.cards_landmark.setText(postCurrent.getLandmark());
        postsViewHolder.cards_description.setText(postCurrent.getDescription());

        Picasso.with(mContext)
                .load(postCurrent.getPostimage())
                .fit()
                .centerCrop()
                .into(postsViewHolder.cards_image);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        TextView cards_username;
        TextView cards_date;
        ImageView cards_image;
        TextView cards_issue;
        TextView cards_location;
        TextView cards_landmark;
        TextView cards_description;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            cards_username = (TextView) mView.findViewById(R.id.cards_username);
            cards_date = (TextView) mView.findViewById(R.id.cards_date);
            cards_image = (ImageView) mView.findViewById(R.id.cards_imageview);
            cards_issue = (TextView) mView.findViewById(R.id.cards_issue2);
            cards_location = (TextView) mView.findViewById(R.id.cards_location2);
            cards_landmark = (TextView) mView.findViewById(R.id.cards_landmark2);
            cards_description = (TextView) mView.findViewById(R.id.cards_description2);
        }
    }

}
