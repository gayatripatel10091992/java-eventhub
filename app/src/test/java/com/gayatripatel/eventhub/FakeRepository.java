package com.gayatripatel.eventhub;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.gayatripatel.eventhub.core.Event;
import com.gayatripatel.eventhub.core.OrderCalculator;
import com.gayatripatel.eventhub.core.SampleEvents;
import com.gayatripatel.eventhub.core.Ticket;
import com.gayatripatel.eventhub.core.TicketTier;
import com.gayatripatel.eventhub.data.EventRepository;
import com.gayatripatel.eventhub.data.Resource;
import java.util.ArrayList;
import java.util.List;

/** Synchronous in-memory repository for UI tests. */
public final class FakeRepository implements EventRepository {
    private final MutableLiveData<Resource<List<Event>>> events = new MutableLiveData<>();
    private final MutableLiveData<List<Ticket>> tickets = new MutableLiveData<>(new ArrayList<>());
    private final List<Event> data = SampleEvents.events(SampleEvents.FIXED_NOW);

    public FakeRepository() {
        events.setValue(Resource.success(data));
    }

    @Override
    public LiveData<Resource<List<Event>>> events() {
        return events;
    }

    @Override
    public void refresh() {
        events.setValue(Resource.success(data));
    }

    @Override
    public LiveData<List<Ticket>> tickets() {
        return tickets;
    }

    @Override
    public void book(Event event, TicketTier tier, int quantity, String holderName, Callback<Ticket> callback) {
        Ticket t = new Ticket("EVH-" + (7000 + tickets.getValue().size()), event.id, event.title, event.startsAtMillis,
                event.venue, tier.name, quantity, OrderCalculator.price(tier, quantity).totalCents, holderName);
        List<Ticket> next = new ArrayList<>(tickets.getValue());
        next.add(t);
        tickets.setValue(next);
        callback.onSuccess(t);
    }

    public void seedTickets() {
        List<Ticket> list = new ArrayList<>();
        Event jazz = data.get(0);
        Event run = data.get(3);
        list.add(new Ticket("EVH-7Q2K9", jazz.id, jazz.title, jazz.startsAtMillis, jazz.venue, "Table for two", 2,
                OrderCalculator.price(jazz.tier("table"), 2).totalCents, "Gayatri"));
        list.add(new Ticket("EVH-M4XRT", run.id, run.title, run.startsAtMillis, run.venue, "10K entry", 1,
                OrderCalculator.price(run.tier("10k"), 1).totalCents, "Gayatri"));
        tickets.setValue(list);
    }
}
