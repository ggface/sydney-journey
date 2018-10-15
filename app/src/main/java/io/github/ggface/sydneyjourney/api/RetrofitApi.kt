package io.github.ggface.sydneyjourney.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.ggface.sydneyjourney.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Предназначение
 *
 * @author Ivan Novikov on 2018-10-15.
 */
class RetrofitApi {

    val mGson: Gson
    val venuesRemoteApi: VenuesRemoteApi

    init {
        mGson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()

        val logger = HttpLoggingInterceptor()
                .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)

        val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

        venuesRemoteApi = Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.LOCATION_HOST)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(VenuesRemoteApi::class.java)
    }
}