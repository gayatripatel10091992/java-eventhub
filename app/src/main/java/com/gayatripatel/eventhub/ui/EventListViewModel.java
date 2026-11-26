package com.gayatripatel.eventhub.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.gayatripatel.eventhub.core.Category;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.EventFilter;
import com.gayatripatel.eventhub.data.Clock;
import com.gayatripatel.eventhub.data.EventRepository;
import com.gayatripatel.eventhub.data.Resource;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public final class EventListViewModel extends ViewModel {
    private final EventRepository repository;
    private final Clock clock;
    private final ZoneId zone;
    private final MutableLiveData<EventFilter> filter = new MutableLiveData<>(EventFilter.none());
    private final LiveData<Resource<List<Event>>> source;
    private final MediatorLiveData<List<Event>> visible = new MediatorLiveData<>();

    EventListViewModel(EventRepository repository, Clock clock, ZoneId zone) {
        this.repository = repository;
        this.clock = clock;
        this.zone = zone;
        this.source = repository.events();
        visible.addSource(source, r -> recompute());
        visible.addSource(filter, f -> recompute());
    }

    private void recompute() {
        Resource<List<Event>> r = source.getValue();
        EventFilter f = filter.getValue();
        List<Event> all = r == null || r.data == null ? Collections.<Event>emptyList() : r.data;
        visible.setValue(f == null ? all : f.apply(all, clock.now(), zone));
    }

    public LiveData<List<Event>> visibleEvents() {
        return visible;
    }

    public LiveData<Resource<List<Event>>> status() {
        return source;
    }

    public ZoneId zone() {
        return zone;
    }

    public void refresh() {
        repository.refresh();
    }

    public void setQuery(String query) {
        EventFilter f = filter.getValue();
        if (f != null && !f.query.equals(query == null ? "" : query.trim())) filter.setValue(f.withQuery(query));
    }

    public void setCategory(Category category) {
        EventFilter f = filter.getValue();
        if (f != null && f.category != category) filter.setValue(f.withCategory(category));
    }
}
