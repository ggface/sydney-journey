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
data class Venue(@field:PrimaryKey @field:ColumnInfo(name = "name") @SerializedName("name") val name: String,
                 @field:ColumnInfo(name = "latitude") @SerializedName("lat") val latitude: Double,
                 @field:ColumnInfo(name = "longitude") @SerializedName("lng") val longitude: Double,
                 @field:ColumnInfo(name = "description") val description: String?,
                 val isManual: Boolean) : Parcelable