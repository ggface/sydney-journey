package io.github.ggface.sydneyjourney.map

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.github.ggface.sydneyjourney.BuildConfig
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.VenueDialogFragment
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.github.ggface.sydneyjourney.list.ListActivity
import io.github.ggface.sydneyjourney.repository
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.mapbox_mapview_internal.view.*


/**
 * Venues Map Screen
 *
 * @author Ivan Novikov on 2018-10-15.
 */
class MapActivity : AppCompatActivity(), MapContract.View {

    private lateinit var mPresenter: MapPresenter
    private var mMap: MapboxMap? = null

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, BuildConfig.MAPKIT_API_KEY)
        setContentView(R.layout.activity_map)
        map_view.onCreate(savedInstanceState)
        initToolbar()
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

    override fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onLoadingChanged(isActive: Boolean) {
        progress_bar.visibility = if (isActive) View.VISIBLE else View.INVISIBLE
    }

    override fun onVenuesChanged(venues: List<Venue>) {
        makeBunch(venues)
    }

    //endregion MapContract.View

    private fun initToolbar() {
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_action_list) {
                startActivity(Intent(this, ListActivity::class.java))
            }
            true
        }
    }

    private fun initViews() {
        progress_bar.visibility = View.INVISIBLE
        map_view.getMapAsync { initMap(it) }
        map_view.compassView.visibility = View.GONE
    }

    private fun initMap(map: MapboxMap) {
        map.addOnMapLongClickListener { VenueDialogFragment.openActivateDialog(MapActivity@ this, it.latitude, it.longitude) }
        map.setOnMarkerClickListener {
            VenueDialogFragment.openActivateDialog(MapActivity@ this, (it as VenueMarker).venue)
            true
        }
        mMap = map
        mPresenter.onMapReady()
    }

    private fun makeBunch(venues: List<Venue>) {
        if (mMap == null) {
            throw IllegalStateException("MapBox Map must be ready")
        }
        val latLngBounds = LatLngBounds.Builder()
        for (venue in venues) {
            mMap!!.addMarker(VenueMarkerOptions(venue))
            latLngBounds.include(LatLng(venue.latitude, venue.longitude))
        }

        mMap!!.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 250), 500)
    }
}