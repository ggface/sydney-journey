package io.github.ggface.sydneyjourney.mvp

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import io.github.ggface.sydneyjourney.Consts.LOG_GEO
import io.github.ggface.sydneyjourney.Consts.REQUEST_CODE_GEO_PERMISSIONS
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

    private var mGoogleApiClient: GoogleApiClient? = null
    private val mGeoPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)

    //region Lifecycle
    override fun onStop() {
        super.onStop()
        repository().disableGeoUpdates()
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_GEO_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && checkSelfPermissions(mGeoPermissions)) {
                    repository().lastKnownLocation()
                } else {
                    onGeoAccessDenied()
                    showText(R.string.warning_location_denied)
                }
            }
            REQUEST_CODE_PREFERENCES -> {
                if (repository().gpsIsEnabled()) {
                    repository().lastKnownLocation()
                } else {
                    onGeoAccessDenied()
                    showText(R.string.warning_location_denied)
                }
            }
        }
    }
    //endregion Lifecycle

    //region LocationAccessDialogListener
    override fun onNeedShowPreferences() {
        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_PREFERENCES)
    }

    override fun onGeoAccessDenied() {
        // Do nothing
    }
    //endregion LocationAccessDialogListener

    fun showText(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    protected fun obtainLocation() {
        if (isMarshmallow()) {
            val isGranted = checkSelfPermissions(mGeoPermissions)
            if (!repository().gpsIsEnabled()) {
                requestUseLocationIfNeed()
            } else if (isGranted) {
                repository().lastKnownLocation()
            } else {
                ActivityCompat.requestPermissions(this,
                        mGeoPermissions.toTypedArray(),
                        REQUEST_CODE_GEO_PERMISSIONS)
            }
        } else {
            if (!repository().gpsIsEnabled()) {
                LocationAccessDialogFragment.showDialog(this)
            } else {
                repository().lastKnownLocation()
            }
        }
    }

    private fun requestUseLocationIfNeed() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build()
        }

        if (!mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.connect()
        }

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { callback ->
            val status = callback.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> repository().lastKnownLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        status.startResolutionForResult(this@BaseActivity, REQUEST_CODE_PREFERENCES)
                    } catch (e: IntentSender.SendIntentException) {
                        Timber.tag(LOG_GEO).d("requestUseLocationIfNeed(${e.message})")
                    }

                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                    Timber.tag(LOG_GEO).d("requestUseLocationIfNeed() SETTINGS_CHANGE_UNAVAILABLE")
            }
        }
    }
}