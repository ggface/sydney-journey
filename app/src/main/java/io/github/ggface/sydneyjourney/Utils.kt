package io.github.ggface.sydneyjourney

import android.app.Activity
import io.github.ggface.sydneyjourney.api.RemoteRepository

/**
 * Some functions
 *
 * @author Ivan Novikov on 2018-10-24.
 */
fun Activity.repository(): RemoteRepository {
    return (this.application as SydneyJourneyApplication).remoteRepository
}