package com.abhi.android.grpc.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by abhi on 4/3/18.
 */

public interface RetrofitApiNodes {

    @GET
    Observable<ResponseBody> zipDownload(@Url String url);
}
