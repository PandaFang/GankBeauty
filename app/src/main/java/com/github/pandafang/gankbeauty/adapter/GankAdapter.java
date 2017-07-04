package com.github.pandafang.gankbeauty.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.pandafang.gankbeauty.ImageActivity;
import com.github.pandafang.gankbeauty.R;
import com.github.pandafang.gankbeauty.model.Girl;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by panda on 2017/6/21.
 */

public class GankAdapter extends RecyclerView.Adapter<GankAdapter.ImageViewHolder> {

    private static final String TAG = "GankAdapter";

    private LayoutInflater mLayoutInflater;

    private List<Girl> mDatas;

    private Context mContext;

    public GankAdapter(Context context , List<Girl> datas)
    {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        if (datas == null) {
            mDatas = new LinkedList<Girl>();
            return ;
        }
        mDatas = datas;
    }


    public void setDatas(List<Girl> datas) {

        if (mDatas != null) {
            mDatas.clear();

            mDatas.addAll(datas);
            notifyDataSetChanged();
            return;
        }
        mDatas = datas;
        notifyDataSetChanged();

    }

    public void addDatas(List<Girl> datas)
    {
        mDatas.addAll(datas);
        Log.i(TAG, "addDatas: " + mDatas.hashCode());
        notifyDataSetChanged();

    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =  mLayoutInflater.inflate(R.layout.item_layout, parent, false);
        final ImageViewHolder holder = new ImageViewHolder(view);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String)v.getTag(R.id.recycler_item);
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra(ImageActivity.EXTRA_URL, url);
                mContext.startActivity(intent);
            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

//            Log.i(TAG, "onBindViewHolder: postion" + position);

        String url = mDatas.get(position).getUrl();
        holder.iv.setTag(R.id.recycler_item, url);


        Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.image_loading)
                .centerCrop()
                .thumbnail(0.1f)
                .into(holder.iv);




    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ImageViewHolder extends  RecyclerView.ViewHolder {

        ImageView iv;


        public ImageViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.recycler_item);
        }
    }
}
