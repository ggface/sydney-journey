package io.github.ggface.sydneyjourney.list

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import io.github.ggface.sydneyjourney.Consts
import io.github.ggface.sydneyjourney.Consts.KEY_SORTING
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.VenueSorting
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.dialog.OnVenueEventsListener
import io.github.ggface.sydneyjourney.dialog.VenueDialogFragment
import io.github.ggface.sydneyjourney.mvp.BaseActivity
import io.github.ggface.sydneyjourney.repository
import kotlinx.android.synthetic.main.activity_list.*

/**
 * Venues List Screen
 *
 * @author Ivan Novikov on 2018-10-22.
 */
class ListActivity : BaseActivity(), ListContract.View, OnVenueEventsListener {

    private lateinit var mPresenter: ListContract.Presenter
    private lateinit var mAdapter: VenuesAdapter

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        initToolbar()
        initViews()

        mPresenter = ListPresenter(this, repository(), getVenueSorting())
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
        onLoadingChanged(false)
    }

    override fun onLocationChanged(location: Location) {
        if (getVenueSorting() == VenueSorting.BY_DISTANCE) {
            mPresenter.sortBy(VenueSorting.BY_DISTANCE)
        }
    }

    override fun onRequiredLocationPermissions() {
        obtainLocation()
    }

    override fun showError(message: String?) {
        Toast.makeText(applicationContext, "error=$message", Toast.LENGTH_SHORT).show()
    }
    //endregion ListContract.View

    //region OnVenueEventsListener
    override fun onUpdate(venue: Venue) {
        mPresenter.updateVenue(venue)
    }

    override fun onDelete(venue: Venue) {
        mPresenter.deleteVenue(venue)
    }

    override fun onCreate(venue: Venue) {
        mPresenter.createVenue(venue)
    }
    //endregion OnVenueEventsListener

    override fun onGeoAccessDenied() {
        super.onGeoAccessDenied()
        onLoadingChanged(false)
    }

    private fun initToolbar() {
        toolbar.inflateMenu(R.menu.menu_list)
        toolbar.setOnMenuItemClickListener {
            var sorting: VenueSorting? = null
            when (it.itemId) {
                R.id.menu_action_az -> sorting = VenueSorting.BY_NAME
                R.id.menu_action_za -> sorting = VenueSorting.BY_NAME_REVERS
                R.id.menu_action_distance -> {
                    sorting = VenueSorting.BY_DISTANCE
                    onLoadingChanged(true)
                }
            }

            if (sorting != null) {
                setVenueSorting(sorting)
                mPresenter.sortBy(sorting)
            }
            true
        }
    }

    private fun initViews() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        mAdapter = VenuesAdapter(object : OnItemClickListener<Venue> {
            override fun onItemClick(element: Venue, position: Int) {
                VenueDialogFragment.openDialog(this@ListActivity, element)
            }
        })
        venues_recycler_view.adapter = mAdapter
        swipe_refresh_layout.setOnRefreshListener { mPresenter.loadVenues() }
    }

    private fun getVenueSorting(): VenueSorting {
        val preferences = getSharedPreferences(Consts.PREFERENCES, Context.MODE_PRIVATE)
        val sortingType = try {
            preferences.getString(KEY_SORTING, VenueSorting.BY_NAME.name)
        } catch (ignore: ClassCastException) {
            VenueSorting.BY_NAME.name
        }
        return VenueSorting.valueOf(sortingType)
    }

    private fun setVenueSorting(sorting: VenueSorting) {
        val preferences = getSharedPreferences(Consts.PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().putString(KEY_SORTING, sorting.name).apply()
    }
}