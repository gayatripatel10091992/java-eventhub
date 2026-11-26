package com.gayatripatel.eventhub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.gayatripatel.eventhub.R;
import com.gayatripatel.eventhub.core.Category;
import com.gayatripatel.eventhub.data.Resource;
import com.gayatripatel.eventhub.databinding.ActivityEventListBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

public class EventListActivity extends AppCompatActivity {
    private ActivityEventListBinding b;
    private EventListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEventListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        vm = new ViewModelProvider(this, new AppViewModelFactory(this, null)).get(EventListViewModel.class);

        b.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_tickets) {
                startActivity(new Intent(this, TicketsActivity.class));
                return true;
            }
            return false;
        });

        EventAdapter adapter = new EventAdapter(vm.zone(), event -> startActivity(EventDetailActivity.intent(this, event.id)));
        b.eventsList.setLayoutManager(new LinearLayoutManager(this));
        b.eventsList.setAdapter(adapter);

        addChip(getString(R.string.all), null, true);
        for (Category c : Category.values()) addChip(c.label, c, false);
        b.categoryChips.setOnCheckedStateChangeListener((group, ids) -> {
            if (ids.isEmpty()) return;
            Object tag = group.findViewById(ids.get(0)).getTag();
            vm.setCategory(tag instanceof Category ? (Category) tag : null);
        });

        b.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                vm.setQuery(s.toString());
            }
        });

        b.swipeRefresh.setOnRefreshListener(vm::refresh);
        vm.visibleEvents().observe(this, events -> {
            adapter.submitList(events);
            boolean loaded = vm.status().getValue() != null && vm.status().getValue().status != Resource.Status.LOADING;
            b.emptyView.setVisibility(events.isEmpty() && loaded ? View.VISIBLE : View.GONE);
        });
        vm.status().observe(this, r -> {
            b.swipeRefresh.setRefreshing(r.status == Resource.Status.LOADING);
            if (r.status == Resource.Status.ERROR) Snackbar.make(b.getRoot(), r.message, Snackbar.LENGTH_LONG).show();
        });
    }

    private void addChip(String label, Category category, boolean checked) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_filter, b.categoryChips, false);
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setTag(category);
        b.categoryChips.addView(chip);
        if (checked) b.categoryChips.check(chip.getId());
    }
}
