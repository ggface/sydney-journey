package io.github.ggface.sydneyjourney.list

import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.totalcoin.ui.BasePresenter
import io.totalcoin.ui.BaseView

/**
 * Contract of Venues List Screen
 *
 * @author Ivan Novikov on 2018-10-24.
 */
interface ListContract {

    interface View : BaseView {

        fun onLoadingChanged(isActive: Boolean)

        fun onVenuesChanged(venues: List<Venue>)
    }

    interface Presenter : BasePresenter {

        fun loadVenues()
    }
}