package io.github.ggface.sydneyjourney.api

import android.arch.persistence.room.Room
import android.content.Context
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

    private val localDatabase = Room.databaseBuilder(context,
            LocalDataBase::class.java, "venues_db")
            .allowMainThreadQueries()
            .build()
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
                localDatabase.venueDAO().insert(venue)
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
                localDatabase.venueDAO().update(venue)
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
                localDatabase.venueDAO().delete(venue)
                it.onComplete()
            } catch (t: Throwable) {
                it.onError(t)
            }
        }.doOnComplete { obtainVenues().subscribe({}, {}) }
                .subscribeOn(Schedulers.io())
    }
    //endregion RemoteRepository

    private fun getRemoteVenues(): Single<List<Venue>> {
        return venuesRemoteApi.venues.map { it.venues }
    }

    private fun getDatabaseVenues(): Single<List<Venue>> {
        return Single.create<List<Venue>> { it.onSuccess(localDatabase.venueDAO().all) }
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
}