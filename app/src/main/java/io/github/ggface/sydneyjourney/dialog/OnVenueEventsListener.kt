package io.github.ggface.sydneyjourney.dialog

import io.github.ggface.sydneyjourney.api.pojo.Venue

/**
 * Describe venue events contract
 *
 * @author Ivan Novikov on 2018-11-02.
 */
interface OnVenueEventsListener {

    fun onUpdate(venue: Venue)

    fun onDelete(venue: Venue)

    fun onCreate(venue: Venue)
}