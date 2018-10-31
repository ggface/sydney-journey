package io.github.ggface.sydneyjourney.map

import android.os.Parcelable
import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import io.github.ggface.sydneyjourney.api.pojo.Venue
import kotlinx.android.parcel.Parcelize

/**
 * BaseMarkerOptions extension
 *
 * @author Ivan Novikov on 2018-10-31.
 */
@Parcelize
class VenueMarkerOptions(val venue: Venue) : BaseMarkerOptions<VenueMarker, VenueMarkerOptions>(), Parcelable {

    init {
        position = LatLng(venue.latitude, venue.longitude)
        title = venue.name
    }

    override fun getMarker(): VenueMarker {
        return VenueMarker(this, venue)
    }

    override fun getThis(): VenueMarkerOptions {
        return this
    }
}