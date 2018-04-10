package com.abdelrahman.bestmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by abdalrahman on 3/5/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.NumberViewHolder> {


    private final ListItemClickListener mOnClickListener;
    private final Context context;

    public MoviesAdapter(ListItemClickListener listener, Context c) {
        this.mOnClickListener = listener;
        this.context= c;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout,parent,false);
        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return MainActivity.moviesList.size();
    }

    public class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;

        public NumberViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_shown);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        public void bind(int position) {

            Picasso.with(context)
                    .load(JsonUtils.photoUrl(MainActivity.moviesList.get(position)))
                    .placeholder(R.drawable.fff)
                    .error(R.drawable.fff)
                    .into(imageView);
        }
    }
    public interface ListItemClickListener{
        void onListItemClick( int clickedItemIndex);
    }
}
