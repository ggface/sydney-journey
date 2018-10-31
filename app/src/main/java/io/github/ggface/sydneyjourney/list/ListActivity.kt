package io.github.ggface.sydneyjourney.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.VenueDialogFragment
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.repository
import kotlinx.android.synthetic.main.activity_list.*

/**
 * Venues List Screen
 *
 * @author Ivan Novikov on 2018-10-22.
 */
class ListActivity : AppCompatActivity(), ListContract.View {

    lateinit var mPresenter: ListContract.Presenter
    lateinit var mAdapter: VenuesAdapter

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        initRecyclerView()
        mPresenter = ListPresenter(this, repository())
    }

    override fun onStart() {
        super.onStart()
        mPresenter.subscribe()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }
    //endregion Lifecycle

    //region ListContract.View
    override fun onLoadingChanged(isActive: Boolean) {
        swipe_refresh_layout.isRefreshing = isActive
    }

    override fun onVenuesChanged(venues: List<Venue>) {
        mAdapter.setItems(venues)
    }

    override fun showError(message: String) {
        Toast.makeText(applicationContext, "error=$message", Toast.LENGTH_SHORT).show()
    }
    //endregion ListContract.View

    private fun initRecyclerView() {
        mAdapter = VenuesAdapter(OnItemClickListener { venue, _ ->
            VenueDialogFragment.openActivateDialog(this, venue)
        })

        venues_recycler_view.adapter = mAdapter
    }
}