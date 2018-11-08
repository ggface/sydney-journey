package io.github.ggface.sydneyjourney.mvp

import android.Manifest
import android.content.Intent
import android.location.Location
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.github.ggface.sydneyjourney.Consts.LOG_GEO
import io.github.ggface.sydneyjourney.Consts.REQUEST_CODE_PREFERENCES
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.checkSelfPermissions
import io.github.ggface.sydneyjourney.dialog.location.LocationAccessDialogFragment
import io.github.ggface.sydneyjourney.dialog.location.LocationAccessDialogListener
import io.github.ggface.sydneyjourney.isMarshmallow
import io.github.ggface.sydneyjourney.repository
import timber.log.Timber

/**
 * Base activity
 *
 * @author Ivan Novikov on 2018-11-07.
 */
abstract class BaseActivity : AppCompatActivity(), LocationAccessDialogListener {

    private val mGeoPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)

    //region Lifecycle
    override fun onStop() {
        super.onStop()
        repository().disableGeoUpdates()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE_PREFERENCES == requestCode) {
            if (!repository().gpsIsEnabled()) {
                onGeoAccessDenied()
                showText(R.string.warning_location_denied)
            } else {
                repository().lastKnownLocation()
            }
        }
    }
    //endregion Lifecycle

    //region LocationAccessDialogListener
    override fun onNeedShowPreferences() {
        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_PREFERENCES)
    }

    open override fun onGeoAccessDenied() {
        // Do nothing
    }
    //endregion LocationAccessDialogListener

    fun showText(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    open fun onLocationChanged(location: Location) {
        Timber.tag(LOG_GEO).d("onLocationChanged($location)")
        // Do nothing
    }

    protected fun obtainLocation() {
        if (isMarshmallow()) {
            val isGranted = checkSelfPermissions(mGeoPermissions)
            if (isGranted) {
                repository().lastKnownLocation()
            } else {
                //TODO: request permissions
            }
        } else {
            if (!repository().gpsIsEnabled()) {
                LocationAccessDialogFragment.showDialog(this)
            } else {
                repository().lastKnownLocation()
            }
        }
    }
}