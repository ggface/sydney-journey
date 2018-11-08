package io.github.ggface.sydneyjourney.map

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.github.ggface.sydneyjourney.BuildConfig
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.dialog.OnVenueEventsListener
import io.github.ggface.sydneyjourney.dialog.VenueDialogFragment
import io.github.ggface.sydneyjourney.list.ListActivity
import io.github.ggface.sydneyjourney.mvp.BaseActivity
import io.github.ggface.sydneyjourney.repository
import kotlinx.android.synthetic.main.activity_map.*


/**
 * Venues Map Screen
 *
 * @author Ivan Novikov on 2018-10-15.
 */
class MapActivity : BaseActivity(), MapContract.View, OnVenueEventsListener {

    private lateinit var mPresenter: MapPresenter
    private var mMap: MapboxMap? = null

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, BuildConfig.MAPKIT_API_KEY)
        setContentView(R.layout.activity_map)
        map_view.onCreate(savedInstanceState)
        initViews()
        mPresenter = MapPresenter(this, repository())
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
        mPresenter.subscribe()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
        mPresenter.unsubscribe()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }
    //endregion Lifecycle

    //region MapContract.View
    override fun showError(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onLoadingChanged(isActive: Boolean) {
        progress_bar.visibility = if (isActive) View.VISIBLE else View.INVISIBLE
    }

    override fun onVenuesChanged(venues: List<Venue>) {
        makeBunch(venues)
        moveCameraToVenues(venues)
    }
    //endregion MapContract.View

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

    override fun onLocationChanged(location: Location) {
        val userLocation = LatLng(location.latitude, location.longitude)
        mMap!!.easeCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10.0))
    }

    private fun initViews() {
        list_button.setOnClickListener { startActivity(Intent(this, ListActivity::class.java)) }
        venues_button.setOnClickListener {
            val venues = ArrayList<Venue>()
            for (marker in mMap!!.markers) {
                venues.add((marker as VenueMarker).venue)
            }
            moveCameraToVenues(venues)
        }
        location_button.setOnClickListener { obtainLocation() }
        progress_bar.visibility = View.INVISIBLE
        map_view.getMapAsync { initMap(it) }
    }

    private fun initMap(map: MapboxMap) {
        map.addOnMapLongClickListener { VenueDialogFragment.openDialog(MapActivity@ this, it.latitude, it.longitude) }
        map.setOnMarkerClickListener {
            VenueDialogFragment.openDialog(MapActivity@ this, (it as VenueMarker).venue)
            true
        }
        mMap = map
        mPresenter.onMapReady()
    }

    private fun makeBunch(venues: List<Venue>) {
        if (mMap == null) {
            throw IllegalStateException("MapBox Map must be ready")
        }

        mMap!!.clear()

        for (venue in venues) {
            mMap!!.addMarker(VenueMarkerOptions(venue))
        }
    }

    private fun moveCameraToVenues(venues: List<Venue>) {
        val latLngBounds = LatLngBounds.Builder()
        for (venue in venues) {
            latLngBounds.include(LatLng(venue.latitude, venue.longitude))
        }
        mMap!!.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 250), 500)
    }
}