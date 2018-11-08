package io.github.ggface.sydneyjourney.dialog.location

/**
 * Location access dialog listener
 *
 * @author Ivan Novikov on 2018-11-07.
 */
interface LocationAccessDialogListener {

    /**
     * Need show Android Preferences
     */
    fun onNeedShowPreferences()

    /**
     * Notify about user dismiss dialog
     */
    fun onGeoAccessDenied()
}