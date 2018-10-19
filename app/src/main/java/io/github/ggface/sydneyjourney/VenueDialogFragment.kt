package io.github.ggface.sydneyjourney

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import io.github.ggface.sydneyjourney.api.pojo.Venue

/**
 * Common bottom sheet dialog
 *
 * @author Ivan Novikov on 2018-10-19.
 */
class VenueDialogFragment : AppCompatDialogFragment() {

    lateinit var mVenue: Venue

    protected val softInputMode: Int
        get() = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    /**
     * @return start state
     * @see BottomSheetBehavior.setState
     */
    protected val startState: Int
        get() = BottomSheetBehavior.STATE_EXPANDED

    //region Lifecycle

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(activity!!, R.style.App_Theme_Sheet)
        val contentView = View.inflate(context, R.layout.dialog_venue, null)
        bottomSheetDialog.setContentView(contentView)

        if (bottomSheetDialog.window != null) {
            bottomSheetDialog.window!!.setSoftInputMode(softInputMode)
        }

        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog

            val bottomSheet = d.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = startState
            }
        }

        mVenue = arguments!!.getParcelable(ARG_VENUE)!!

        initCallback(contentView)
        initViews(contentView)
        return bottomSheetDialog
    }
    //endregion Lifecycle

    private fun initCallback(contentView: View) {
        val mBottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.skipCollapsed = false
        }
    }

    private fun initViews(contentView: View) {
        contentView.findViewById<TextView>(R.id.title_text_view).text = mVenue.name
    }

    companion object {

        private val ARG_VENUE = "ARG_VENUE"
        private val TAG = VenueDialogFragment::class.java.simpleName

        /**
         * Entry point. Shows dialog.
         *
         * @param activity Activity
         * @param venue    Venue
         */
        fun openActivateDialog(activity: FragmentActivity,
                               venue: Venue) {
            val dialog = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (dialog == null) {
                activity.supportFragmentManager.beginTransaction()
                        .add(VenueDialogFragment.newInstance(venue), TAG)
                        .commitAllowingStateLoss()
            }
        }

        /**
         * Default constructor
         *
         * @param venue Venue
         * @return dialog
         */
        private fun newInstance(venue: Venue): VenueDialogFragment {
            val fragment = VenueDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_VENUE, venue)
            fragment.arguments = bundle
            return fragment
        }
    }
}