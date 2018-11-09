package io.github.ggface.sydneyjourney.map

import io.github.ggface.sydneyjourney.api.RemoteRepository
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor

/**
 * Presenter of Map Screen
 *
 * @author Ivan Novikov on 2018-10-26.
 */
class MapPresenter internal constructor(private val view: MapContract.View,
                                        private val remoteRepository: RemoteRepository) : MapContract.Presenter {

    private var mLocationDisposable: Disposable? = null
    private val mCompositeDisposable = CompositeDisposable()
    private val mMapWaitingProcessor = BehaviorProcessor.create<Int>()

    override fun listenLocation() {
        mLocationDisposable?.dispose()
        mLocationDisposable = remoteRepository.location().firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onLocationChanged(it)
                }, {
                    view.showError(it.message)
                })
    }

    override fun onMapReady() {
        mMapWaitingProcessor.onNext(1)
    }

    override fun loadVenues() {
        view.onLoadingChanged(true)
        mCompositeDisposable.add(remoteRepository.obtainVenues()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view.onLoadingChanged(false) },
                        { t ->
                            view.onLoadingChanged(false)
                            view.showError(t.message)
                        }))
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
        mCompositeDisposable.add(remoteRepository.venues()
                .withLatestFrom(mMapWaitingProcessor, BiFunction<List<Venue>, Int, List<Venue>> { t1, _ -> t1 })
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.onVenuesChanged(it) })
        loadVenues()
    }

    override fun unsubscribe() {
        mLocationDisposable?.dispose()
        mCompositeDisposable.clear()
    }
}
