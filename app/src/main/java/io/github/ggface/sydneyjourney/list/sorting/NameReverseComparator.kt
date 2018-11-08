package io.github.ggface.sydneyjourney.list.sorting

import io.github.ggface.sydneyjourney.api.pojo.Venue
import java.util.*

/**
 * Comparator for [Venue] by name desc
 *
 * @author Ivan Novikov on 2018-11-08.
 */
class NameReverseComparator : Comparator<Venue> {

    override fun compare(venue1: Venue, venue2: Venue): Int {
        return venue2.name.compareTo(venue1.name)
    }
}