package com.location.anup.searchwiki;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

import model.PageModelData;

/**
 * Created by anup on 3/6/16.
 */
public class ImageResultAdapter extends RecyclerView.Adapter<ImageResultAdapter.ResultItemViewHolder> {
    private final ArrayList<PageModelData> mListOfItem;
    private Context mActivity;

    public ImageResultAdapter(ArrayList<PageModelData> pageItemList) {
        mListOfItem=pageItemList;
    }

    @Override
    public ResultItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mActivity=parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_search_item, parent, false);

        return new ResultItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ResultItemViewHolder holder, int position) {
        PageModelData pageModelData =mListOfItem.get(position);
        holder.title.setText(pageModelData.title);
        if(pageModelData.thumbnail!=null && pageModelData.thumbnail.source!=null){
            holder.mProgressBar.setVisibility(View.VISIBLE);
            Picasso.with(mActivity.getApplicationContext()).load(pageModelData.thumbnail.source).into(holder
                    .imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.imageView.invalidate();
                    holder.mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    holder.mProgressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mListOfItem.size();
    }

    public class ResultItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView imageView;
        private final ProgressBar mProgressBar;

        public ResultItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_tv);
            imageView =(ImageView) itemView.findViewById(R.id.image_view);
            mProgressBar=(ProgressBar)itemView.findViewById(R.id.image_progress_bar);
        }
    }
}
