package com.abdelrahman.bestmovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abdalrahman on 4/8/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
    private List<Review> mydata;
    public ReviewsAdapter(List<Review> myData) {
        this.mydata = myData;
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("ReviewsAdapter","onCreate");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mydata.size();
    }

    class ReviewsViewHolder extends RecyclerView.ViewHolder{

        TextView autherName;
        TextView autherReview;

        public ReviewsViewHolder(View itemView) {
            super(itemView);

            autherName   = (TextView) itemView.findViewById(R.id.auther_name);
            autherReview = (TextView) itemView.findViewById(R.id.auther_review);
        }

        public void bind(int position){

            autherName.setText(mydata.get(position).getName());
            autherReview.setText(mydata.get(position).getDescription());
        }
    }
}
