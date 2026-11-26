package com.gayatripatel.eventhub.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.gayatripatel.eventhub.R;
import com.gayatripatel.eventhub.core.DateLabels;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.Money;
import com.gayatripatel.eventhub.core.TicketTier;
import com.gayatripatel.eventhub.databinding.ItemEventBinding;
import java.time.ZoneId;

final class EventAdapter extends ListAdapter<Event, EventAdapter.Holder> {

    interface OnEventClick {
        void onClick(Event event);
    }

    private final ZoneId zone;
    private final OnEventClick onClick;

    EventAdapter(ZoneId zone, OnEventClick onClick) {
        super(DIFF);
        this.zone = zone;
        this.onClick = onClick;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id.hashCode();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(getItem(position));
    }

    final class Holder extends RecyclerView.ViewHolder {
        private final ItemEventBinding b;

        Holder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(Event e) {
            Context c = b.getRoot().getContext();
            b.banner.setBackground(EventArt.banner(e.category, e.id));
            b.glyph.setImageResource(EventArt.glyph(e.category));
            b.month.setText(DateLabels.month(e.startsAtMillis, zone));
            b.day.setText(DateLabels.day(e.startsAtMillis, zone));
            b.category.setText(e.category.label);
            b.title.setText(e.title);
            b.meta.setText(DateLabels.full(e.startsAtMillis, zone) + " · " + e.venue);

            TicketTier cheapest = e.cheapestAvailableTier();
            if (cheapest == null) {
                b.price.setText(R.string.sold_out);
                b.badge.setVisibility(View.GONE);
            } else {
                b.price.setText(cheapest.isFree() ? Money.format(0) : c.getString(R.string.from_price, Money.format(cheapest.priceCents)));
                boolean fast = e.isSellingFast();
                b.badge.setVisibility(fast ? View.VISIBLE : View.GONE);
                if (fast) b.badge.setText(c.getString(R.string.selling_fast) + " · " + c.getString(R.string.left, e.totalRemaining()));
            }
            b.getRoot().setOnClickListener(v -> onClick.onClick(e));
        }
    }

    private static final DiffUtil.ItemCallback<Event> DIFF = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(@NonNull Event a, @NonNull Event b) {
            return a.id.equals(b.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event a, @NonNull Event b) {
            return a.title.equals(b.title) && a.startsAtMillis == b.startsAtMillis && a.totalRemaining() == b.totalRemaining();
        }
    };
}
