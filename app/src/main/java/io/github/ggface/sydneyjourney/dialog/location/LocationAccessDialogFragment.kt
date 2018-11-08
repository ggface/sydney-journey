package io.github.ggface.sydneyjourney.dialog.location

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import io.github.ggface.sydneyjourney.R
import io.github.ggface.sydneyjourney.getPixelsFromDp
import io.github.ggface.sydneyjourney.getScreenWidth

class LocationAccessDialogFragment : DialogFragment() {

    private var mDialogListener: LocationAccessDialogListener? = null

    //region Lifecycle
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mDialogListener = activity as LocationAccessDialogListener?
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_location_access, null)

        view.findViewById<View>(R.id.close_image_button).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.cancel_button).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.allow_button).setOnClickListener {
            dismiss()
            mDialogListener!!.onNeedShowPreferences()
        }

        val dialog = Dialog(activity!!, R.style.PopupDialog)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(false)
        setSize(dialog)
        return dialog
    }

    override fun onDetach() {
        super.onDetach()
        mDialogListener = null
    }
    //endregion Lifecycle

    private fun setSize(dialog: Dialog) {
        val width = getScreenWidth(activity!!) - getPixelsFromDp(activity!!, 16 * 2)
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
    }

    companion object {

        private val TAG = LocationAccessDialogFragment::class.java.simpleName

        /**
         * Отображает диалог с подсказкой
         *
         * @param activity активити
         */
        fun showDialog(activity: FragmentActivity) {
            val fragmentManager = activity.supportFragmentManager
            var dialog = fragmentManager.findFragmentByTag(TAG) as LocationAccessDialogFragment?

            if (dialog == null) {
                dialog = LocationAccessDialogFragment()
                dialog.show(activity.supportFragmentManager, TAG)
            }
        }
    }
}