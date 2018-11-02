package io.github.ggface.sydneyjourney

import android.app.Activity
import io.github.ggface.sydneyjourney.api.RemoteRepository

/**
 * Some functions
 *
 * @author Ivan Novikov on 2018-10-24.
 */

object Consts {

    const val EXTRA_VENUE = "EXTRA_VENUE"
}

fun Activity.repository(): RemoteRepository {
    return (this.application as SydneyJourneyApplication).remoteRepository
}

fun nonNull(nullableString: String?): String {
    return nullableString ?: ""
}