package io.github.ggface.sydneyjourney.list.sorting

import android.location.Location
import io.github.ggface.sydneyjourney.api.pojo.Venue
import java.util.*

/**
 * Comparator for [Venue] by distance
 *
 * @author Ivan Novikov on 2018-11-08.
 */
class DistanceComparator(private val geoLocation: Location) : Comparator<Venue> {

    override fun compare(venue1: Venue, venue2: Venue): Int {
        return distanceFromMe(venue1.latitude, venue1.longitude)
                .compareTo(distanceFromMe(venue2.latitude, venue2.longitude))
    }

    private fun distanceFromMe(latitude: Double, longitude: Double): Double {
        val theta = longitude - geoLocation.longitude
        var dist = Math.sin(deg2rad(latitude)) * Math.sin(deg2rad(geoLocation.latitude)) +
                (Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(geoLocation.latitude))
                        * Math.cos(deg2rad(theta)))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}