package io.github.ggface.sydneyjourney.mvp

/**
 * Base view
 *
 * @author Ivan Novikov on 2018-10-24.
 */
interface BaseView {

    /**
     * Notify error
     *
     * @param message error message
     */
    fun showError(message: String?)
}