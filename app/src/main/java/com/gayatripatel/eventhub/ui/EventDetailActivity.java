package com.gayatripatel.eventhub.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.gayatripatel.eventhub.R;
import com.gayatripatel.eventhub.core.DateLabels;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.Money;
import com.gayatripatel.eventhub.core.OrderCalculator;
import com.gayatripatel.eventhub.core.TicketTier;
import com.gayatripatel.eventhub.data.ServiceLocator;
import com.gayatripatel.eventhub.databinding.ActivityEventDetailBinding;
import com.gayatripatel.eventhub.databinding.ItemSummaryLineBinding;
import com.gayatripatel.eventhub.databinding.ItemTierBinding;
import com.google.android.material.snackbar.Snackbar;
import java.time.ZoneId;

public class EventDetailActivity extends AppCompatActivity {
    private static final String EXTRA_EVENT_ID = "event_id";

    public static Intent intent(Context context, String eventId) {
        return new Intent(context, EventDetailActivity.class).putExtra(EXTRA_EVENT_ID, eventId);
    }

    private ActivityEventDetailBinding b;
    private EventDetailViewModel vm;
    private String renderedEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        String eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        vm = new ViewModelProvider(this, new AppViewModelFactory(this, eventId)).get(EventDetailViewModel.class);

        b.toolbar.setNavigationOnClickListener(v -> finish());
        b.minus.setOnClickListener(v -> vm.changeQuantity(-1));
        b.plus.setOnClickListener(v -> vm.changeQuantity(+1));
        b.book.setOnClickListener(v -> vm.book(getString(R.string.holder_default)));

        vm.state().observe(this, this::render);
        vm.messages().observe(this, m -> {
            if (m != null) Snackbar.make(b.getRoot(), m, Snackbar.LENGTH_LONG).show();
        });
        vm.booked().observe(this, ticket -> {
            if (ticket == null) return;
            startActivity(new Intent(this, TicketsActivity.class));
            finish();
        });
    }

    private void render(EventDetailViewModel.OrderState s) {
        Event e = s.event;
        ZoneId zone = ServiceLocator.zone();
        if (!e.id.equals(renderedEventId)) {
            renderedEventId = e.id;
            b.banner.setBackground(EventArt.banner(e.category, e.id));
            b.glyph.setImageResource(EventArt.glyph(e.category));
            b.title.setText(e.title);
            b.date.setText(DateLabels.full(e.startsAtMillis, zone));
            b.venue.setText(e.venue + ", " + e.city);
            b.organizer.setText(getString(R.string.by_organizer, e.organizer));
            b.description.setText(e.description);
            b.countdown.setText(DateLabels.countdown(e.startsAtMillis, e.endsAtMillis, ServiceLocator.clock().now(), zone));
        }

        b.tiers.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        for (TicketTier tier : e.tiers) {
            ItemTierBinding t = ItemTierBinding.inflate(inflater, b.tiers, false);
            t.tierName.setText(tier.name);
            t.tierPerks.setText(tier.isSoldOut() ? getString(R.string.sold_out) : getString(R.string.tier_price, tier.perks, tier.maxPerOrder));
            t.tierPrice.setText(Money.format(tier.priceCents));
            t.getRoot().setChecked(s.tier != null && s.tier.id.equals(tier.id));
            t.getRoot().setEnabled(!tier.isSoldOut());
            t.getRoot().setAlpha(tier.isSoldOut() ? 0.5f : 1f);
            t.getRoot().setOnClickListener(v -> vm.selectTier(tier.id));
            b.tiers.addView(t.getRoot());
        }

        b.quantity.setText(String.valueOf(s.quantity));
        b.error.setVisibility(s.error == null ? View.GONE : View.VISIBLE);
        b.error.setText(s.error);

        b.summary.removeAllViews();
        if (s.summary != null) {
            for (OrderCalculator.Line line : s.summary.lines) addSummaryLine(line.label, Money.format(line.cents), false);
            addSummaryLine(getString(R.string.total), Money.format(s.summary.totalCents), true);
        }
        b.book.setEnabled(s.canBook());
        b.book.setText(s.booking ? getString(R.string.booking)
                : s.summary == null ? getString(R.string.book)
                : getString(R.string.book) + " · " + Money.format(s.summary.totalCents));
    }

    private void addSummaryLine(String label, String amount, boolean bold) {
        ItemSummaryLineBinding line = ItemSummaryLineBinding.inflate(getLayoutInflater(), b.summary, false);
        line.label.setText(label);
        line.amount.setText(amount);
        if (bold) {
            line.label.setTypeface(line.label.getTypeface(), android.graphics.Typeface.BOLD);
            line.amount.setTypeface(line.amount.getTypeface(), android.graphics.Typeface.BOLD);
        }
        b.summary.addView(line.getRoot());
    }
}
