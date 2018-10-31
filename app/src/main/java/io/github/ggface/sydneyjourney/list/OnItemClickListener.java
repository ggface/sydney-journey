package io.github.ggface.sydneyjourney.list;

/**
 * Common item click listener
 *
 * @author Ivan Novikov on 2018-10-26.
 */
public interface OnItemClickListener<T> {

    /**
     * Notify click
     *
     * @param element  element
     * @param position adapter position
     */
    void onItemClick(T element, int position);
}