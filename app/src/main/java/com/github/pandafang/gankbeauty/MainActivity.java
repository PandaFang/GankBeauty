package com.github.pandafang.gankbeauty;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.pandafang.gankbeauty.adapter.GankAdapter;
import com.github.pandafang.gankbeauty.api.GirlService;
import com.github.pandafang.gankbeauty.model.GankResponse;
import com.github.pandafang.gankbeauty.model.Girl;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    SwipeRefreshLayout mSwipeLayout;
    RecyclerView mRecycler;

    GankAdapter myAdapter;

    GirlService girlService;

    public static final int PAGE_SIZE = 20;
    int pageNum = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);

        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 2);

        mRecycler.setLayoutManager(layout);

        final List<Girl> datas = new ArrayList<Girl>();

        myAdapter   = new GankAdapter(this, null);
        mRecycler.setAdapter(myAdapter);

        mSwipeLayout.setOnRefreshListener(refreshListener);

        mRecycler.addOnScrollListener(onScrollListener);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/")
               .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        girlService = retrofit.create(GirlService.class);


        getGirls(PAGE_SIZE , 1);


    }


    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {


            if (!mSwipeLayout.isRefreshing()) {
                Log.i(TAG, "onRefresh: done ");
            }
            else {
                getGirls(PAGE_SIZE , ++pageNum);

                Log.i(TAG, "onRefresh: ing");
            }

        }
    };

    int lastpos = 0;
    int scrolledX;
    int scrolledY;
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                scrolledX = (int) recyclerView.getScrollX();
                scrolledY = (int) recyclerView.getScrollY();


//                 lastpos = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int count = ((GridLayoutManager) recyclerView.getLayoutManager()).getItemCount();
                int count2 = recyclerView.getChildCount();
                Log.i(TAG, "onScrollStateChanged: " + lastpos + " itemcount" + count + "  纯" + count2);


                if (lastpos + 2 >= count) {
                    getGirls(PAGE_SIZE, ++pageNum);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            lastpos = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }

    };

    private void getGirls(int pageSize, final int pageNum) {
        Call<GankResponse> call = girlService.getGirls(pageSize, pageNum);
        // 用法和OkHttp的call如出一辙,
        // 不同的是如果是Android系统回调方法执行在主线程
        call.enqueue(new Callback<GankResponse>() {
            @Override
            public void onResponse(Call<GankResponse> call, Response<GankResponse> response) {
                try {
                    Log.i(TAG, response.body().toString());
                    if (pageNum == 1) {
                        myAdapter.setDatas(response.body().getResults());
                    } else {
                        myAdapter.addDatas(response.body().getResults());
                    }
                    mSwipeLayout.setRefreshing(false);
                    Log.i(TAG, "onResponse:thread name:" + Thread.currentThread().getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GankResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


}
