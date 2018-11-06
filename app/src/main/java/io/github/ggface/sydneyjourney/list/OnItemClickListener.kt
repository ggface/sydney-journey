package io.github.ggface.sydneyjourney.list

/**
 * Common item click listener
 *
 * @author Ivan Novikov on 2018-10-26.
 */
interface OnItemClickListener<T> {

    /**
     * Notify click
     *
     * @param element  element
     * @param position adapter position
     */
    fun onItemClick(element: T, position: Int)
}