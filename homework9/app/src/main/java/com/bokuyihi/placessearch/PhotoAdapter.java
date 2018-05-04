package com.bokuyihi.placessearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by bokuyihi on 22/04/2018.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<Bitmap> allPhotos;
    private Context context;

    public PhotoAdapter(List<Bitmap> allPhotos, Context context) {
        this.allPhotos = allPhotos;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.photo.setImageBitmap(allPhotos.get(position));
    }

    @Override
    public int getItemCount() {
        return allPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);

            photo = (ImageView)itemView.findViewById(R.id.photo_item);
        }
    }
}
