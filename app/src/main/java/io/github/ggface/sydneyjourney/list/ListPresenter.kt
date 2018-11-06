package io.github.ggface.sydneyjourney.list

import io.github.ggface.sydneyjourney.api.RemoteRepository
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Presenter of Venues List Screen
 *
 * @author Ivan Novikov on 2018-10-24.
 */
class ListPresenter(private val view: ListContract.View,
                    private val remoteRepository: RemoteRepository) : ListContract.Presenter {

    private val mCompositeDisposable = CompositeDisposable()

    override fun loadVenues() {
        view.onLoadingChanged(true)
        mCompositeDisposable.add(remoteRepository.obtainVenues()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view.onLoadingChanged(false) },
                        { view.onLoadingChanged(false) }))
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
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.onVenuesChanged(it) })

        loadVenues()
    }

    override fun unsubscribe() {
        mCompositeDisposable.clear()
    }
}