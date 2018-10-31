package io.github.ggface.sydneyjourney.map

import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions
import com.mapbox.mapboxsdk.annotations.Marker

import io.github.ggface.sydneyjourney.api.pojo.Venue

/**
 * Marker contains some venue
 *
 * @author Ivan Novikov on 2018-10-31.
 */
class VenueMarker(baseMarkerOptions: BaseMarkerOptions<*, *>, val venue: Venue) : Marker(baseMarkerOptions)