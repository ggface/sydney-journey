package io.github.ggface.sydneyjourney.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import io.github.ggface.sydneyjourney.api.pojo.Venue;

/**
 * Adapter for venues
 *
 * @author Ivan Novikov on 2018-10-26.
 */
public class VenuesAdapter extends RecyclerView.Adapter<VenueViewHolder> {

    private List<Venue> mVenues;
    private final OnItemClickListener<Venue> mOnItemClickListener;

    public VenuesAdapter(OnItemClickListener<Venue> itemClickListener) {
        mVenues = Collections.emptyList();
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(VenueViewHolder.LAYOUT, parent, false);
        return new VenueViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder venueViewHolder, int position) {
        venueViewHolder.bind(mVenues.get(position));
    }

    @Override
    public int getItemCount() {
        return mVenues.size();
    }

    public void setItems(@NonNull List<Venue> venues) {
        mVenues = venues;
        notifyDataSetChanged();
    }
}