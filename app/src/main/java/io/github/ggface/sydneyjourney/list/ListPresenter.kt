package io.github.ggface.sydneyjourney.list

import android.location.Location
import io.github.ggface.sydneyjourney.VenueSorting
import io.github.ggface.sydneyjourney.api.RemoteRepository
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.list.sorting.DistanceComparator
import io.github.ggface.sydneyjourney.list.sorting.NameComparator
import io.github.ggface.sydneyjourney.list.sorting.NameReverseComparator
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor

/**
 * Presenter of Venues List Screen
 *
 * @author Ivan Novikov on 2018-10-24.
 */
class ListPresenter(private val view: ListContract.View,
                    private val remoteRepository: RemoteRepository,
                    defaultSorting: VenueSorting) : ListContract.Presenter {

    private val mCompositeDisposable = CompositeDisposable()
    private val mSortingProcessor = BehaviorProcessor.createDefault(defaultSorting)
    private val mLocationProcessor = BehaviorProcessor.create<Location>()

    override fun loadVenues() {
        view.onLoadingChanged(true)
        mCompositeDisposable.add(remoteRepository.obtainVenues()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view.onLoadingChanged(false) },
                        { view.onLoadingChanged(false) }))
    }

    override fun sortBy(sortBy: VenueSorting) {
        mSortingProcessor.onNext(sortBy)
    }

    override fun setCurrentLocation(location: Location) {
        mLocationProcessor.onNext(location)
    }

    override fun createVenue(venue: Venue) {
        mCompositeDisposable.add(remoteRepository.createVenue(venue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // Do nothing
                }, { t -> view.showError(t.message) }))
    }

    override fun updateVenue(venue: Venue) {
        mCompositeDisposable.add(remoteRepository.updateVenue(venue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // Do nothing
                }, { t -> view.showError(t.message) }))
    }

    override fun deleteVenue(venue: Venue) {
        mCompositeDisposable.add(remoteRepository.deleteVenue(venue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // Do nothing
                }, { t -> view.showError(t.message) }))
    }

    override fun subscribe() {
        mCompositeDisposable.add(Flowable.combineLatest(remoteRepository.venues().distinctUntilChanged(),
                mSortingProcessor.distinctUntilChanged(),
                BiFunction<List<Venue>, VenueSorting, List<Venue>> { venues, sortingType ->
                    val location = mLocationProcessor.value
                    val comparator: Comparator<Venue> = when (sortingType) {
                        VenueSorting.BY_NAME_REVERS -> NameReverseComparator()
                        VenueSorting.BY_DISTANCE -> {
                            if (location != null) {
                                DistanceComparator(location)
                            } else {
                                throw IllegalStateException("Location is required for sorting by distance")
                            }
                        }
                        else -> NameComparator()
                    }
                    venues.sortedWith(comparator)
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.onVenuesChanged(it) })

        loadVenues()
    }

    override fun unsubscribe() {
        mCompositeDisposable.clear()
    }
}