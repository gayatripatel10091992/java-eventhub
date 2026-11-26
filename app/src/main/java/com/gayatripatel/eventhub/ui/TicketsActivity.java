package com.gayatripatel.eventhub.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gayatripatel.eventhub.R;
import com.gayatripatel.eventhub.core.DateLabels;
import com.gayatripatel.eventhub.core.Money;
import com.gayatripatel.eventhub.core.Ticket;
import com.gayatripatel.eventhub.data.ServiceLocator;
import com.gayatripatel.eventhub.databinding.ActivityTicketsBinding;
import com.gayatripatel.eventhub.databinding.ItemTicketBinding;
import java.util.ArrayList;
import java.util.List;

public class TicketsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTicketsBinding b = ActivityTicketsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        TicketsAdapter adapter = new TicketsAdapter();
        b.ticketsList.setLayoutManager(new LinearLayoutManager(this));
        b.ticketsList.setAdapter(adapter);

        TicketsViewModel vm = new ViewModelProvider(this, new AppViewModelFactory(this, null)).get(TicketsViewModel.class);
        vm.tickets().observe(this, tickets -> {
            adapter.submit(tickets);
            b.emptyView.setVisibility(tickets.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private static final class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.Holder> {
        private final List<Ticket> items = new ArrayList<>();

        void submit(List<Ticket> tickets) {
            items.clear();
            items.addAll(tickets);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(ItemTicketBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int position) {
            Ticket t = items.get(position);
            h.b.title.setText(t.eventTitle);
            h.b.date.setText(DateLabels.full(t.startsAtMillis, ServiceLocator.zone()));
            h.b.venue.setText(t.venue);
            h.b.line.setText(h.itemView.getContext().getString(R.string.ticket_line, t.tierName, t.quantity, Money.format(t.totalCents)));
            h.b.ref.setText(t.bookingRef);
            int px = (int) (108 * h.itemView.getResources().getDisplayMetrics().density);
            h.b.qr.setImageBitmap(QrCodes.render(t.checkInPayload(), px));
        }

        static final class Holder extends RecyclerView.ViewHolder {
            final ItemTicketBinding b;

            Holder(ItemTicketBinding binding) {
                super(binding.getRoot());
                b = binding;
            }
        }
    }
}
