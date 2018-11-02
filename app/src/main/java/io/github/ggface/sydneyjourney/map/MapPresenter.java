package io.github.ggface.sydneyjourney.map;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import io.github.ggface.sydneyjourney.api.RemoteRepository;
import io.github.ggface.sydneyjourney.api.pojo.Venue;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.BehaviorProcessor;

/**
 * Presenter of Map Screen
 *
 * @author Ivan Novikov on 2018-10-26.
 */
public class MapPresenter implements MapContract.Presenter {

    private final MapContract.View mView;
    private final RemoteRepository mRemoteRepository;
    private final CompositeDisposable mCompositeDisposable;
    private final BehaviorProcessor<Integer> mMapWaitingProcessor;

    MapPresenter(@NonNull MapContract.View view,
                 @NonNull RemoteRepository remoteRepository) {
        mView = view;
        mRemoteRepository = remoteRepository;
        mCompositeDisposable = new CompositeDisposable();
        mMapWaitingProcessor = BehaviorProcessor.create();
    }

    @Override
    public void onMapReady() {
        mMapWaitingProcessor.onNext(1);
    }

    @Override
    public void obtainVenues() {
        mView.onLoadingChanged(true);
        mCompositeDisposable.add(mRemoteRepository.obtainVenues()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mView.onLoadingChanged(false),
                        t -> mView.onLoadingChanged(false)));
    }

    @Override
    public void createVenue(@NotNull Venue venue) {
        mCompositeDisposable.add(mRemoteRepository.createVenue(venue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // Do nothing
                }, t -> mView.showError(t.getMessage())));
    }

    @Override
    public void updateVenue(@NotNull Venue venue) {
        mCompositeDisposable.add(mRemoteRepository.updateVenue(venue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // Do nothing
                }, t -> mView.showError(t.getMessage())));
    }

    @Override
    public void deleteVenue(@NotNull Venue venue) {
        mCompositeDisposable.add(mRemoteRepository.deleteVenue(venue)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // Do nothing
                }, t -> mView.showError(t.getMessage())));
    }

    @Override
    public void subscribe() {
        mCompositeDisposable.add(mRemoteRepository.venues()
                .withLatestFrom(mMapWaitingProcessor, (venues, integer) -> venues)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::onVenuesChanged));

        obtainVenues();
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
