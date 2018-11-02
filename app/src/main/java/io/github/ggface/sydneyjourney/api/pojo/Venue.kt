package io.github.ggface.sydneyjourney.api.pojo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Describes some place and keep location
 *
 * @author Ivan Novikov on 2018-10-15.
 */
@Parcelize
@Entity(tableName = "venues", indices = arrayOf(Index(value = arrayOf("name"), unique = true)))
data class Venue(@PrimaryKey @ColumnInfo(name = "name") @SerializedName("name") val name: String,
                 @ColumnInfo(name = "latitude") @SerializedName("lat") val latitude: Double,
                 @ColumnInfo(name = "longitude") @SerializedName("lng") val longitude: Double,
                 @ColumnInfo(name = "description") val description: String?) : Parcelable
