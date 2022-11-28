package com.example.termproject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder>{
    private ArrayList<Uri> mData = null;
    private Context mContext = null;
    private int from_activity;

    public MultiImageAdapter(ArrayList<Uri> uriList, Context applicationContext, int from_activity) {
        mData = uriList;
        mContext = applicationContext;
        this.from_activity = from_activity;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageItem);
            if(from_activity == 1) {
                imageview.setOnClickListener(e -> {
                    if(getAdapterPosition() != -1){
                        mData.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mData.size());
                    }
                });
            }
        }
    }
    @NonNull
    @Override
    public MultiImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.multi_img_item,parent,false);
        MultiImageAdapter.ViewHolder viewHolder = new MultiImageAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri image_uri = mData.get(position);

        Glide.with(mContext)
                .load(image_uri)
                .into(holder.imageview);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
