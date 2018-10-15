package io.github.ggface.sydneyjourney.api

import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Предназначение
 *
 * @author Ivan Novikov on 2018-10-15.
 */
class Repository(private val mVenuesRemoteApi: VenuesRemoteApi) : RemoteRepository {
    private val mVenuesProcessor = BehaviorProcessor.create<List<Venue>>()

    //region RemoteRepository

    /**
     * {@inheritDoc}
     */
    override fun obtainVenues(): Completable {
        return mVenuesRemoteApi.venues
                .doOnSuccess { (venues) -> mVenuesProcessor.onNext(venues) }
                .doOnError { t -> Timber.tag("sys_rest").d("obtainVenues() -> %s", t) }
                .subscribeOn(Schedulers.io())
                .toCompletable()


    }

    override fun venues(): Flowable<List<Venue>> {
        return mVenuesProcessor.onBackpressureLatest()
    }
    //endregion RemoteRepository
}