package io.github.ggface.sydneyjourney.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.view.WindowManager
import android.widget.*
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.api.pojo.Venue

/**
 * Common bottom sheet dialog
 *
 * @author Ivan Novikov on 2018-10-19.
 */
class VenueDialogFragment : AppCompatDialogFragment() {

    private var mIsNew = false
    private var mOnVenueEventsListener: OnVenueEventsListener? = null
    private lateinit var mVenue: Venue
    private lateinit var mTitleTextView: TextView
    private lateinit var mNoDescriptionTextView: TextView
    private lateinit var mVenueNameEditText: EditText
    private lateinit var mVenueDescriptionEditText: EditText
    private lateinit var mDeleteImageButton: ImageButton
    private lateinit var mEditImageButton: ImageButton
    private lateinit var mDoneImageButton: ImageButton

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mOnVenueEventsListener = activity as OnVenueEventsListener
    }

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
            mIsNew = true
            mVenue = Venue("", arguments!!.getDouble(ARG_LAT), arguments!!.getDouble(ARG_LON), null, true)
            switchToEdit()
        } else {
            mVenue = venue
            switchToView()
        }

        initCallback(contentView)
        fillViews()
        return bottomSheetDialog
    }

    override fun onDetach() {
        super.onDetach()
        mOnVenueEventsListener = null
    }
    //endregion Lifecycle

    private fun switchToView() {
        mVenueNameEditText.visibility = View.GONE
        mTitleTextView.visibility = View.VISIBLE
        mNoDescriptionTextView.visibility = if (mVenue.description.isNullOrEmpty()) View.VISIBLE else View.GONE
        mVenueDescriptionEditText.visibility = if (mVenue.description.isNullOrEmpty()) View.GONE else View.VISIBLE
        mVenueDescriptionEditText.isEnabled = false
        mDeleteImageButton.visibility = View.GONE
        mEditImageButton.visibility = View.VISIBLE
        mDoneImageButton.visibility = View.GONE
    }

    private fun switchToEdit() {
        mVenueNameEditText.visibility = if (mIsNew) View.VISIBLE else View.GONE
        mTitleTextView.visibility = if (mIsNew) View.GONE else View.VISIBLE
        mVenueDescriptionEditText.visibility = View.VISIBLE
        mNoDescriptionTextView.visibility = View.GONE
        mVenueDescriptionEditText.isEnabled = true
        mDeleteImageButton.visibility = if (!mIsNew && mVenue.isManual) View.VISIBLE else View.GONE
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

    private fun fillViews() {
        mTitleTextView.text = mVenue.name
        mVenueDescriptionEditText.setText(mVenue.description)
        mEditImageButton.setOnClickListener {
            switchToEdit()
            mVenueNameEditText.setText(mVenue.name)
            mVenueDescriptionEditText.setText(mVenue.description)
        }

        mDoneImageButton.setOnClickListener {
            if (mIsNew) {
                if (isValidName()) {
                    mOnVenueEventsListener!!.onCreate(mVenue.copy(
                            name = mVenueNameEditText.text.toString(),
                            description = mVenueDescriptionEditText.text.toString()))
                    dismiss()
                } else {
                    Toast.makeText(activity, R.string.dialog_name_error, Toast.LENGTH_SHORT).show()
                }
            } else {
                if (mVenue.isManual) {
                    mOnVenueEventsListener!!.onUpdate(mVenue.copy(
                            description = mVenueDescriptionEditText.text.toString()))
                } else {
                    mOnVenueEventsListener!!.onCreate(mVenue.copy(
                            name = mVenueNameEditText.text.toString(),
                            description = mVenueDescriptionEditText.text.toString()))
                }

                dismiss()
            }
        }

        mDeleteImageButton.setOnClickListener {
            mOnVenueEventsListener!!.onDelete(mVenue)
            dismiss()
        }
    }

    private fun isValidName(): Boolean {
        return mVenueNameEditText.text.toString().isNotEmpty()
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
        fun openDialog(activity: FragmentActivity,
                       venue: Venue) {
            val dialog = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (dialog == null) {
                activity.supportFragmentManager.beginTransaction()
                        .add(newInstance(venue, null, null), TAG)
                        .commitAllowingStateLoss()
            }
        }

        /**
         * Entry point for creating the venue. Shows venue dialog.
         *
         * @param activity  Activity
         * @param latitude  Location latitude
         * @param longitude Location longitude
         */
        @JvmStatic
        fun openDialog(activity: FragmentActivity,
                       latitude: Double,
                       longitude: Double?) {
            val dialog = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (dialog == null) {
                activity.supportFragmentManager.beginTransaction()
                        .add(newInstance(null, latitude, longitude), TAG)
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