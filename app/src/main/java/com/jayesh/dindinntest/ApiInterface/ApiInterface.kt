package com.jayesh.dindinntest.ApiInterface

import com.jayesh.dindinntest.incredientsModels.IncredientsMenuWrapper
import com.jayesh.dindinntest.incredientsModels.IncredientsWrapper
import com.jayesh.dindinntest.orderModels.OrderWrapper
import com.jayesh.dindinntest.utils.OConstants
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("/orders")
    fun getOrders() : Observable<OrderWrapper>

    @GET("/incredients")
    fun getIncredients(@Query("category_id") query: String?) : Observable<IncredientsMenuWrapper>

    companion object {
        fun create() : ApiInterface {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(OConstants.MAIN_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)

        }
    }
}