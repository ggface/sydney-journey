package io.github.ggface.sydneyjourney.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.ggface.sydneyjourney.api.pojo.Venue

/**
 * Adapter for venues
 *
 * @author Ivan Novikov on 2018-10-26.
 */
class VenuesAdapter(private val itemClickListener: OnItemClickListener<Venue>) : RecyclerView.Adapter<VenueViewHolder>() {

    private var mVenues: List<Venue> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(VenueViewHolder.LAYOUT, parent, false)
        return VenueViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(venueViewHolder: VenueViewHolder, position: Int) {
        venueViewHolder.bind(mVenues[position])
    }

    override fun getItemCount(): Int {
        return mVenues.size
    }

    fun setItems(venues: List<Venue>) {
        mVenues = venues
        notifyDataSetChanged()
    }
}