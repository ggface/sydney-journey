package io.github.ggface.sydneyjourney.list.sorting

import io.github.ggface.sydneyjourney.api.pojo.Venue
import java.util.*

/**
 * Comparator for [Venue] by name asc
 *
 * @author Ivan Novikov on 2018-11-08.
 */
class NameComparator : Comparator<Venue> {

    override fun compare(venue1: Venue, venue2: Venue): Int {
        return venue1.name.compareTo(venue2.name)
    }
}