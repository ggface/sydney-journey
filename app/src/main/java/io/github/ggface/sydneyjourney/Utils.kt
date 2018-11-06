package io.github.ggface.sydneyjourney

import android.app.Activity
import io.github.ggface.sydneyjourney.api.RemoteRepository

/**
 * Some functions
 *
 * @author Ivan Novikov on 2018-10-24.
 */

object Consts {

    const val LOG_SYSTEM = "LOG_SYSTEM"

    const val ARG_VENUE = "ARG_VENUE"
    const val ARG_LAT = "ARG_LAT"
    const val ARG_LON = "ARG_LON"
}

fun Activity.repository(): RemoteRepository {
    return (this.application as SydneyJourneyApplication).remoteRepository
}