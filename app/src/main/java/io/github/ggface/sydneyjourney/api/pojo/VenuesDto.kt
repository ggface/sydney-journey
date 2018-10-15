package io.github.ggface.sydneyjourney.api.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Предназначение
 *
 * @author Ivan Novikov on 2018-10-15.
 */
data class VenuesDto(@SerializedName("locations") val venues: List<Venue>,
                     @SerializedName("updated") val updatedDate: Date)