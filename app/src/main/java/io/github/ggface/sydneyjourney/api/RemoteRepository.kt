package io.github.ggface.sydneyjourney.api

import io.github.ggface.sydneyjourney.api.pojo.Venue
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Предназначение
 *
 * @author Ivan Novikov on 2018-10-15.
 */
interface RemoteRepository {

    fun obtainVenues(): Completable

    fun venues(): Flowable<List<Venue>>
}