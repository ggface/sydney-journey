package io.github.ggface.sydneyjourney.api.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.github.ggface.sydneyjourney.nonNull
import kotlinx.android.parcel.Parcelize

/**
 * Describes some place and keep location
 *
 * @author Ivan Novikov on 2018-10-15.
 */
@Parcelize
data class Venue(@SerializedName("name") val name: String,
                 @SerializedName("lat") val latitude: Double,
                 @SerializedName("lng") val longitude: Double,
                 val _description: String?) : Parcelable {

    val description get() = nonNull(_description)
}
