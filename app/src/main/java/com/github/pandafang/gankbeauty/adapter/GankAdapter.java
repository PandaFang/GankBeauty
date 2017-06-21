package com.github.pandafang.gankbeauty.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

//            Log.i(TAG, "onBindViewHolder: postion" + position);

        Glide.with(mContext).load(mDatas.get(position).getUrl()).placeholder(R.drawable.image_loading)
                .centerCrop()
                .thumbnail(0.2f)
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
