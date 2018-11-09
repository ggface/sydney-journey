package io.github.ggface.sydneyjourney.api

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.ggface.sydneyjourney.api.database.LocalDataBase
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * @author Ivan Novikov on 2018-10-15.
 */
class Repository(context: Context,
                 private val venuesRemoteApi: VenuesRemoteApi) : RemoteRepository {

    private val mVenuesProcessor = BehaviorProcessor.create<List<Venue>>()
    private val mLocationProcessor = BehaviorProcessor.create<Location>()
    private val mLocalDatabase = Room.databaseBuilder(context,
            LocalDataBase::class.java, "venues_db")
            .build()
    private val mLocationManager = context
            .getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (null != location) {
                mLocationProcessor.onNext(location)
                disableGeoUpdates()
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    //region RemoteRepository

    /**
     * {@inheritDoc}
     */
    override fun obtainVenues(): Completable {
        return Flowable.combineLatest(getRemoteVenues().toFlowable(),
                getDatabaseVenues().toFlowable(),
                BiFunction<List<Venue>, List<Venue>, List<Venue>> { t1, t2 -> splitVenues(t1, t2) })
                .firstOrError()
                .doOnSuccess { mVenuesProcessor.onNext(it) }
                .doOnError { t -> Timber.tag("sys_rest").d("loadVenues() -> %s", t) }
                .subscribeOn(Schedulers.io())
                .toCompletable()
    }

    override fun venues(): Flowable<List<Venue>> {
        return mVenuesProcessor.onBackpressureLatest()
    }

    override fun createVenue(venue: Venue): Completable {
        return Completable.create {
            try {
                mLocalDatabase.venueDAO().insert(venue)
                it.onComplete()
            } catch (t: Throwable) {
                it.onError(t)
            }
        }.doOnComplete { obtainVenues().subscribe({}, {}) }
                .subscribeOn(Schedulers.io())
    }

    override fun updateVenue(venue: Venue): Completable {
        return Completable.create {
            try {
                mLocalDatabase.venueDAO().update(venue)
                it.onComplete()
            } catch (t: Throwable) {
                it.onError(t)
            }
        }.doOnComplete { obtainVenues().subscribe({}, {}) }
                .subscribeOn(Schedulers.io())
    }

    override fun deleteVenue(venue: Venue): Completable {
        return Completable.create {
            try {
                mLocalDatabase.venueDAO().delete(venue)
                it.onComplete()
            } catch (t: Throwable) {
                it.onError(t)
            }
        }.doOnComplete { obtainVenues().subscribe({}, {}) }
                .subscribeOn(Schedulers.io())
    }

    override fun gpsIsEnabled(): Boolean {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun disableGeoUpdates() {
        mLocationManager.removeUpdates(mLocationListener)
    }

    @SuppressLint("MissingPermission")
    override fun lastKnownLocation() {
        val matchingProviders = mLocationManager.allProviders
        var lastLocation: Location? = null
        for (provider in matchingProviders) {
            val location = mLocationManager.getLastKnownLocation(provider)
            if (null != location) {
                lastLocation = location
                break
            }
        }

        if (null != lastLocation) {
            mLocationProcessor.onNext(lastLocation)
        } else {
            singleLocationUpdate()
        }
    }

    override fun location(): Flowable<Location> {
        return mLocationProcessor.onBackpressureLatest()
    }
    //endregion RemoteRepository

    private fun getRemoteVenues(): Single<List<Venue>> {
        return venuesRemoteApi.venues.map { it.venues }
    }

    private fun getDatabaseVenues(): Single<List<Venue>> {
        return Single.create<List<Venue>> { it.onSuccess(mLocalDatabase.venueDAO().all) }
    }

    private fun splitVenues(remoteVenues: List<Venue>, databaseVenues: List<Venue>): List<Venue> {
        val newList = ArrayList<Venue>()
        newList.addAll(databaseVenues)

        val unchangedVenues = ArrayList<Venue>()
        for (venue in remoteVenues) {
            if (databaseVenues.find { it.name == venue.name } == null) {
                unchangedVenues.add(venue)
            }
        }
        newList.addAll(unchangedVenues)
        return newList
    }

    @SuppressLint("MissingPermission")
    private fun singleLocationUpdate() {
        var lp: LocationProvider? = mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER)
        if (null != lp) {
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null)
        }
        lp = mLocationManager.getProvider(LocationManager.GPS_PROVIDER)
        if (null != lp) {
            mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null)
        }
        lp = mLocationManager.getProvider(LocationManager.PASSIVE_PROVIDER)
        if (null != lp) {
            mLocationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, mLocationListener, null)
        }
    }
}