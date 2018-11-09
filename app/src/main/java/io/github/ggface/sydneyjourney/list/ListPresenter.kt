package io.github.ggface.sydneyjourney.list

import io.github.ggface.sydneyjourney.VenueSorting
import io.github.ggface.sydneyjourney.api.RemoteRepository
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.list.sorting.DistanceComparator
import io.github.ggface.sydneyjourney.list.sorting.NameComparator
import io.github.ggface.sydneyjourney.list.sorting.NameReverseComparator
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

    private var mLocationDisposable: Disposable? = null
    private val mCompositeDisposable = CompositeDisposable()
    private val mSortingProcessor = BehaviorProcessor.createDefault(defaultSorting)

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
                BiFunction<List<Venue>, VenueSorting, Pair<List<Venue>, VenueSorting>> { venues, sortingType -> Pair(venues, sortingType) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    var venues = it.first
                    val sortingType = it.second

                    cancelLocation()
                    if (sortingType == VenueSorting.BY_DISTANCE) {
                        mLocationDisposable = remoteRepository.location().firstOrError()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    val newVenues = venues.sortedWith(DistanceComparator(it))
                                    view.onVenuesChanged(newVenues)
                                }, { view.showError(it.message) })
                        view.onRequiredLocationPermissions()
                    } else {
                        val comparator: Comparator<Venue> = when (sortingType) {
                            VenueSorting.BY_NAME -> NameComparator()
                            VenueSorting.BY_NAME_REVERS -> NameReverseComparator()
                            else -> throw IllegalStateException("Unknown sorting type")
                        }
                        venues = venues.sortedWith(comparator)
                    }
                    view.onVenuesChanged(venues)
                })

        loadVenues()
    }

    override fun unsubscribe() {
        cancelLocation()
        mCompositeDisposable.clear()
    }

    private fun cancelLocation() {
        mLocationDisposable?.dispose()
    }
}