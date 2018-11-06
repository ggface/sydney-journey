package io.github.ggface.sydneyjourney.map

import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.mvp.BasePresenter
import io.github.ggface.sydneyjourney.mvp.BaseView

/**
 * Contract of Map Screen
 *
 * @author Ivan Novikov on 2018-10-26.
 */
interface MapContract {

    interface View : BaseView {

        /**
         * Notify loading status is changed
         *
         * @param isActive if true, loading is progress
         */
        fun onLoadingChanged(isActive: Boolean)

        /**
         * Notify changing of venues
         *
         * @param venues venues list
         */
        fun onVenuesChanged(venues: List<Venue>)
    }

    interface Presenter : BasePresenter {

        /**
         * Notify map is ready
         */
        fun onMapReady()

        /**
         * Get venues
         */
        fun loadVenues()

        /**
         * Create new venue
         */
        fun createVenue(venue: Venue)

        /**
         * Change venue
         */
        fun updateVenue(venue: Venue)

        /**
         * Delete venue
         */
        fun deleteVenue(venue: Venue)
    }
}