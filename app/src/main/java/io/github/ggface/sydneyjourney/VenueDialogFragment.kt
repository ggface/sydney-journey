package io.github.ggface.sydneyjourney

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import io.github.ggface.sydneyjourney.api.pojo.Venue

/**
 * Common bottom sheet dialog
 *
 * @author Ivan Novikov on 2018-10-19.
 */
class VenueDialogFragment : AppCompatDialogFragment() {

    private lateinit var mVenue: Venue
    private lateinit var mTitleTextView: TextView
    private lateinit var mNoDescriptionTextView: TextView
    private lateinit var mVenueNameEditText: EditText
    private lateinit var mVenueDescriptionEditText: EditText
    private lateinit var mDeleteImageButton: ImageButton
    private lateinit var mEditImageButton: ImageButton
    private lateinit var mDoneImageButton: ImageButton

    //region Lifecycle
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(activity!!, R.style.App_Theme_Sheet)
        val contentView = View.inflate(context, R.layout.dialog_venue, null)
        bottomSheetDialog.setContentView(contentView)
        initViews(contentView)

        if (bottomSheetDialog.window != null) {
            bottomSheetDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        }

        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog

            val bottomSheet = d.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        val venue: Venue? = arguments!!.getParcelable(ARG_VENUE)

        if (venue == null) {
            mVenue = Venue("", arguments!!.getDouble(ARG_LAT), arguments!!.getDouble(ARG_LON), null)
            switchToEdit()
        } else {
            mVenue = venue
            switchToView()
        }

        initCallback(contentView)

        mTitleTextView.text = mVenue.name

        return bottomSheetDialog
    }
    //endregion Lifecycle

    private fun switchToView() {
        mVenueNameEditText.visibility = View.GONE
        mTitleTextView.visibility = View.VISIBLE
        mNoDescriptionTextView.visibility = if (mVenue.description.isEmpty()) View.VISIBLE else View.GONE
        mVenueDescriptionEditText.visibility = if (mVenue.description.isEmpty()) View.GONE else View.VISIBLE

        mDeleteImageButton.visibility = View.GONE
        mEditImageButton.visibility = View.VISIBLE
        mDoneImageButton.visibility = View.GONE
    }

    private fun switchToEdit() {
        mVenueNameEditText.visibility = View.VISIBLE
        mTitleTextView.visibility = View.GONE
        mVenueDescriptionEditText.visibility = View.VISIBLE
        mNoDescriptionTextView.visibility = View.GONE

        mDeleteImageButton.visibility = View.GONE
        mEditImageButton.visibility = View.GONE
        mDoneImageButton.visibility = View.VISIBLE
    }

    private fun initCallback(contentView: View) {
        val mBottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.skipCollapsed = false
        }
    }

    private fun initViews(contentView: View) {
        mTitleTextView = contentView.findViewById(R.id.title_text_view)
        mNoDescriptionTextView = contentView.findViewById(R.id.no_description_text_view)
        mVenueNameEditText = contentView.findViewById(R.id.venue_name_edit_text)
        mVenueDescriptionEditText = contentView.findViewById(R.id.venue_description_edit_text)
        mDeleteImageButton = contentView.findViewById(R.id.delete_image_button)
        mEditImageButton = contentView.findViewById(R.id.edit_image_button)
        mDoneImageButton = contentView.findViewById(R.id.done_image_button)
    }

    companion object {

        private const val ARG_VENUE = "ARG_VENUE"
        private const val ARG_LAT = "ARG_LAT"
        private const val ARG_LON = "ARG_LON"

        private val TAG = VenueDialogFragment::class.java.simpleName

        /**
         * Entry point. Shows venue dialog.
         *
         * @param activity Activity
         * @param venue    Venue
         */
        fun openActivateDialog(activity: FragmentActivity,
                               venue: Venue) {
            val dialog = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (dialog == null) {
                activity.supportFragmentManager.beginTransaction()
                        .add(VenueDialogFragment.newInstance(venue, null, null), TAG)
                        .commitAllowingStateLoss()
            }
        }

        /**
         * Entry point. Shows venue dialog.
         *
         * @param activity  Activity
         * @param latitude  Location latitude
         * @param longitude Location longitude
         */
        @JvmStatic
        fun openActivateDialog(activity: FragmentActivity,
                               latitude: Double,
                               longitude: Double?) {
            val dialog = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (dialog == null) {
                activity.supportFragmentManager.beginTransaction()
                        .add(VenueDialogFragment.newInstance(null, latitude, longitude), TAG)
                        .commitAllowingStateLoss()
            }
        }

        /**
         * Default constructor
         *
         * @param venue     Venue
         * @param latitude  Location latitude
         * @param longitude Location longitude
         * @return dialog
         */
        private fun newInstance(venue: Venue?,
                                latitude: Double?,
                                longitude: Double?): VenueDialogFragment {
            val fragment = VenueDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_VENUE, venue)
            if (latitude != null && longitude != null) {
                bundle.putDouble(ARG_LAT, latitude)
                bundle.putDouble(ARG_LON, longitude)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}