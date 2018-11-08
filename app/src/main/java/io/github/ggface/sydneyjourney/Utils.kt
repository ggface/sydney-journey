package io.github.ggface.sydneyjourney

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.util.TypedValue
import android.view.WindowManager
import io.github.ggface.sydneyjourney.api.RemoteRepository

/**
 * Some functions
 *
 * @author Ivan Novikov on 2018-10-24.
 */

object Consts {

    const val PREFERENCES = "APP_PREFERENCES"

    const val REQUEST_CODE_PREFERENCES = 100

    const val LOG_SYSTEM = "LOG_SYSTEM"
    const val LOG_GEO = "LOG_GEO"

    const val ARG_VENUE = "ARG_VENUE"
    const val ARG_LAT = "ARG_LAT"
    const val ARG_LON = "ARG_LON"

    const val KEY_SORTING = "KEY_SORTING"
}

enum class VenueSorting {
    BY_NAME,
    BY_NAME_REVERS,
    BY_DISTANCE
}

fun Activity.repository(): RemoteRepository {
    return (this.application as SydneyJourneyApplication).remoteRepository
}

fun Activity.checkSelfPermissions(permissions: List<String>): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun isMarshmallow(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

/**
 * @param context context
 * @return screen width (px)
 */
fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}

/**
 * Transmute DP to PX
 *
 * @param context context
 * @param dp      independent pixels
 * @return pixels
 */
fun getPixelsFromDp(context: Context, dp: Int): Int {
    val r = context.resources
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).toInt()
}