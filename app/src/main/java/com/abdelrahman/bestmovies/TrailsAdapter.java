package com.abdelrahman.bestmovies;

import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abdalrahman on 4/9/2018.
 */

public class TrailsAdapter extends RecyclerView.Adapter<TrailsAdapter.TrailsViewHolder> {

    final private ListItemClickListener mOnClickListener;
    private List<String> mydata;

    public TrailsAdapter(List<String> myData,ListItemClickListener mOnClickListener) {
        this.mydata = myData;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public TrailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trails_layout, parent, false);
        return new TrailsAdapter.TrailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mydata.size();
    }

    class TrailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        LinearLayout linearLayout;

        public TrailsViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.trails_text);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.trails_layout);

            linearLayout.setOnClickListener(this);
        }

        public void bind(int position) {
            textView.setText("Trail " + (position+1));
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(mydata.get(getAdapterPosition()));
        }
    }
    public interface ListItemClickListener{void onListItemClick( String uri);}

}
