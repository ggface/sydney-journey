package io.github.ggface.sydneyjourney

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider
import io.github.ggface.sydneyjourney.api.RemoteRepository
import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val SYDNEY_POINT = Point(-33.8791, 151.1944)

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var mapObjects: MapObjectCollection

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        mapObjects = mapView.map.mapObjects.addCollection()
        mapView.map.move(
                CameraPosition(SYDNEY_POINT, 12f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()

        mCompositeDisposable.add(getRemoteRepository().obtainVenues()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.tag("sys_").d("MainActivity::obtainVenues() OK") },
                        { Timber.tag("sys_").d("MainActivity::obtainVenues() FAIL -> ${it.message}") }))


        mCompositeDisposable.add(getRemoteRepository().venues()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ makeBunch(it) }))
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
        mCompositeDisposable.clear()
    }
    //endregion Lifecycle

    private fun getRemoteRepository(): RemoteRepository {
        return (application as SydneyJourneyApplication).remoteRepository
    }

    private fun makeBunch(venues: List<Venue>) {
        for (venue in venues) {
            val image: ImageProvider = ImageProvider.fromResource(this, R.drawable.ic_maps_venue)
            val placemark = mapObjects.addPlacemark(Point(venue.latitude, venue.longitude), image)
            placemark.userData = venue

            placemark.addTapListener(MapObjectTapListener { mapObject: MapObject?, point: Point? ->
                val venue = mapObject!!.userData as Venue
                VenueDialogFragment.openActivateDialog(this, venue)
                true
            })
        }
    }
}