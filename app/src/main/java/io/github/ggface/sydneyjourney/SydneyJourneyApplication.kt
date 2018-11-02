package io.github.ggface.sydneyjourney

import android.app.Application
import android.os.StrictMode
import io.github.ggface.sydneyjourney.api.RemoteRepository
import io.github.ggface.sydneyjourney.api.Repository
import io.github.ggface.sydneyjourney.api.RetrofitApi
import timber.log.Timber

/**
 * @author Ivan Novikov on 2018-10-15.
 */
class SydneyJourneyApplication : Application() {

    private lateinit var mRetrofitApi: RetrofitApi
    lateinit var remoteRepository: RemoteRepository private set

    override fun onCreate() {
        super.onCreate()
        initTimber()

//        Single.just(this).observeOn(Schedulers.io())
//                .subscribe( {

                    mRetrofitApi = RetrofitApi()
                    remoteRepository = Repository(this, mRetrofitApi.venuesRemoteApi)
//                }, {t -> Timber.tag("").d(t.message) })

    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
    }
}