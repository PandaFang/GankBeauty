package com.github.pandafang.gankbeauty.api;

import com.github.pandafang.gankbeauty.model.GankResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by panda on 2017/6/21.
 */

public interface GirlService {

    @GET("api/data/福利/{pageSize}/{pageNum}")
    Call<GankResponse> getGirls(@Path("pageSize") int pageSize, @Path("pageNum") int pageNum);
}
