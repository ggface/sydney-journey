package io.github.ggface.sydneyjourney.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.repository

/**
 * Venues List Screen
 *
 * @author Ivan Novikov on 2018-10-22.
 */
class ListActivity : AppCompatActivity(), ListContract.View {

    lateinit var mPresenter: ListContract.Presenter

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        mPresenter = ListPresenter(this, repository())

    }
    //endregion Lifecycle

    //region ListContract.View
    override fun onLoadingChanged(isActive: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onVenuesChanged(venues: List<Venue>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion ListContract.View
}