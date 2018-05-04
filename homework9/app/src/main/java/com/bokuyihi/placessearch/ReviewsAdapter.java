package com.bokuyihi.placessearch;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bokuyihi on 20/04/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<ReviewItem> allReviews;
    private Context context;

    public ReviewsAdapter(List<ReviewItem> allReviews, Context context) {
        this.allReviews = allReviews;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ReviewItem reviewItem = allReviews.get(position);

        holder.reviewerName.setText(reviewItem.getReviewerName());
        holder.reviewerTime.setText(reviewItem.getReviewerTime());
        holder.reviewerText.setText(reviewItem.getReviewerText());
        holder.reviewerRating.setText(reviewItem.getReviewerRating());

        Picasso.get().load(reviewItem.getReviewerPhoto()).into(holder.reviewerPhoto);

        holder.reviewerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = reviewItem.getLink();

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return allReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView reviewerPhoto;
        public TextView reviewerName;
        public TextView reviewerTime;
        public TextView reviewerText;
        public TextView reviewerRating;
        public LinearLayout reviewerItem;


        public ViewHolder(View itemView) {
            super(itemView);
            reviewerName = (TextView)itemView.findViewById(R.id.reviewerName);
            reviewerRating = (TextView)itemView.findViewById(R.id.reviewerRating);
            reviewerTime = (TextView)itemView.findViewById(R.id.reviewerTime);
            reviewerText = (TextView)itemView.findViewById(R.id.reviewerText);
            reviewerPhoto = (ImageView)itemView.findViewById(R.id.reviewerPhoto);
            reviewerItem = (LinearLayout)itemView.findViewById(R.id.reviewItem);
        }
    }
}
