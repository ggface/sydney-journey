package io.github.ggface.sydneyjourney.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.ggface.sydneyjourney.R;
import io.github.ggface.sydneyjourney.api.pojo.Venue;

/**
 * Venue ViewHolder
 *
 * @author Ivan Novikov on 2018-10-26.
 */
public class VenueViewHolder extends RecyclerView.ViewHolder {

    public static int LAYOUT = R.layout.list_item_venue;

    private Venue mVenue;
    private final OnItemClickListener<Venue> mOnItemClickListener;
    private final TextView mName;

    VenueViewHolder(@NonNull View view,
                    @NonNull OnItemClickListener<Venue> itemClickListener) {
        super(view);
        mOnItemClickListener = itemClickListener;
        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (position > RecyclerView.NO_POSITION) {
                mOnItemClickListener.onItemClick(mVenue, position);
            }
        });

        mName = view.findViewById(R.id.venue_name_text_view);
    }

    public void bind(@NonNull Venue venue) {
        mVenue = venue;
        mName.setText(mVenue.getName());
    }
}